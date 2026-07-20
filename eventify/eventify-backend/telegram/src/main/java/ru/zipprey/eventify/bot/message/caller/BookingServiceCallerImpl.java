package ru.zipprey.eventify.bot.message.caller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.zipprey.eventify.bot.model.dto.BookingResponse;
import ru.zipprey.eventify.bot.model.dto.ConfirmedBooking;

import java.util.List;

@Component
public class BookingServiceCallerImpl implements BookingServiceCaller {

    private final WebClient webClient;

    public BookingServiceCallerImpl(@Qualifier("bookingWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<BookingResponse> getBookingsByEmail(String email) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/bookings")
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .bodyToFlux(BookingResponse.class)
                .collectList()
                .blockOptional()
                .orElse(List.of());
    }

    @Override
    public List<ConfirmedBooking> getConfirmed() {
        return webClient.get()
                .uri("/internal/bookings/confirmed")
                .retrieve()
                .bodyToFlux(ConfirmedBooking.class)
                .collectList()
                .blockOptional()
                .orElse(List.of());
    }
}

