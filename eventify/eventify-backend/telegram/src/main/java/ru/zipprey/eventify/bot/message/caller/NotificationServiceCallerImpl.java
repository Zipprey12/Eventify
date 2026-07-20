package ru.zipprey.eventify.bot.message.caller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.zipprey.eventify.bot.model.dto.LinkRequest;

import java.util.Optional;

@Component
public class NotificationServiceCallerImpl implements NotificationServiceCaller {

    private final WebClient webClient;

    public NotificationServiceCallerImpl(@Qualifier("notificationWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void unlink(long chatId) {
        webClient.delete()
                .uri("/internal/telegram/{chatId}", chatId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public Optional<String> link(long chatId, String code) {
        try {
            var email = webClient.post()
                    .uri("/internal/telegram/link")
                    .bodyValue(new LinkRequest(chatId, code))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return Optional.ofNullable(email);
        } catch (WebClientResponseException.BadRequest e) {
            return Optional.empty();
        }
    }
}