package ru.zipprey.eventify.booking.model.dto.outbox;

import ru.zipprey.eventify.booking.service.outbox.OutboxPayload;

public record OutboxBookingDeletedPayload(
        Long bookingId,
        Long eventId,
        String customerEmail,
        Integer ticketsCount,
        Boolean wasConfirmed
) implements OutboxPayload {
}