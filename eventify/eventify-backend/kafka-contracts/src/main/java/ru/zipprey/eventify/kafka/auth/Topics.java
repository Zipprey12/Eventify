package ru.zipprey.eventify.kafka.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topics {
    USER_REGISTERED("user.registered");

    private final String topic;
}
