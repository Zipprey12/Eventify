package ru.zipprey.eventify.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.event.exception.CapacityReductionException;
import ru.zipprey.eventify.event.exception.EventNotFoundException;
import ru.zipprey.eventify.event.mapper.EventMapper;
import ru.zipprey.eventify.event.model.dto.request.EventRequest;
import ru.zipprey.eventify.event.repository.EventRepository;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final EventMapper mapper;

    @Override
    public EventDto findById(long id) {
        var found = repository.findById(id);
        if (found.isEmpty()) {
            throw new EventNotFoundException(id);
        }
        return mapper.toDto(found.get());
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
        return mapper.toDto(repository.save(mapped));
    }

    @Override
    public EventDto update(Long id, EventRequest request) {
        var existing = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

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

        return mapper.toDto(repository.save(existing));
    }

    @Override
    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        repository.deleteById(id);
    }
}
