package ru.zipprey.eventify.event.messaging.consumption.deduplicate;

public interface KafkaDeduplicateService {

    boolean isDuplicate(String topic, Object key);
}
