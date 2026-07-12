package ru.zipprey.eventify.kafka.booking;

public record CanceledBookingEntry(
        Long bookingId,
        Long eventId,
        String customerEmail,
        Integer ticketsCount
) {
}
