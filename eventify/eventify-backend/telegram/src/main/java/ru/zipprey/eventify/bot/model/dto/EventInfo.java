package ru.zipprey.eventify.bot.model.dto;

import java.time.Instant;

public record EventInfo(
        Long id,
        String title,
        String description,
        Instant dateTime) {
}