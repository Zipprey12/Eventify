package ru.zipprey.eventify.booking.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.zipprey.eventify.booking.exception.EventServiceUnavailableException;
import ru.zipprey.eventify.eventapi.exception.EventNotFoundException;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.util.List;

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

    private WebClient.ResponseSpec addDefaultHandlers(long eventId, WebClient.ResponseSpec responseSpec) {
        return responseSpec
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new EventNotFoundException(eventId)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new EventServiceUnavailableException(eventId)));
    }
}