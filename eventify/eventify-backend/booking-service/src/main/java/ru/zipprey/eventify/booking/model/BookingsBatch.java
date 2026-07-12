package ru.zipprey.eventify.booking.model;

import ru.zipprey.eventify.booking.model.entity.Booking;

import java.util.List;

public record BookingsBatch(List<Booking> bookings, int totalCount) {
}
