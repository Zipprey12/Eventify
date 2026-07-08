package ru.zipprey.eventify.booking.service.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.booking.exception.BookingExpiredException;
import ru.zipprey.eventify.booking.exception.BookingNotFoundException;
import ru.zipprey.eventify.booking.mapper.BookingMapper;
import ru.zipprey.eventify.booking.model.dto.BookingResponse;
import ru.zipprey.eventify.booking.model.entity.Booking;
import ru.zipprey.eventify.booking.repository.BookingRepository;
import ru.zipprey.eventify.booking.service.event.EventsServiceCaller;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminBookingServiceImpl implements AdminBookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final EventsServiceCaller caller;

    //todo написать сервис с redis, который будет автоматически отменять просроченные заявки
    @Override
    public void confirm(long id) {
        var existed = repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (existed.getExpiryTime().isBefore(Instant.now())) {
            throw new BookingExpiredException(id);
        }

        caller.bookTickets(existed.getEventId(), existed.getTicketsCount());

        existed.setConfirmed(true);
        repository.save(existed);
    }

    @Override
    public Page<BookingResponse> getAll(Long eventId, boolean unconfirmedOnly, Pageable pageable) {
        var found = repository.findAllFiltered(eventId, unconfirmedOnly, pageable);

        var eventIds = found.getContent().stream()
                .map(Booking::getEventId)
                .distinct()
                .toList();

        var events = caller.findByIds(eventIds);

        return found.map(booking -> {
            var response = mapper.toResponse(booking);
            response.setEvent(events.getOrDefault(booking.getEventId(), EventDto.deleted(booking.getEventId())));
            return response;
        });
    }

    @Override
    public void delete(long bookingId) {
        var found = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (found.getConfirmed()) {
            caller.freeUpPlaces(found.getEventId(), found.getTicketsCount());
        }
        repository.deleteById(bookingId);
    }
}
