package ru.zipprey.eventify.booking.service.outbox;

public interface OutboxPayload {
    Long eventId();
}
