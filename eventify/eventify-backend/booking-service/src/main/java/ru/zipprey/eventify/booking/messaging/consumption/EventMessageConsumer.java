package ru.zipprey.eventify.booking.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.booking.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.booking.model.entity.Booking;
import ru.zipprey.eventify.booking.service.booking.AdminBookingService;
import ru.zipprey.eventify.booking.service.booking.cancellation.BookingCancellationService;
import ru.zipprey.eventify.kafka.booking.BookingsCascadeCanceledMessage;
import ru.zipprey.eventify.kafka.booking.BookingsOutcompetedMessage;
import ru.zipprey.eventify.kafka.booking.CanceledBookingEntry;
import ru.zipprey.eventify.kafka.booking.CancellationFailedMessage;
import ru.zipprey.eventify.kafka.event.EventDeletedMessage;
import ru.zipprey.eventify.kafka.event.EventOverbookedMessage;
import ru.zipprey.eventify.outbox.OutboxStatus;
import ru.zipprey.eventify.outbox.entity.OutboxEvent;
import ru.zipprey.eventify.outbox.service.OutboxDataService;
import ru.zipprey.eventify.outbox.service.OutboxEventFactory;

import java.util.List;

import static ru.zipprey.eventify.kafka.booking.Topics.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventMessageConsumer {

    private static final String EVENT_OVERBOOKED = "event.overbooked";
    private static final String EVENT_DELETED = "event.deleted";

    private static final String DUPLICATE_LOG = "Дубль {}: operationId={},  eventId={}, пропуск операции";

    private final KafkaDeduplicateService deduplicateService;
    private final BookingCancellationService cancellationService;
    private final AdminBookingService bookingService;
    private final OutboxDataService outboxDataService;
    private final OutboxEventFactory eventFactory;

    @Transactional
    @KafkaListener(topics = EVENT_OVERBOOKED, groupId = "booking-service-event-overbooked")
    public void handle(EventOverbookedMessage message) {
        log.info("Получено {} : {}", EVENT_OVERBOOKED, message);

        var operationId = message.operationId();
        var eventId = message.eventId();
        if (deduplicateService.isDuplicate(EVENT_OVERBOOKED, operationId)) {
            log.info(DUPLICATE_LOG, EVENT_OVERBOOKED, message.eventId(), operationId);
            return;
        }

        var canceledOpt = cancellationService.freeUp(eventId, message.difference());
        OutboxEvent event;
        if (canceledOpt.isPresent()) {
            var canceled = canceledOpt.get();
            var mapped = toMessageList(canceled.bookings());
            var publishing = new BookingsOutcompetedMessage(
                    message.eventId(),
                    message.operationId(),
                    message.difference(),
                    canceled.totalCount(),
                    mapped,
                    message.eventTitle()
            );

            event = eventFactory.create(
                    FORCE_CANCELED.getTopic(),
                    String.valueOf(eventId),
                    publishing,
                    OutboxStatus.READY
            );

        } else {
            var publishing = new CancellationFailedMessage(
                    operationId,
                    eventId,
                    message.difference()
            );

            event = eventFactory.create(
                    FORCE_CANCELED_ERROR.getTopic(),
                    String.valueOf(eventId),
                    publishing,
                    OutboxStatus.READY
            );
        }

        outboxDataService.add(event);
    }

    @Transactional
    @KafkaListener(topics = EVENT_DELETED, groupId = "booking-service-event-deleted")
    public void handle(EventDeletedMessage message) {
        var eventId = message.eventId();

        if (deduplicateService.isDuplicate(EVENT_DELETED, eventId)) {
            log.info("Дубль {}: eventId={}, пропуск операции", EVENT_DELETED, eventId);
            return;
        }

        var canceled = bookingService.cancelAllRelatedEvent(eventId);
        var publishing = new BookingsCascadeCanceledMessage(
                eventId,
                toMessageList(canceled),
                message.eventTitle()
        );

        var event = eventFactory.create(
                CASCADE_DELETED.getTopic(),
                String.valueOf(publishing.eventId()),
                publishing,
                OutboxStatus.READY
        );

        outboxDataService.add(event);
    }

    private List<CanceledBookingEntry> toMessageList(List<Booking> bookings) {
        return bookings.stream()
                .map(b -> new CanceledBookingEntry(
                        b.getId(),
                        b.getCustomerEmail(),
                        b.getTicketsCount()
                ))
                .toList();
    }
}