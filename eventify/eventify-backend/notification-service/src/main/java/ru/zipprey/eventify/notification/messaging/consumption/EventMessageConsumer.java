package ru.zipprey.eventify.notification.messaging.consumption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.booking.BookingsCascadeCanceledMessage;
import ru.zipprey.eventify.kafka.booking.BookingsOutcompetedMessage;
import ru.zipprey.eventify.kafka.booking.CanceledBookingEntry;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.notification.messaging.consumption.deduplicate.KafkaDeduplicateService;

import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventMessageConsumer {

    private static final String EVENT_CREATED = "event.created";
    private static final String EVENT_DATE_CHANGED = "event.date-changed";
    private static final String BOOKING_FORCE_CANCELED = "booking.force-canceled";
    private static final String BOOKING_FORCE_CANCELLATION_ERROR = "booking.force-cancellation-error";
    private static final String BOOKING_CASCADE_CANCELED = "booking.canceled-cascade";

    private static final String DUPLICATE_LOG = "Дубль {}: eventId={}, пропуск операции";

    private final KafkaDeduplicateService service;

    @KafkaListener(topics = EVENT_CREATED, groupId = "notification-service-event-created")
    public void handle(EventCreatedMessage message) {
        logMessage(EVENT_CREATED, message);

        var eventId = message.eventId();
        if (service.isDuplicate(EVENT_CREATED, eventId)) {
            logDuplicate(EVENT_CREATED, eventId);
            return;
        }

        notifyTelegram(message);
        notifyEmail(message);
    }

    @KafkaListener(topics = EVENT_DATE_CHANGED, groupId = "notification-service-event-date-changed")
    public void handleDateChanged(EventDateChangedMessage message) {
        logMessage(EVENT_DATE_CHANGED, message);

        notifyTelegram(message);
        notifyEmail(message);
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
        notifyEmail(message);
    }

    @KafkaListener(topics = BOOKING_CASCADE_CANCELED, groupId = "notification-service-booking-cascade-canceled")
    public void handleBookingCascadeCanceled(BookingsCascadeCanceledMessage message) {
        logMessage(BOOKING_CASCADE_CANCELED, message);

        var eventId = message.eventId();
        if (service.isDuplicate(BOOKING_CASCADE_CANCELED, eventId)) {
            logDuplicate(BOOKING_CASCADE_CANCELED, eventId);
            return;
        }
        //TODO: отправка
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

    private void notifyEmail(EventCreatedMessage message) {
        // TODO: отправка письма
        log.info("Отправляю уведомление о новом событии по email: {}", message);
    }

    private void notifyEmail(EventDateChangedMessage message) {
        // TODO: отправка письма
        log.info("Отправляю уведомление об изменении даты по email: {}", message);
    }

    private void notifyEmail(BookingsOutcompetedMessage message) {
        // TODO: отправка письма
        log.info("Отправляю уведомление об отмене брони из-за " +
                "уменьшения количества мест по email: {}", emails(message));
    }

    private String emails(BookingsOutcompetedMessage message) {
        return message.bookings().stream()
                .map(CanceledBookingEntry::customerEmail)
                .collect(Collectors.joining(" "));
    }
}
