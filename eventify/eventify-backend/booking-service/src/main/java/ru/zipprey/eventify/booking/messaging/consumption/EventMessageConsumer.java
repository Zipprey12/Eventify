package ru.zipprey.eventify.booking.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.booking.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.booking.messaging.production.BookingMessageProducer;
import ru.zipprey.eventify.booking.model.entity.Booking;
import ru.zipprey.eventify.booking.service.booking.cancellation.BookingCancellationService;
import ru.zipprey.eventify.kafka.booking.BookingsCanceledMessage;
import ru.zipprey.eventify.kafka.booking.CanceledBookingEntry;
import ru.zipprey.eventify.kafka.booking.CancellationFailedMessage;
import ru.zipprey.eventify.kafka.event.EventOverbookedMessage;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventMessageConsumer {

    private static final String EVENT_OVERBOOKED = "event.overbooked";
    private static final String DUPLICATE_LOG = "Дубль {}: operationId={},  eventId={}, пропуск операции";

    private final KafkaDeduplicateService deduplicateService;
    private final BookingCancellationService cancellationService;
    private final BookingMessageProducer producer;

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
        if (canceledOpt.isPresent()) {
            var canceled = canceledOpt.get();
            var mapped = toMessageList(canceled.bookings());
            producer.publish(
                    new BookingsCanceledMessage(
                            message.eventId(),
                            message.operationId(),
                            message.difference(),
                            canceled.totalCount(),
                            mapped
                    ));
        } else {
            var errorMessage = new CancellationFailedMessage(
                    operationId,
                    eventId,
                    message.difference()
            );
            producer.publish(errorMessage);
        }
    }

    private List<CanceledBookingEntry> toMessageList(List<Booking> bookings) {
        return bookings.stream()
                .map(b -> new CanceledBookingEntry(
                        b.getId(),
                        b.getEventId(),
                        b.getCustomerEmail(),
                        b.getTicketsCount()
                ))
                .toList();
    }
}
