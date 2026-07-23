package ru.zipprey.eventify.booking.model.dto;

import ru.zipprey.eventify.eventapi.model.EventDto;

import java.time.Instant;

public record BookingResponse(
        Long id,
        EventDto event,
        String customerEmail,
        Integer ticketsCount,
        Instant createdAt,
        Instant expiryTime,
        Boolean confirmed
) {
}
