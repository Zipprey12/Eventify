package ru.zipprey.eventify.booking.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.booking.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.booking.model.BookingsBatch;
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

import static ru.zipprey.eventify.booking.messaging.consumption.EventMessageConsumer.EVENT_DELETED;
import static ru.zipprey.eventify.booking.messaging.consumption.EventMessageConsumer.EVENT_OVERBOOKED;
import static ru.zipprey.eventify.kafka.booking.Topics.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventMessageHandler {

    private final BookingCancellationService cancellationService;
    private final AdminBookingService bookingService;
    private final OutboxDataService outboxDataService;
    private final OutboxEventFactory eventFactory;

    @Transactional
    public void handle(EventOverbookedMessage message) {
        var event = resolveOverbooking(message);
        outboxDataService.add(event);
    }

    @Transactional
    public void handle(EventDeletedMessage message) {
        var eventId = message.eventId();
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

    private OutboxEvent resolveOverbooking(EventOverbookedMessage message) {
        var canceled = cancellationService
                .freeUp(message.eventId(), message.difference());

        return canceled.isPresent()
                ? buildForceCanceledEvent(message, canceled.get())
                : buildCancellationFailedEvent(message);
    }

    private OutboxEvent buildForceCanceledEvent(EventOverbookedMessage message, BookingsBatch canceled) {
        var publishing = new BookingsOutcompetedMessage(
                message.eventId(),
                message.operationId(),
                message.difference(),
                canceled.totalCount(),
                toMessageList(canceled.bookings()),
                message.eventTitle()
        );

        return eventFactory.create(
                FORCE_CANCELED.getTopic(),
                String.valueOf(message.eventId()),
                publishing,
                OutboxStatus.READY
        );
    }

    private OutboxEvent buildCancellationFailedEvent(EventOverbookedMessage message) {
        var publishing = new CancellationFailedMessage(
                message.operationId(),
                message.eventId(),
                message.difference()
        );

        return eventFactory.create(
                FORCE_CANCELED_ERROR.getTopic(),
                String.valueOf(message.eventId()),
                publishing,
                OutboxStatus.READY
        );
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
