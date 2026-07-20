package ru.zipprey.eventify.booking.model.dto;

public record BookingInfo(Long id,
                          Long eventId,
                          boolean confirmed,
                          int ticketsCount) {
}