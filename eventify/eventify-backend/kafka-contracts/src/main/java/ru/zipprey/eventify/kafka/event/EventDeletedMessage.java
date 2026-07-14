package ru.zipprey.eventify.kafka.event;

import java.time.Instant;

public record EventDeletedMessage(
        Long eventId,
        String eventTitle,
        Instant eventDateTime
) {
}