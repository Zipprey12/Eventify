package ru.zipprey.eventify.kafka.booking;

public record BookTicketsMessage(
        Long bookingId,
        Long eventId,
        Integer ticketsCount
) {
}
