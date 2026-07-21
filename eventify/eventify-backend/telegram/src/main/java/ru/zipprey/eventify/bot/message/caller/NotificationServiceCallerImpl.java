package ru.zipprey.eventify.bot.message.caller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.zipprey.eventify.bot.exception.TelegramInvalidCodeException;
import ru.zipprey.eventify.bot.exception.TelegramLinkedToAnotherUserException;
import ru.zipprey.eventify.bot.model.dto.LinkRequest;

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
    public String link(long chatId, String code) {
        try {
            return webClient.post()
                    .uri("/internal/telegram/link")
                    .bodyValue(new LinkRequest(chatId, code))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException.BadRequest e) {
            throw new TelegramInvalidCodeException();
        } catch (WebClientResponseException.Conflict e) {
            throw new TelegramLinkedToAnotherUserException();
        }
    }
}