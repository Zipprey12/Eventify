package ru.zipprey.eventify.booking.service.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.booking.exception.BookingExpiredException;
import ru.zipprey.eventify.booking.exception.BookingNotFoundException;
import ru.zipprey.eventify.booking.mapper.BookingMapper;
import ru.zipprey.eventify.booking.model.dto.BookingResponse;
import ru.zipprey.eventify.booking.model.entity.Booking;
import ru.zipprey.eventify.booking.repository.BookingRepository;
import ru.zipprey.eventify.booking.service.booking.expiration.ExpiryQueueService;
import ru.zipprey.eventify.booking.service.event.EventsServiceCaller;
import ru.zipprey.eventify.eventapi.model.EventDto;
import ru.zipprey.eventify.kafka.booking.BookTicketsMessage;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedByAdminMessage;
import ru.zipprey.eventify.outbox.OutboxStatus;
import ru.zipprey.eventify.outbox.entity.OutboxEvent;
import ru.zipprey.eventify.outbox.service.OutboxDataService;
import ru.zipprey.eventify.outbox.service.OutboxEventFactory;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static ru.zipprey.eventify.kafka.booking.Topics.*;

@Service
@RequiredArgsConstructor
public class AdminBookingServiceImpl implements AdminBookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final EventsServiceCaller caller;
    private final OutboxDataService outboxDataService;
    private final OutboxEventFactory eventFactory;
    private final ExpiryQueueService queueService;

    //todo написать сервис с redis, который будет автоматически отменять просроченные заявки
    @Override
    @Transactional
    public void confirm(long id) {
        var existed = validateAndGetBooking(id);

        existed.setConfirmed(true);
        repository.save(existed);
        queueService.remove(existed.getId());

        var bookTicketsEvent = eventFactory.create(
                BOOK_TICKETS.getTopic(),
                String.valueOf(existed.getId()),
                new BookTicketsMessage(id,
                        existed.getEventId(),
                        existed.getTicketsCount()),
                OutboxStatus.READY
        );

        var notificationEvent = eventFactory.create(
                CONFIRMED.getTopic(),
                String.valueOf(existed.getId()),
                new BookingConfirmedMessage(
                        id,
                        existed.getEventId(),
                        existed.getCustomerEmail(),
                        null,
                        null,
                        existed.getTicketsCount()
                ),
                OutboxStatus.PENDING_ENRICHMENT
        );
        saveOutboxEvents(bookTicketsEvent, notificationEvent);
    }

    @Override
    public Page<BookingResponse> getAll(Long eventId, boolean unconfirmedOnly, Pageable pageable) {
        var found = repository.findAllFiltered(eventId, unconfirmedOnly, pageable);

        var eventIds = found.getContent().stream()
                .map(Booking::getEventId)
                .distinct()
                .toList();

        var events = caller.findByIds(eventIds)
                .collectMap(EventDto::getId, Function.identity())
                .block();

        return found.map(booking -> {
            var response = mapper.toResponse(booking);
            response.setEvent(events.getOrDefault(booking.getEventId(), EventDto.deleted(booking.getEventId())));
            return response;
        });
    }

    @Override
    @Transactional
    public void delete(long bookingId) {
        var found = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        repository.deleteById(bookingId);
        queueService.remove(bookingId);

        var payload = new BookingDeletedByAdminMessage(
                found.getId(),
                found.getEventId(),
                found.getCustomerEmail(),
                null,
                null,
                found.getTicketsCount(),
                found.getConfirmed()
        );

        var event = eventFactory.create(
                DELETED_BY_ADMIN.getTopic(),
                String.valueOf(found.getId()),
                payload,
                OutboxStatus.PENDING_ENRICHMENT
        );
        outboxDataService.add(event);
    }

    @Override
    public List<Booking> cancelAllRelatedEvent(long eventId) {
        var deleted = repository.deleteAllByEventId(eventId);
        queueService.removeAll(deleted.stream()
                .map(Booking::getId)
                .toList());
        return deleted;
    }

    private Booking validateAndGetBooking(long id) {
        var existed = repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        var expiryTime = existed.getExpiryTime();
        if (expiryTime == null || expiryTime.isBefore(Instant.now())) {
            throw new BookingExpiredException(id);
        }
        return existed;
    }

    private void saveOutboxEvents(OutboxEvent... events) {
        for (OutboxEvent event : events) {
            outboxDataService.add(event);
        }
    }
}
