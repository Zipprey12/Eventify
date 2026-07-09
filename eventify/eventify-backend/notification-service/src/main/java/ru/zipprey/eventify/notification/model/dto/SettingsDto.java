package ru.zipprey.eventify.notification.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsDto {

    @NotNull
    private Boolean notifyNewEvents;

    @NotNull
    private Boolean notifyUpcoming;

    @Min(1)
    @Max(24)
    @NotNull
    private Integer notifyBeforeHours;

}
