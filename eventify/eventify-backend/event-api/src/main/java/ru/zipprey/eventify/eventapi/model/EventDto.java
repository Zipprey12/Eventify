package ru.zipprey.eventify.eventapi.model;


import java.time.Instant;

public record EventDto(
        Long id,
        String title,
        String description,
        Instant dateTime,
        Integer totalTickets,
        Integer availableTickets,
        String coverUrl
) {
    public static EventDto deleted(Long id) {
        return new EventDto(
                id,
                "Событие удалено",
                null,
                null,
                0,
                0,
                null);
    }
}
