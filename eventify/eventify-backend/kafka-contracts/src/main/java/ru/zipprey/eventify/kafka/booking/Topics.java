package ru.zipprey.eventify.kafka.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topics {

    CANCELED("booking.canceled"),
    FORCE_CANCELED("booking.force-canceled"),
    FORCE_CANCELED_ERROR("booking.force-cancellation-error");

    private final String topic;

}
