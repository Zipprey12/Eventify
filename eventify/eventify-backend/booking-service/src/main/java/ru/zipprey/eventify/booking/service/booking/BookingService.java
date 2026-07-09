package ru.zipprey.eventify.booking.service.booking;

import org.springframework.security.core.Authentication;
import ru.zipprey.eventify.booking.model.dto.BookingResponse;
import ru.zipprey.eventify.booking.model.dto.request.CreateBookingRequest;
import ru.zipprey.eventify.booking.model.dto.request.UpdateBookingRequest;

import java.util.List;

public interface BookingService {

    BookingResponse getById(long id, Authentication authentication);

    List<BookingResponse> getAll(Authentication authentication);

    BookingResponse update(Long id, UpdateBookingRequest request, Authentication authentication);

    BookingResponse create(CreateBookingRequest request, Authentication authentication);

    void delete(long id, Authentication authentication);
}
