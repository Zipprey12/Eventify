package ru.zipprey.eventify.booking.model.dto;

public record BookingDto(
        Long id,
        Long eventId,
        boolean confirmed,
        int ticketsCount) {
}