package ru.zipprey.eventify.booking.model.dto;

public record ConfirmedBookingInfo(Long bookingId,
                                   Long eventId,
                                   String email,
                                   int ticketsCount) {
}
