package ru.zipprey.eventify.booking.service.booking.expiration;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface ExpiryQueueService {

    void schedule(long bookingId, Instant expiryTime);

    void remove(long bookingId);

    void removeAll(List<Long> bookingIds);

    Set<String> pollDue(Instant instant, long limit);
}
