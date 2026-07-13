package ru.zipprey.eventify.booking.model.outbox;

public enum OutboxStatus {
    PENDING_ENRICHMENT,
    READY,
    PUBLISHED,
    UNAVAILABLE
}
