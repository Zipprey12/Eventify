package ru.zipprey.eventify.booking.service.event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.util.List;
import java.util.Map;

public interface EventsServiceCaller {

    Mono<EventDto> findById(long id);

    Flux<EventDto> findByIds(List<Long> ids);

    Mono<Void> bookTickets(long eventId, int count);

    Mono<Void> freeUpPlaces(long eventId, int count);
}
