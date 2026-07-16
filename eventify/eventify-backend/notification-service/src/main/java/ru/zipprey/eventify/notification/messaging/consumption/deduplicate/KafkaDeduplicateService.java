package ru.zipprey.eventify.notification.messaging.consumption.deduplicate;

import java.util.UUID;

public interface KafkaDeduplicateService {
    boolean isDuplicate(String topic, Long eventId);

    boolean isDuplicate(String topic, UUID id);

    boolean isDuplicate(String topic, String key);
}
