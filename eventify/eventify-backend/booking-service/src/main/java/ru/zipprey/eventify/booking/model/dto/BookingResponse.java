package ru.zipprey.eventify.booking.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.zipprey.eventify.eventapi.model.EventDto;

import java.time.Instant;

@Getter
@Setter
public class BookingResponse {

    private Long id;

    private EventDto event;

    private String customerEmail;

    private Integer ticketsCount;

    private Instant createdAt;

    private Instant expiryTime;

    private Boolean confirmed;

    private String timezone;
}
