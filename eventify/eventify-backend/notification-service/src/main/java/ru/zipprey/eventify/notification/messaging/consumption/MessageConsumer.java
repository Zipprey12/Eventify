package ru.zipprey.eventify.notification.messaging.consumption;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.zipprey.eventify.notification.messaging.consumption.deduplicate.KafkaDeduplicateService;

@Slf4j
@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class MessageConsumer {

    private static final String DUPLICATE_LOG = "Дубль {}: eventId={}, пропуск операции";

    private final KafkaDeduplicateService deduplicateService;

    protected void logConsumption(String topic, Object message) {
        log.info("Получено {} : {}", topic, message);
    }

    protected void logDuplicate(String topic, Object key) {
        log.info(DUPLICATE_LOG, topic, key);
    }
}
