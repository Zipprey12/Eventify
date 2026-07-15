package ru.zipprey.eventify.kafka.event;

import java.time.Instant;

public record EventDateChangedMessage(
        Long eventId,
        String eventTitle,
        Instant newDateTime
) {
}
