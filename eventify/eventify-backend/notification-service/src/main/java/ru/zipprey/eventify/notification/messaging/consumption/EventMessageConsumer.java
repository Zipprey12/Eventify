package ru.zipprey.eventify.notification.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedByAdminMessage;
import ru.zipprey.eventify.kafka.booking.BookingsCascadeCanceledMessage;
import ru.zipprey.eventify.kafka.booking.BookingsOutcompetedMessage;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.notification.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.notification.service.email.EmailService;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventMessageConsumer {

    private static final String EVENT_CREATED = "event.created";
    private static final String EVENT_DATE_CHANGED = "event.date-changed";
    private static final String BOOKING_FORCE_CANCELED = "booking.force-canceled";
    private static final String BOOKING_CASCADE_CANCELED = "booking.canceled-cascade";
    private static final String BOOKING_CONFIRMED = "booking.confirmed";
    private static final String BOOKING_DELETED_BY_ADMIN = "booking.deleted-by-admin";

    private static final String DUPLICATE_LOG = "Дубль {}: eventId={}, пропуск операции";

    private final KafkaDeduplicateService service;
    private final EmailService emailService;

    @KafkaListener(topics = EVENT_CREATED, groupId = "notification-service-event-created")
    public void handle(EventCreatedMessage message) {
        logMessage(EVENT_CREATED, message);

        var eventId = message.eventId();
        if (service.isDuplicate(EVENT_CREATED, eventId)) {
            logDuplicate(EVENT_CREATED, eventId);
            return;
        }

        notifyTelegram(message);
        emailService.notify(message);
    }

    @KafkaListener(topics = EVENT_DATE_CHANGED, groupId = "notification-service-event-date-changed")
    public void handleDateChanged(EventDateChangedMessage message) {
        logMessage(EVENT_DATE_CHANGED, message);

        notifyTelegram(message);
        emailService.notify(message);
    }

    @KafkaListener(topics = BOOKING_FORCE_CANCELED, groupId = "notification-service-booking-force-canceled")
    public void handleBookingCanceled(BookingsOutcompetedMessage message) {
        logMessage(BOOKING_FORCE_CANCELED, message);

        var id = message.operationId();
        if (service.isDuplicate(BOOKING_FORCE_CANCELED, id)) {
            logDuplicate(BOOKING_FORCE_CANCELED, id);
            return;
        }

        notifyTelegram(message);
        emailService.notify(message);
    }

    @KafkaListener(topics = BOOKING_CASCADE_CANCELED, groupId = "notification-service-booking-cascade-canceled")
    public void handleBookingCascadeCanceled(BookingsCascadeCanceledMessage message) {
        logMessage(BOOKING_CASCADE_CANCELED, message);

        var eventId = message.eventId();
        if (service.isDuplicate(BOOKING_CASCADE_CANCELED, eventId)) {
            logDuplicate(BOOKING_CASCADE_CANCELED, eventId);
            return;
        }
        emailService.notify(message);
        //TODO: отправка
    }

    @KafkaListener(topics = BOOKING_CONFIRMED, groupId = "notification-service-booking-confirmed")
    public void handleBookingConfirmed(BookingConfirmedMessage message) {
        logMessage(BOOKING_CONFIRMED, message);

        var bookingId = message.bookingId();
        if (service.isDuplicate(BOOKING_CONFIRMED, bookingId)) {
            logDuplicate(BOOKING_CONFIRMED, bookingId);
            return;
        }

        emailService.notify(message);
    }

    @KafkaListener(topics = BOOKING_DELETED_BY_ADMIN, groupId = "notification-service-booking-deleted-by-admin")
    public void handleBookingDeletedByAdmin(BookingDeletedByAdminMessage message) {
        logMessage(BOOKING_DELETED_BY_ADMIN, message);

        var bookingId = message.bookingId();
        if (service.isDuplicate(BOOKING_DELETED_BY_ADMIN, bookingId)) {
            logDuplicate(BOOKING_DELETED_BY_ADMIN, bookingId);
            return;
        }

        emailService.notify(message);
    }

    private void logMessage(String topic, Object message) {
        log.info("Получено {} : {}", topic, message);
    }

    private void logDuplicate(String topic, Object key) {
        log.info(DUPLICATE_LOG, topic, key);
    }

    private void notifyTelegram(EventCreatedMessage message) {
        // TODO: отправка через Telegram-бота
        log.info("Отправляю информацию в telegram-service: {}", message);
    }

    private void notifyTelegram(EventDateChangedMessage message) {
        // TODO: отправка через Telegram-бота
        log.info("Отправляю информацию об изменении даты в telegram-service: {}", message);
    }

    private void notifyTelegram(BookingsOutcompetedMessage message) {
        // TODO: отправка через Telegram-бота
        log.info("Отправил информацию в Telegram об отмене брони из-за уменьшения количества мест {}", message);
    }
}
