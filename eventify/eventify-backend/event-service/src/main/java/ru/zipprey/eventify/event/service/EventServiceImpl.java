package ru.zipprey.eventify.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.event.exception.CapacityReductionException;
import ru.zipprey.eventify.event.mapper.EventMapper;
import ru.zipprey.eventify.event.messaging.EventMessageProducer;
import ru.zipprey.eventify.event.model.dto.request.EventRequest;
import ru.zipprey.eventify.event.repository.EventRepository;
import ru.zipprey.eventify.eventapi.exception.EventNotFoundException;
import ru.zipprey.eventify.eventapi.exception.NotEnoughTicketsException;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final EventMapper mapper;
    private final EventMessageProducer producer;

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

    @Override
    public EventDto create(EventRequest request) {
        var mapped = mapper.toEntity(request);
        mapped.setAvailableTickets(request.getTotalTickets());

        var saved = repository.save(mapped);
        producer.publish(mapper.toCreateMessage(saved));

        return mapper.toDto(saved);
    }

    @Override
    public EventDto update(Long id, EventRequest request) {
        var existing = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        var previousDate = existing.getDate();
        mapper.updateEntity(request, existing);

        if (request.getTotalTickets() != null
                && !Objects.equals(request.getTotalTickets(), existing.getTotalTickets())) {

            var booked = existing.getTotalTickets() - existing.getAvailableTickets();
            if (booked > request.getTotalTickets()) {
                // TODO: обращение к bookingService для отката последних броней. Временно выбрасывается исключение
                throw new CapacityReductionException(booked);
            }

            var diff = request.getTotalTickets() - existing.getTotalTickets();
            existing.setTotalTickets(request.getTotalTickets());
            existing.setAvailableTickets(existing.getAvailableTickets() + diff);
        }

        var saved = repository.save(existing);
        if(!previousDate.equals(existing.getDate())){
            producer.publish(mapper.toDateChangedMessage(saved));
        }
        return mapper.toDto(saved);
    }

    @Override
    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        repository.deleteById(id);
    }

    @Override
    public void bookTickets(Long id, int count) {
        var event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        if (event.getAvailableTickets() < count) {
            throw new NotEnoughTicketsException(event.getTitle(), event.getAvailableTickets());
        }

        event.setAvailableTickets(event.getAvailableTickets() - count);
        log.info("{} {}", event.getAvailableTickets(), event);
        repository.save(event);
    }

    @Override
    public void freeUpPlaces(Long id, int count) {
        var event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        event.setAvailableTickets(event.getAvailableTickets() + count);
        repository.save(event);
    }
}
