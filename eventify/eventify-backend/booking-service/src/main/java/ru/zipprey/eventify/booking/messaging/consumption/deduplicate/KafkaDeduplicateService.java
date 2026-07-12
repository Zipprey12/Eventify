package ru.zipprey.eventify.booking.messaging.consumption.deduplicate;

import java.util.UUID;

public interface KafkaDeduplicateService {

    boolean isDuplicate(String topic, UUID operationId);
}
