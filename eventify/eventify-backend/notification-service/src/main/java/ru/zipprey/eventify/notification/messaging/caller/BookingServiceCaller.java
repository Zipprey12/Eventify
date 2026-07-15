package ru.zipprey.eventify.notification.messaging.caller;

import reactor.core.publisher.Mono;

import java.util.List;

public interface BookingServiceCaller {

    Mono<List<String>> findCustomerEmailsByEventId(long eventId);
}
