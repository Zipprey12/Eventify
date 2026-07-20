package ru.zipprey.eventify.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.zipprey.eventify.internalsecurity.InternalApiKeyExchangeFilter;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient notificationWebClient(
            @Value("${services.notification.base-url}") String baseUrl,
            @Value("${internal.own-key}") String ownKey) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(InternalApiKeyExchangeFilter.withKey(ownKey))
                .build();
    }

    @Bean
    public WebClient bookingWebClient(
            @Value("${services.booking.base-url}") String baseUrl,
            @Value("${internal.own-key}") String ownKey) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(InternalApiKeyExchangeFilter.withKey(ownKey))
                .build();
    }

    @Bean
    public WebClient eventWebClient(
            @Value("${services.event.base-url}") String baseUrl,
            @Value("${internal.own-key}") String ownKey) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(InternalApiKeyExchangeFilter.withKey(ownKey))
                .build();
    }
}