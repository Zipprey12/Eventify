package ru.zipprey.eventify.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.event.mapper.EventMapper;
import ru.zipprey.eventify.event.model.dto.request.EventRequest;
import ru.zipprey.eventify.event.model.entity.Event;
import ru.zipprey.eventify.event.repository.EventRepository;
import ru.zipprey.eventify.eventapi.exception.EventNotFoundException;
import ru.zipprey.eventify.eventapi.exception.NotEnoughTicketsException;
import ru.zipprey.eventify.eventapi.model.EventDto;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.kafka.event.EventDeletedMessage;
import ru.zipprey.eventify.kafka.event.EventOverbookedMessage;
import ru.zipprey.eventify.kafka.event.Topics;
import ru.zipprey.eventify.outbox.OutboxStatus;
import ru.zipprey.eventify.outbox.service.OutboxDataService;
import ru.zipprey.eventify.outbox.service.OutboxEventFactory;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final EventMapper mapper;
    private final OutboxDataService outboxDataService;
    private final OutboxEventFactory eventFactory;

    @Override
    public EventDto findById(long id) {
        var found = repository.findById(id);
        if (found.isEmpty()) {
            throw new EventNotFoundException(id);
        }
        return mapper.toDto(found.get());
    }

    @Override
    public List<EventDto> findByIds(List<Long> ids) {
        return repository.findAllById(ids).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Page<EventDto> findAll(Pageable pageable, Instant from, Instant to) {
        var found = repository.findAllFiltered(from, to, pageable);
        return found.map(mapper::toDto);
    }

    @Transactional
    @Override
    public EventDto create(EventRequest request) {
        var mapped = mapper.toEntity(request);
        mapped.setAvailableTickets(request.getTotalTickets());

        var saved = repository.save(mapped);
        var outboxEvent = eventFactory.create(
                Topics.CREATED.getTopic(),
                String.valueOf(saved.getId()),
                mapper.toCreateMessage(saved),
                OutboxStatus.READY
        );
        outboxDataService.add(outboxEvent);

        return mapper.toDto(saved);
    }

    @Transactional
    @Override
    public EventDto update(Long id, EventRequest request) {
        var existing = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        var previousDate = existing.getDate();
        mapper.updateEntity(request, existing);

        var requestTotalTickets = request.getTotalTickets();
        var existingTotalTickets = existing.getTotalTickets();

        Integer deficit = null;
        if (requestTotalTickets != null
                && !Objects.equals(requestTotalTickets, existingTotalTickets)) {

            var booked = existingTotalTickets - existing.getAvailableTickets();
            if (booked > requestTotalTickets) {
                deficit = booked - requestTotalTickets;
                existing.setAvailableTickets(0);
            } else {
                var diff = requestTotalTickets - existingTotalTickets;
                existing.setAvailableTickets(existing.getAvailableTickets() + diff);
            }
            existing.setTotalTickets(requestTotalTickets);
        }

        var saved = repository.save(existing);

        if (!previousDate.equals(saved.getDate())) {
            var dateChangedEvent = eventFactory.create(
                    Topics.DATE_CHANGED.getTopic(),
                    String.valueOf(saved.getId()),
                    new EventDateChangedMessage(saved.getId(), saved.getDate()),
                    OutboxStatus.READY
            );
            outboxDataService.add(dateChangedEvent);
        }

        if (deficit != null) {
            resolveTicketsDeficit(saved, deficit);
        }

        return mapper.toDto(saved);
    }

    @Transactional
    @Override
    public void delete(long id) {
        var found = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        repository.deleteById(id);

        var deletedMessage = new EventDeletedMessage(
                found.getId(),
                found.getTitle(),
                found.getDate()
        );

        var outboxEvent = eventFactory.create(
                Topics.EVENT_DELETED.getTopic(),
                String.valueOf(found.getId()),
                deletedMessage,
                OutboxStatus.READY
        );
        outboxDataService.add(outboxEvent);
    }

    @Override
    public EventDto bookTickets(Long id, int count) {
        var event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        if (event.getAvailableTickets() < count) {
            throw new NotEnoughTicketsException(event.getTitle(), event.getAvailableTickets());
        }

        event.setAvailableTickets(event.getAvailableTickets() - count);
        repository.save(event);
        return mapper.toDto(event);
    }

    @Override
    public EventDto freeUpPlaces(Long id, int count) {
        var event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        event.setAvailableTickets(event.getAvailableTickets() + count);
        repository.save(event);
        return mapper.toDto(event);
    }

    @Override
    public void addTotalTickets(Long id, int count) {
        var event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        event.setTotalTickets(event.getTotalTickets() + count);
        repository.save(event);
    }

    private void resolveTicketsDeficit(Event event, int deficit) {
        var message = new EventOverbookedMessage(
                UUID.randomUUID(), event.getId(), deficit, event.getTitle()
        );

        var outboxEvent = eventFactory.create(
                Topics.OVERBOOKED.getTopic(),
                String.valueOf(event.getId()),
                message,
                OutboxStatus.READY
        );
        outboxDataService.add(outboxEvent);
    }
}
