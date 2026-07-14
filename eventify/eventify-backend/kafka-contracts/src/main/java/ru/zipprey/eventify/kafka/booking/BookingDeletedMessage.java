package ru.zipprey.eventify.kafka.booking;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingDeletedMessage(
        Long eventId,
        Long bookingId,
        String customerEmail,
        String eventTitle,
        Instant eventDateTime,
        Integer ticketsCount,
        Boolean wasConfirmed
) {
}
