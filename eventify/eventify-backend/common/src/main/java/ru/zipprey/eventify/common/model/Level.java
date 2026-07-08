package ru.zipprey.eventify.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Level {

    ERROR("error"),
    WARN("warn"),
    INFO("info");


    private final String key;
}
