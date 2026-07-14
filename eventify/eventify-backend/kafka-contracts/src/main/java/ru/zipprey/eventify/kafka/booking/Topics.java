package ru.zipprey.eventify.kafka.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topics {

    DELETED("booking.canceled"),
    FORCE_CANCELED("booking.force-canceled"),
    FORCE_CANCELED_ERROR("booking.force-cancellation-error"),
    DELETED_BY_ADMIN("booking.deleted-by-admin"),
    CONFIRMED("booking.confirmed"),
    BOOK_TICKETS("booking.book-tickets"),
    CASCADE_DELETED("booking.canceled-cascade");

    private final String topic;

}
