package ru.zipprey.eventify.notification.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventMessageConsumer {

    private static final String EVENT_CREATED = "event.created";
    private static final String EVENT_DATE_CHANGED = "event.date-changed";
    private static final String DUPLICATE_LOG = "Дубль {}: eventId={}, пропуск операции";

    private final KafkaDeduplicationService service;

    @KafkaListener(topics = EVENT_CREATED, groupId = "notification-service-event-created")
    public void handle(EventCreatedMessage message) {
        log.info("Получено event.created: {}", message);

        var eventId = message.eventId();
        if (service.isDuplicate(EVENT_CREATED, eventId)) {
            log.info(DUPLICATE_LOG, EVENT_CREATED, eventId);
            return;
        }

        notifyTelegram(message);
        notifyEmail(message);
    }

    @KafkaListener(topics = EVENT_DATE_CHANGED, groupId = "notification-service-event-date-changed")
    public void handleDateChanged(EventDateChangedMessage message) {
        log.info("Получено event.date-changed: {}", message);

        notifyTelegram(message);
        notifyEmail(message);
    }

    private void notifyTelegram(EventCreatedMessage message) {
        // TODO: отправка через Telegram-бота
        log.info("Отправляю информацию в telegram-service: {}", message);
    }

    private void notifyTelegram(EventDateChangedMessage message) {
        // TODO: отправка через Telegram-бота
        log.info("Отправляю информацию об изменении даты в telegram-service: {}", message);
    }

    private void notifyEmail(EventCreatedMessage message) {
        // TODO: отправка письма
        log.info("Отправляю уведомление о новом событии по email: {}", message);
    }

    private void notifyEmail(EventDateChangedMessage message) {
        // TODO: отправка письма
        log.info("Отправляю уведомление об изменении даты по email: {}", message);
    }
}
