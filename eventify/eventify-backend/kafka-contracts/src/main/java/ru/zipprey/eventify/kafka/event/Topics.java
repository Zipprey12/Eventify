package ru.zipprey.eventify.kafka.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topics {

    CREATED("event.created"),
    DATE_CHANGED("event.date-changed");

    private final String topic;
}
