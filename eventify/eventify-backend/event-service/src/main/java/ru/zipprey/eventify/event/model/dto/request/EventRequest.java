package ru.zipprey.eventify.event.model.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record EventRequest(
        @Size(max = 100) String title,
        String description,
        @NotNull Instant dateTime,
        @Min(1) Integer totalTickets,
        String coverUrl
) {
}
