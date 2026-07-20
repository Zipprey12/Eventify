package ru.zipprey.eventify.bot.model.dto;

public record ConfirmedBooking(
        Long bookingId,
        Long eventId,
        String email,
        int ticketsCount) {
}
