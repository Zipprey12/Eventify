package ru.zipprey.eventify.notification.messaging.consumption;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.zipprey.eventify.notification.messaging.consumption.deduplicate.KafkaDeduplicateService;
import ru.zipprey.eventify.notification.service.email.EmailService;

@Slf4j
@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class MessageConsumer {

    private static final String DUPLICATE_LOG = "Дубль {}: eventId={}, пропуск операции";

    private final KafkaDeduplicateService deduplicateService;
    private final EmailService emailService;

    protected void safeExecute(Runnable action, String context) {
        try {
            action.run();
        } catch (Exception e) {
            log.error("Ошибка при работе с напоминаниями ({}): {}", context, e.getMessage());
        }
    }

    protected void logMessage(String topic, Object message) {
        log.info("Получено {} : {}", topic, message);
    }

    protected void logDuplicate(String topic, Object key) {
        log.info(DUPLICATE_LOG, topic, key);
    }
}
