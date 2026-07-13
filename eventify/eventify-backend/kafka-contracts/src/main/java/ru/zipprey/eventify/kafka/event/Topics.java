package ru.zipprey.eventify.kafka.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topics {

    CREATED("event.created"),
    DATE_CHANGED("event.date-changed"),
    OVERBOOKED("event.overbooked"),
    EVENT_DELETED("event.deleted");

    private final String topic;
}
