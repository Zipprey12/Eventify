package ru.zipprey.eventify.event.messaging.consumption.deduplicate;

import java.util.UUID;

public interface KafkaDeduplicateService {

    boolean isDuplicate(String topic, UUID operationId);

}
