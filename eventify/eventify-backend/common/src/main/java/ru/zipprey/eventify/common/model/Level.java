package ru.zipprey.eventify.common.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Level {

    ERROR("error"),
    WARN("warn"),
    INFO("info");

    @JsonValue
    private final String key;
}
