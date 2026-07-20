package ru.zipprey.eventify.booking.service.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.zipprey.eventify.booking.repository.BookingRepository;
import ru.zipprey.eventify.booking.service.booking.expiration.ExpiryQueueService;
import ru.zipprey.eventify.booking.service.event.EventsServiceCaller;
import ru.zipprey.eventify.eventapi.exception.NotEnoughTicketsException;
import ru.zipprey.eventify.eventapi.model.EventDto;
import ru.zipprey.eventify.kafka.booking.BookingDeletedMessage;
import ru.zipprey.eventify.outbox.OutboxStatus;
import ru.zipprey.eventify.outbox.service.OutboxDataService;
import ru.zipprey.eventify.outbox.service.OutboxEventFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static ru.zipprey.eventify.kafka.booking.Topics.DELETED;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    public static final Duration BOOKING_CONFIRMATION_DURATION = Duration.ofDays(2);

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final EventsServiceCaller eventsCaller;
    private final OutboxEventFactory eventFactory;
    private final OutboxDataService outboxDataService;
    private final ExpiryQueueService expiryQueueService;

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

        if (event.getAvailableTickets() < request.getTicketsCount()) {
            throw new NotEnoughTicketsException(event.getTitle(), event.getAvailableTickets());
        }

        var entity = mapper.toEntity(request, getEmailKey(authentication));
        entity.setExpiryTime(Instant.now().plus(BOOKING_CONFIRMATION_DURATION));

        var saved = repository.save(entity);
        expiryQueueService.schedule(saved.getId(), saved.getExpiryTime());

        return fillEvent(saved, event);
    }

    @Transactional
    @Override
    public void delete(long id, Authentication authentication) {
        var found = findById(id, authentication);

        repository.deleteById(id);
        expiryQueueService.remove(id);

        var payload = new BookingDeletedMessage(
                found.getEventId(),
                found.getId(),
                found.getCustomerEmail(),
                null,
                null,
                found.getTicketsCount(),
                found.getConfirmed()
        );

        var event = eventFactory.create(
                DELETED.getTopic(),
                String.valueOf(found.getId()),
                payload,
                OutboxStatus.PENDING_ENRICHMENT
        );
        outboxDataService.add(event);
    }

    private String getEmailKey(Authentication authentication) {
        return authentication.getName().toLowerCase();
    }

    private String getEmailKey(String email) {
        return email.toLowerCase();
    }

    private BookingResponse fillEvent(Booking booking) {
        var event = safeFindEvent(booking.getEventId());
        var response = mapper.toResponse(booking);
        response.setEvent(event);
        return response;
    }

    private List<BookingResponse> fillEvents(List<Booking> bookings) {
        var eventIds = bookings.stream()
                .map(Booking::getEventId)
                .distinct()
                .toList();

        var events = safeFindEvents(eventIds);

        return bookings.stream()
                .map(b -> {
                    var response = mapper.toResponse(b);
                    var id = response.getEvent().getId();
                    response.setEvent(events.getOrDefault(id, EventDto.deleted(id)));
                    return response;
                })
                .toList();
    }

    private EventDto safeFindEvent(Long eventId) {
        try {
            return eventsCaller.findById(eventId).block();
        } catch (Exception e) {
            log.warn("event-service недоступен для заполнения брони данными события {}: {}", eventId, e.getMessage());
            return EventDto.deleted(eventId);
        }
    }

    private Map<Long, EventDto> safeFindEvents(List<Long> eventIds) {
        try {
            return eventsCaller.findByIds(eventIds)
                    .collectMap(EventDto::getId, Function.identity())
                    .blockOptional()
                    .orElse(Map.of());
        } catch (Exception e) {
            log.warn("event-service недоступен для заполнения броней данными событий: {}", e.getMessage());
            return Map.of();
        }
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