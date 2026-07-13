package ru.zipprey.eventify.kafka.event;

import java.util.UUID;

public record EventOverbookedMessage(
        UUID operationId,
        Long eventId,
        Integer difference,
        String eventTitle
) {
}
