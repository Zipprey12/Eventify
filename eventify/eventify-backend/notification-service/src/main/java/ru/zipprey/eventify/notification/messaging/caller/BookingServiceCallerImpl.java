package ru.zipprey.eventify.notification.messaging.caller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceCallerImpl implements BookingServiceCaller {

    private final WebClient bookingWebClient;

    @Override
    public Mono<List<String>> findCustomerEmailsByEventId(long eventId) {
        return bookingWebClient.get()
                .uri(builder -> builder.path("/internal/bookings/emails")
                        .queryParam("eventId", eventId)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .onErrorResume(e -> {
                    log.error("Не удалось получить почтовые адреса бронирований для события c id={}: {}",
                            eventId, e.getMessage());
                    return Mono.just(List.of());
                });
    }
}
