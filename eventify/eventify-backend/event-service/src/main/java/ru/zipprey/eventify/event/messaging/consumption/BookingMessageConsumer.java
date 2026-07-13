package ru.zipprey.eventify.event.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.event.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.event.service.EventService;
import ru.zipprey.eventify.kafka.booking.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingMessageConsumer {

    public static final String DUPLICATE_LOG = "Дубль {}. Пропуск операции: {} для event: {}";
    public static final String ADDITION_MESSAGE = "Не удалось отменить брони. К событию {} добавлено {} билетов";
    public static final String FREE_UP_TICKETS = "Освобождено {} билетов для event: {}";
    private static final String DELETED = "booking.deleted";
    private static final String DELETED_BY_ADMIN = "booking.deleted-by-admin";
    private static final String BOOK_TICKETS = "booking.book-tickets";
    private static final String FORCE_CANCELLATION_ERROR = "booking.force-cancellation-error";
    private static final String FORCE_CANCELLED = "booking.force-canceled";
    private final KafkaDeduplicateService deduplicateService;
    private final EventService eventService;

    @KafkaListener(topics = FORCE_CANCELLATION_ERROR, groupId = "event-service-booking-force-cancellation-error")
    public void handle(CancellationFailedMessage message) {
        logMessageConsumption(FORCE_CANCELLATION_ERROR);

        if (deduplicateService.isDuplicate(FORCE_CANCELLATION_ERROR, message.operationId())) {
            logDuplicate(FORCE_CANCELLATION_ERROR, message.operationId(), message.eventId());
            return;
        }

        var id = message.eventId();
        var count = message.requiredTickets();
        eventService.addTotalTickets(message.eventId(), count);
        log.info(ADDITION_MESSAGE, id, count);
    }

    @KafkaListener(topics = FORCE_CANCELLED, groupId = "event-service-booking-force-canceled")
    public void handle(BookingsCanceledMessage message) {
        logMessageConsumption(FORCE_CANCELLED);

        var operationId = message.operationId();
        var eventId = message.eventId();

        if (deduplicateService.isDuplicate(FORCE_CANCELLED, operationId)) {
            logDuplicate(FORCE_CANCELLED, operationId, eventId);
            return;
        }

        var difference = message.freedTickets() - message.requiredTickets();
        if (difference > 0) {
            eventService.freeUpPlaces(eventId, difference);
            log.info(FREE_UP_TICKETS, difference, eventId);
        }
    }

    @KafkaListener(topics = DELETED, groupId = "event-service-booking-deleted")
    public void handleDeleted(BookingDeletedMessage message) {
        if (!Boolean.TRUE.equals(message.wasConfirmed())) {
            return;
        }
        if (deduplicateService.isDuplicate(DELETED, message.bookingId())) {
            logDuplicate(DELETED, message.bookingId(), message.eventId());
            return;
        }

        log.info("Освобождение мест: eventId={}, count={}", message.eventId(), message.ticketsCount());
        eventService.freeUpPlaces(message.eventId(), message.ticketsCount());
    }

    @KafkaListener(topics = DELETED_BY_ADMIN, groupId = "event-service-booking-deleted-by-admin")
    public void handleDeletedByAdmin(BookingDeletedByAdminMessage message) {
        if (!Boolean.TRUE.equals(message.wasConfirmed())) {
            return;
        }
        if (deduplicateService.isDuplicate(DELETED_BY_ADMIN, message.bookingId())) {
            logDuplicate(DELETED_BY_ADMIN, message.bookingId(), message.eventId());
            return;
        }

        log.info("Освобождение мест места (admin): eventId={}, count={}", message.eventId(), message.ticketsCount());
        eventService.freeUpPlaces(message.eventId(), message.ticketsCount());
    }

    @KafkaListener(topics = BOOK_TICKETS, groupId = "event-service-booking-book-tickets")
    public void handleBookTickets(BookTicketsMessage message) {
        if (deduplicateService.isDuplicate(BOOK_TICKETS, message.bookingId())) {
            logDuplicate(BOOK_TICKETS, message.bookingId(), message.eventId());
            return;
        }

        log.info("Бронирование мест: eventId={}, count={}", message.eventId(), message.ticketsCount());
        eventService.bookTickets(message.eventId(), message.ticketsCount());
    }

    private void logMessageConsumption(String message) {
        log.info("Получено: {}", message);
    }

    private void logDuplicate(String message, Object key, Long eventId) {
        log.info(DUPLICATE_LOG, message, key, eventId);
    }
}