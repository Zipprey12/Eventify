package ru.zipprey.eventify.booking.service.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.zipprey.eventify.booking.model.dto.BookingResponse;
import ru.zipprey.eventify.booking.model.entity.Booking;

import java.util.List;

public interface AdminBookingService {

    void confirm(long id);

    Page<BookingResponse> getAll(Long eventId, boolean unconfirmedOnly, Pageable pageable);

    void delete(long bookingId);

    List<Booking> cancelAllRelatedEvent(long eventId);
}
