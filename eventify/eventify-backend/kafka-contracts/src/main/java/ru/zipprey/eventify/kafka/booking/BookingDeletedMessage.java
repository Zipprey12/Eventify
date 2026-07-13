package ru.zipprey.eventify.kafka.booking;

import java.time.Instant;

public record BookingDeletedMessage(
        Long eventId,
        Long bookingId,
        String customerEmail,
        String eventTitle,
        Instant eventDateTime,
        Integer ticketsCount,
        Boolean wasConfirmed
) {
}
