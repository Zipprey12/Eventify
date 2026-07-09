package ru.zipprey.eventify.booking.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.zipprey.eventify.booking.exception.EventServiceUnavailableException;
import ru.zipprey.eventify.eventapi.exception.EventNotFoundException;
import ru.zipprey.eventify.eventapi.exception.NotEnoughTicketsException;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceCallerImpl implements EventsServiceCaller {

    private final WebClient webClient;

    @Override
    public Mono<EventDto> findById(long id) {
        var spec = webClient.get()
                .uri("/events/{id}", id)
                .retrieve();

        return addDefaultHandlers(id, spec)
                .bodyToMono(EventDto.class);
    }

    @Override
    public Flux<EventDto> findByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return Flux.just();
        }

        return webClient.post()
                .uri("/events/batch")
                .bodyValue(ids)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        response ->
                                Mono.error(new EventServiceUnavailableException(ids.getFirst())))
                .bodyToFlux(EventDto.class);
    }

    @Override
    public Mono<Void> bookTickets(long eventId, int count) {
        var spec = webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/events/{id}/book")
                        .queryParam("count", count)
                        .build(eventId))
                .retrieve();

        return addDefaultHandlers(eventId, spec)
                .onStatus(status -> status.value() == 409,
                        response -> Mono.error(new NotEnoughTicketsException(String.valueOf(eventId), 0)))
                .toBodilessEntity()
                .then();
    }

    @Override
    public Mono<Void> freeUpPlaces(long eventId, int count) {
        var spec = webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/events/{id}/free")
                        .queryParam("count", count)
                        .build(eventId))
                .retrieve();

        return addDefaultHandlers(eventId, spec)
                .toBodilessEntity()
                .then();
    }

    private WebClient.ResponseSpec addDefaultHandlers(long eventId, WebClient.ResponseSpec responseSpec) {
        return responseSpec
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new EventNotFoundException(eventId)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new EventServiceUnavailableException(eventId)));
    }
}
