package ru.zipprey.eventify.booking.messaging.consumption.deduplicate;

public interface KafkaDeduplicateService {

    boolean isDuplicate(String topic, Object key);

}
