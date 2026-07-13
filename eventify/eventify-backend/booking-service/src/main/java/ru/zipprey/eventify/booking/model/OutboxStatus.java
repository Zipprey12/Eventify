package ru.zipprey.eventify.booking.model;

public enum OutboxStatus {
    PENDING_ENRICHMENT,
    READY,
    PUBLISHED,
    UNAVAILABLE
}
