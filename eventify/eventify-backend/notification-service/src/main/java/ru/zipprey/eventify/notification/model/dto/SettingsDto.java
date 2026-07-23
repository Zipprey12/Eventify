package ru.zipprey.eventify.notification.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SettingsDto(
        @NotNull Boolean notifyNewEvents,
        @NotNull Boolean notifyUpcoming,
        @Min(1) @Max(24) @NotNull Integer notifyBeforeHours,
        Boolean emailConfirmed
) {
}
