package ru.zipprey.eventify.booking.model.dto.request;

import jakarta.validation.constraints.Min;

public record CreateBookingRequest(
        Long eventId,
        @Min(1) Integer ticketsCount
) {
}