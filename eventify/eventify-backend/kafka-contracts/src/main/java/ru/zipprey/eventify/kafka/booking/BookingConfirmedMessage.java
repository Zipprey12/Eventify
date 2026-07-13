package ru.zipprey.eventify.kafka.booking;

import java.time.Instant;

public record BookingConfirmedMessage(
        Long bookingId,
        Long eventId,
        String customerEmail,
        String eventTitle,
        Instant eventDateTime,
        Integer ticketsCount) {
}
