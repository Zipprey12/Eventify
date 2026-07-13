package ru.zipprey.eventify.notification.messaging.consumption.deduplicate;

import java.util.UUID;

public interface KafkaDeduplicationService {
    boolean isDuplicate(String topic, Long eventId);

    boolean isDuplicate(String topic, UUID id);
}
