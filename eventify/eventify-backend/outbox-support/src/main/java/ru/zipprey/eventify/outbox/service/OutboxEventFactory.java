package ru.zipprey.eventify.outbox.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.outbox.OutboxStatus;
import ru.zipprey.eventify.outbox.entity.OutboxEvent;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public final class OutboxEventFactory {

    private final ObjectMapper objectMapper;
    private final OutboxTypeRegistry typeRegistry;

    public OutboxEvent create(String topic, String key, Object payload, OutboxStatus status) {
        return OutboxEvent.builder()
                .topic(topic)
                .key(key)
                .payload(objectMapper.writeValueAsString(payload))
                .payloadType(typeRegistry.aliasFor(payload.getClass()))
                .status(status)
                .build();
    }
}