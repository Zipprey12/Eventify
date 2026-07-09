package ru.zipprey.eventify.internalsecurity;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@UtilityClass
public class InternalApiKeyExchangeFilter {

    public static ExchangeFilterFunction withKey(String key) {
        return ExchangeFilterFunction.ofRequestProcessor(request ->
                Mono.just(ClientRequest.from(request)
                        .header(InternalServiceKeysProperties.HEADER_NAME, key)
                        .build()));
    }

}
