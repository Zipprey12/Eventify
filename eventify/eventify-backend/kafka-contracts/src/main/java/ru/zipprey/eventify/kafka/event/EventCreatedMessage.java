package ru.zipprey.eventify.kafka.event;

import java.time.Instant;

public record EventCreatedMessage(
        Long eventId,
        String title,
        String description,
        Instant dateTime
) {
}
