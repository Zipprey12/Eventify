package ru.zipprey.eventify.kafka.booking;

import java.time.Instant;

public record BookingDeletedByAdminMessage(
        Long bookingId,
        Long eventId,
        String customerEmail,
        String eventTitle,
        Instant eventDateTime,
        Integer ticketsCount,
        Boolean wasConfirmed
) {
}
