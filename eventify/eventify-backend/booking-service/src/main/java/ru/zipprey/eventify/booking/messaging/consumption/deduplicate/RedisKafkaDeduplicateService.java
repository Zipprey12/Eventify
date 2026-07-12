package ru.zipprey.eventify.booking.messaging.consumption.deduplicate;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisKafkaDeduplicateService implements KafkaDeduplicateService {

    private static final Duration DEDUP_TTL = Duration.ofMinutes(15);

    private final StringRedisTemplate redis;

    @Override
    public boolean isDuplicate(String topic, UUID operationId) {
        var key = "dedup:" + topic + ":" + operationId.toString();
        var isNew = redis.opsForValue().setIfAbsent(key, "1", DEDUP_TTL);
        return Boolean.FALSE.equals(isNew);
    }
}
