package ru.zipprey.eventify.kafka.booking;

import java.util.List;
import java.util.UUID;

public record BookingsCanceledMessage(
        Long eventId,
        UUID operationId,
        Integer requiredTickets,
        Integer freedTickets,
        List<CanceledBookingEntry> bookings,
        String eventTitle
) {
}
