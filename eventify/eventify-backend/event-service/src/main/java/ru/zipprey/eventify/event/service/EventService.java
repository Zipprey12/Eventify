package ru.zipprey.eventify.event.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.zipprey.eventify.event.model.dto.request.EventRequest;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.time.Instant;
import java.util.List;

public interface EventService {

    EventDto findById(long id);

    List<EventDto> findByIds(List<Long> ids);

    Page<EventDto> findAll(Pageable pageable, Instant from, Instant to);

    EventDto create(EventRequest response);

    EventDto update(Long id, EventRequest updateRequest);

    void delete(long id);

    EventDto bookTickets(Long id, int count);

    EventDto freeUpPlaces(Long id, int count);

    void addTotalTickets(Long id, int count);
}
