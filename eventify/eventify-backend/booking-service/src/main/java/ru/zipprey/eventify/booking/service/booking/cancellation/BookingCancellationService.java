package ru.zipprey.eventify.booking.service.booking.cancellation;

import ru.zipprey.eventify.booking.model.BookingsBatch;

import java.util.Optional;

public interface BookingCancellationService {

    Optional<BookingsBatch> freeUp(Long eventId, int overbooking);
}
