package ru.zipprey.eventify.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record EventDeletedMessage(
        UUID operationId,
        Long eventId,
        String eventTitle,
        Instant eventDateTime
) {
}