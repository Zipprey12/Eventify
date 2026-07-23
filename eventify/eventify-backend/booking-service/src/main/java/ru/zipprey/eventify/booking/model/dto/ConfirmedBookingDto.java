package ru.zipprey.eventify.booking.model.dto;

public record ConfirmedBookingDto(
        Long bookingId,
        Long eventId,
        String email,
        int ticketsCount) {
}
