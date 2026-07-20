package ru.zipprey.eventify.bot.message.caller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.zipprey.eventify.bot.model.dto.EventInfo;

import java.util.List;

@Component
public class EventServiceCallerImpl implements EventServiceCaller {

    private final WebClient webClient;

    public EventServiceCallerImpl(@Qualifier("eventWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<EventInfo> findByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return webClient.post()
                .uri("/events/batch")
                .bodyValue(ids)
                .retrieve()
                .bodyToFlux(EventInfo.class)
                .collectList()
                .blockOptional()
                .orElse(List.of());
    }
}