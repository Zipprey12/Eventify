package ru.zipprey.eventify.notification.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.zipprey.eventify.internalsecurity.InternalApiKeyExchangeFilter;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient bookingServiceWebClient(@Value("${booking-service.base-url}") String baseUrl,
                                             @Value("${internal.secret}") String ownKey) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(InternalApiKeyExchangeFilter.withKey(ownKey))
                .build();
    }
}
