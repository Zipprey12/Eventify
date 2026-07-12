package ru.zipprey.eventify.kafka.booking;

import java.util.UUID;

public record CancellationFailedMessage(
        UUID operationId,
        Long eventId,
        Integer requiredTickets
) {
}
