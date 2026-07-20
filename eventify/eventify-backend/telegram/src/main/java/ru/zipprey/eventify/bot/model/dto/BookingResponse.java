package ru.zipprey.eventify.bot.model.dto;

public record BookingResponse(Long id,
                              Long eventId,
                              boolean confirmed,
                              int ticketsCount) {
}
