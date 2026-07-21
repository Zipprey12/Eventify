package ru.zipprey.eventify.bot.message.consuption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.bot.message.consuption.deduplication.KafkaDeduplicateService;
import ru.zipprey.eventify.bot.service.notification.NotificationService;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedByAdminMessage;
import ru.zipprey.eventify.kafka.booking.BookingsCascadeCanceledMessage;
import ru.zipprey.eventify.kafka.booking.BookingsOutcompetedMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingMessageConsumer {

    private static final String BOOKING_CONFIRMED = "booking.confirmed";
    private static final String BOOKING_DELETED_BY_ADMIN = "booking.deleted-by-admin";
    private static final String BOOKING_CASCADE_CANCELED = "booking.canceled-cascade";
    private static final String BOOKING_FORCE_CANCELED = "booking.force-canceled";
    private static final String DUPLICATE_LOG = "Дубль {}: {}, пропуск";

    private static final String LOG_PATTERN = "Получено {} : eventId={}, броней={}";
    private static final String LOG_MESSAGE_PATTERN = "Получено {} : {}";

    private final NotificationService notificationService;
    private final KafkaDeduplicateService deduplicateService;

    @KafkaListener(topics = BOOKING_CONFIRMED, groupId = "telegram-bot-booking-confirmed")
    public void handleConfirmed(BookingConfirmedMessage message) {
        logConsumption(BOOKING_CONFIRMED, message);

        var bookingId = message.bookingId();
        if (deduplicateService.isDuplicate(BOOKING_CONFIRMED, bookingId)) {
            logDuplicate(BOOKING_CONFIRMED, bookingId);
            return;
        }

        notificationService.notify(message);
    }

    @KafkaListener(topics = BOOKING_DELETED_BY_ADMIN, groupId = "telegram-bot-booking-deleted-by-admin")
    public void handleDeletedByAdmin(BookingDeletedByAdminMessage message) {
        logConsumption(BOOKING_DELETED_BY_ADMIN, message);

        var bookingId = message.bookingId();
        if (deduplicateService.isDuplicate(BOOKING_DELETED_BY_ADMIN, bookingId)) {
            logDuplicate(BOOKING_DELETED_BY_ADMIN, bookingId);
            return;
        }

        notificationService.notify(message);
    }

    @KafkaListener(topics = BOOKING_CASCADE_CANCELED, groupId = "telegram-bot-booking-cascade-canceled")
    public void handleCascadeCanceled(BookingsCascadeCanceledMessage message) {
        logConsumption(BOOKING_CASCADE_CANCELED, message.eventId(), message.bookings().size());

        var eventId = message.eventId();
        if (deduplicateService.isDuplicate(BOOKING_CASCADE_CANCELED, eventId)) {
            logDuplicate(BOOKING_CASCADE_CANCELED, eventId);
            return;
        }

        notificationService.notify(message);
    }

    @KafkaListener(topics = BOOKING_FORCE_CANCELED, groupId = "telegram-bot-booking-force-canceled")
    public void handleForceCanceled(BookingsOutcompetedMessage message) {
        logConsumption(BOOKING_FORCE_CANCELED, message.eventId(), message.bookings().size());

        var operationId = message.operationId();
        if (deduplicateService.isDuplicate(BOOKING_FORCE_CANCELED, operationId)) {
            logDuplicate(BOOKING_FORCE_CANCELED, operationId);
            return;
        }

        notificationService.notify(message);
    }

    private void logConsumption(String topic, Object message) {
        log.info(LOG_MESSAGE_PATTERN, topic, message);
    }

    private void logConsumption(String topic, long eventId, int bookings) {
        log.info(LOG_PATTERN, topic, eventId, bookings);
    }

    private void logDuplicate(String topic, Object key) {
        log.info(DUPLICATE_LOG, topic, key);
    }
}