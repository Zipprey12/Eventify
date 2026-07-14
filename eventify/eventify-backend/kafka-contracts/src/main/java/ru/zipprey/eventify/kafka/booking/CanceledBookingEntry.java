package ru.zipprey.eventify.kafka.booking;

public record CanceledBookingEntry(
        Long bookingId,
        String customerEmail,
        Integer ticketsCount
) {
}
