package ru.zipprey.eventify.bot.message.consuption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
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

    private static final String LOG_PATTERN = "Получено {} : eventId={}, броней={}";
    private static final String LOG_MESSAGE_PATTERN = "Получено {} : {}";

    private final NotificationService notificationService;

    @KafkaListener(topics = BOOKING_CONFIRMED, groupId = "telegram-bot-booking-confirmed")
    public void handleConfirmed(BookingConfirmedMessage message) {
        logConsumption(BOOKING_CONFIRMED, message);
        notificationService.notify(message);
    }

    @KafkaListener(topics = BOOKING_DELETED_BY_ADMIN, groupId = "telegram-bot-booking-deleted-by-admin")
    public void handleDeletedByAdmin(BookingDeletedByAdminMessage message) {
        logConsumption(BOOKING_DELETED_BY_ADMIN, message);
        notificationService.notify(message);
    }

    @KafkaListener(topics = BOOKING_CASCADE_CANCELED, groupId = "telegram-bot-booking-cascade-canceled")
    public void handleCascadeCanceled(BookingsCascadeCanceledMessage message) {
        logConsumption(BOOKING_CASCADE_CANCELED, message.eventId(), message.bookings().size());
        notificationService.notify(message);
    }

    @KafkaListener(topics = BOOKING_FORCE_CANCELED, groupId = "telegram-bot-booking-force-canceled")
    public void handleForceCanceled(BookingsOutcompetedMessage message) {
        logConsumption(BOOKING_FORCE_CANCELED, message.eventId(), message.bookings().size());
        notificationService.notify(message);
    }

    private void logConsumption(String topic, Object message) {
        log.info(LOG_MESSAGE_PATTERN, topic, message);
    }

    private void logConsumption(String topic, long eventId, int bookings) {
        log.info(LOG_PATTERN, topic, eventId, bookings);
    }
}