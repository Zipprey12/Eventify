package ru.zipprey.eventify.event.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.event.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.kafka.booking.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingMessageConsumer {

    public static final String DUPLICATE_LOG = "Дубль {}. Пропуск операции: {} для event: {}";
    private static final String DELETED = "booking.deleted";
    private static final String DELETED_BY_ADMIN = "booking.deleted-by-admin";
    private static final String BOOK_TICKETS = "booking.book-tickets";
    private static final String FORCE_CANCELLATION_ERROR = "booking.force-cancellation-error";
    private static final String FORCE_CANCELLED = "booking.force-canceled";

    private final KafkaDeduplicateService deduplicateService;
    private final BookingMessageHandler handler;

    @KafkaListener(topics = FORCE_CANCELLATION_ERROR, groupId = "event-service-booking-force-cancellation-error")
    public void handle(CancellationFailedMessage message) {
        logConsumption(FORCE_CANCELLATION_ERROR, message);

        if (deduplicateService.isDuplicate(FORCE_CANCELLATION_ERROR, message.operationId())) {
            logDuplicate(FORCE_CANCELLATION_ERROR, message.operationId(), message.eventId());
            return;
        }

        handler.handle(message);
    }

    @KafkaListener(topics = FORCE_CANCELLED, groupId = "event-service-booking-force-canceled")
    public void handle(BookingsOutcompetedMessage message) {
        logConsumption(FORCE_CANCELLED, message);

        if (deduplicateService.isDuplicate(FORCE_CANCELLED, message.operationId())) {
            logDuplicate(FORCE_CANCELLED, message.operationId(), message.eventId());
            return;
        }

        handler.handle(message);
    }

    @KafkaListener(topics = DELETED, groupId = "event-service-booking-deleted")
    public void handleDeleted(BookingDeletedMessage message) {
        logConsumption(DELETED, message);

        if (deduplicateService.isDuplicate(DELETED, message.bookingId())) {
            logDuplicate(DELETED, message.bookingId(), message.eventId());
            return;
        }

        handler.handle(message);
    }

    @KafkaListener(topics = DELETED_BY_ADMIN, groupId = "event-service-booking-deleted-by-admin")
    public void handleDeletedByAdmin(BookingDeletedByAdminMessage message) {
        logConsumption(DELETED_BY_ADMIN, message);

        if (deduplicateService.isDuplicate(DELETED_BY_ADMIN, message.bookingId())) {
            logDuplicate(DELETED_BY_ADMIN, message.bookingId(), message.eventId());
            return;
        }

        handler.handle(message);
    }

    @KafkaListener(topics = BOOK_TICKETS, groupId = "event-service-booking-book-tickets")
    public void handleBookTickets(BookTicketsMessage message) {
        logConsumption(BOOK_TICKETS, message);

        if (deduplicateService.isDuplicate(BOOK_TICKETS, message.bookingId())) {
            logDuplicate(BOOK_TICKETS, message.bookingId(), message.eventId());
            return;
        }

        handler.handle(message);
    }

    private void logDuplicate(String message, Object key, Long eventId) {
        log.info(DUPLICATE_LOG, message, key, eventId);
    }

    private void logConsumption(String topic, Object message) {
        log.info("Получено {} : {}", topic, message);
    }
}
