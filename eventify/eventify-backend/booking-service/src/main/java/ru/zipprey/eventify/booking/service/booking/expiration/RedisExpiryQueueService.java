package ru.zipprey.eventify.booking.service.booking.expiration;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisExpiryQueueService implements ExpiryQueueService {

    private static final String QUEUE_KEY = "booking:expiry-queue";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void schedule(long bookingId, Instant expiryTime) {
        runAfterCommit(() ->
                redisTemplate.opsForZSet().add(QUEUE_KEY, String.valueOf(bookingId), expiryTime.toEpochMilli()));
    }

    @Override
    public void remove(long bookingId) {
        runAfterCommit(() ->
                redisTemplate.opsForZSet().remove(QUEUE_KEY, String.valueOf(bookingId)));
    }

    @Override
    public void removeAll(List<Long> bookingIds) {
        runAfterCommit(() -> {
                    var zSet = redisTemplate.opsForZSet();
                    bookingIds.forEach(id -> zSet.remove(QUEUE_KEY, String.valueOf(id)));
                }
        );
    }

    @Override
    public Set<String> pollDue(Instant instant, long limit) {
        var result = redisTemplate.opsForZSet()
                .rangeByScore(QUEUE_KEY, 0, instant.toEpochMilli(), 0, limit);
        return result == null ? Set.of() : result;
    }

    private void runAfterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}
