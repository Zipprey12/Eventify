package ru.zipprey.eventify.event.model.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class EventRequest {

    @Size(max = 100)
    private String title;

    private String description;

    @NotNull
    private Instant dateTime;

    @Min(1)
    private Integer totalTickets;

    private String coverUrl;
}
