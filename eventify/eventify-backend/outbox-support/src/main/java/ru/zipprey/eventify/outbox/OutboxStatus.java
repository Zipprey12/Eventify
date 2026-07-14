package ru.zipprey.eventify.outbox;

public enum OutboxStatus {
    PENDING_ENRICHMENT,
    READY,
    PUBLISHED,
    UNAVAILABLE
}
