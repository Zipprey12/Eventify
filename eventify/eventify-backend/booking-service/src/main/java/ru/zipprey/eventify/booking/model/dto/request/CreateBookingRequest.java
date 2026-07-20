package ru.zipprey.eventify.booking.model.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CreateBookingRequest {

    private Long eventId;

    @Min(1)
    private Integer ticketsCount;
}
