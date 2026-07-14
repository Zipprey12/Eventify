package ru.zipprey.eventify.kafka.booking;

import java.util.List;

public record BookingsCascadeCanceledMessage(
        Long eventId,
        List<CanceledBookingEntry> bookings,
        String eventTitle
) {
}
