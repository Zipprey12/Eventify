package ru.zipprey.eventify.eventapi.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long id;
    private String title;
    private String description;
    private Instant dateTime;
    private Integer totalTickets;
    private Integer availableTickets;
    private String coverUrl;

    public static EventDto deleted(Long id) {
        return new EventDto(id, "Событие удалено", null, null, 0, 0, null);
    }
}