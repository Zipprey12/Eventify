package ru.zipprey.eventify.notification.messaging;

public interface KafkaDeduplicationService {
    boolean isDuplicate(String topic, Long eventId);
}
