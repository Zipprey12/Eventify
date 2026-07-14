package ru.zipprey.eventify.kafka.booking;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingConfirmedMessage(
        Long bookingId,
        Long eventId,
        String customerEmail,
        String eventTitle,
        Instant eventDateTime,
        Integer ticketsCount) {
}
