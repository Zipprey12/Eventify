package ru.zipprey.eventify.booking.service.event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.util.List;

public interface EventsServiceCaller {

    Mono<EventDto> findById(long id);

    Flux<EventDto> findByIds(List<Long> ids);

    Mono<EventDto> bookTickets(long eventId, int count);

    Mono<EventDto> freeUpPlaces(long eventId, int count);
}
