package ru.zipprey.eventify.booking.service.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.booking.exception.BookingAccessDeniedException;
import ru.zipprey.eventify.booking.exception.BookingNotFoundException;
import ru.zipprey.eventify.booking.mapper.BookingMapper;
import ru.zipprey.eventify.booking.model.dto.BookingResponse;
import ru.zipprey.eventify.booking.model.dto.request.CreateBookingRequest;
import ru.zipprey.eventify.booking.model.dto.request.UpdateBookingRequest;
import ru.zipprey.eventify.booking.model.entity.Booking;
import ru.zipprey.eventify.booking.model.outbox.OutboxStatus;
import ru.zipprey.eventify.booking.repository.BookingRepository;
import ru.zipprey.eventify.booking.service.event.EventsServiceCaller;
import ru.zipprey.eventify.booking.service.outbox.OutboxDataService;
import ru.zipprey.eventify.booking.service.outbox.OutboxEventFactory;
import ru.zipprey.eventify.eventapi.exception.NotEnoughTicketsException;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static ru.zipprey.eventify.kafka.booking.Topics.DELETED;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    public static final Duration BOOKING_CONFIRMATION_DURATION = Duration.ofDays(2);
    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final EventsServiceCaller eventsCaller;
    private final OutboxEventFactory eventFactory;
    private final OutboxDataService outboxDataService;

    @Override
    public BookingResponse getById(long id, Authentication authentication) {
        var found = findById(id, authentication);
        return fillEvent(found);
    }

    @Override
    public List<BookingResponse> getAll(Authentication authentication) {
        var email = getEmailKey(authentication);
        var found = repository.findAllByCustomerEmail(email);

        return fillEvents(found);
    }

    @Override
    public BookingResponse update(Long id, UpdateBookingRequest request, Authentication authentication) {
        var found = findById(id, authentication);
        mapper.updateEntity(request, found);
        return fillEvent(repository.save(found));
    }

    @Override
    public BookingResponse create(CreateBookingRequest request, Authentication authentication) {
        var event = eventsCaller.findById(request.getEventId()).block();

        if (event.getAvailableTickets() < request.getTicketCount()) {
            throw new NotEnoughTicketsException(event.getTitle(), event.getAvailableTickets());
        }

        var entity = mapper.toEntity(request, getEmailKey(authentication));
        entity.setExpiryTime(Instant.now().plus(BOOKING_CONFIRMATION_DURATION));

        var saved = repository.save(entity);
        return fillEvent(saved, event);
    }

    @Transactional
    @Override
    public void delete(long id, Authentication authentication) {
        var found = findById(id, authentication);

        repository.deleteById(id);
        var event = eventFactory.createDelete(
                DELETED.getTopic(),
                found,
                OutboxStatus.PENDING_ENRICHMENT
        );
        outboxDataService.addUnprocessed(event);
    }

    private String getEmailKey(Authentication authentication) {
        return authentication.getName().toLowerCase();
    }

    private String getEmailKey(String email) {
        return email.toLowerCase();
    }

    private BookingResponse fillEvent(Booking booking) {
        var event = eventsCaller.findById(booking.getEventId()).block();
        var response = mapper.toResponse(booking);
        response.setEvent(event);
        return response;
    }

    private List<BookingResponse> fillEvents(List<Booking> bookings) {
        var eventIds = bookings.stream()
                .map(Booking::getEventId)
                .toList();

        var events = eventsCaller.findByIds(eventIds).
                collectMap(EventDto::getId, Function.identity())
                .block();

        return bookings.stream()
                .map(mapper::toResponse)
                .map(r -> {
                    var id = r.getEvent().getId();
                    r.setEvent(events.getOrDefault(id, EventDto.deleted(id)));
                    return r;
                })
                .toList();
    }

    private BookingResponse fillEvent(Booking booking, EventDto event) {
        return mapper.toResponse(booking, event);
    }

    private Booking findById(Long id, Authentication authentication) {
        var found = repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        checkAvailability(found, authentication);
        return found;
    }

    private void checkAvailability(Booking booking, Authentication authentication) {
        var bookingEmail = getEmailKey(booking.getCustomerEmail());
        var authEmail = getEmailKey(authentication);
        if (!bookingEmail.equals(authEmail)) {
            throw new BookingAccessDeniedException(booking.getId());
        }
    }
}

