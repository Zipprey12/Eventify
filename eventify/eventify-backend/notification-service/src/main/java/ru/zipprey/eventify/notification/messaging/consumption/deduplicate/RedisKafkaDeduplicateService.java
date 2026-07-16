package ru.zipprey.eventify.notification.messaging.consumption.deduplicate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisKafkaDeduplicateService implements KafkaDeduplicateService {

    private static final Duration DEDUP_TTL = Duration.ofMinutes(30);
    public static final String DEDUP_PREFIX = "dedup:";

    private final StringRedisTemplate redis;

    @Override
    public boolean isDuplicate(String topic, Long eventId) {
        var key = DEDUP_PREFIX + topic + ":" + eventId;
        var isNew = redis.opsForValue().setIfAbsent(key, "1", DEDUP_TTL);
        return Boolean.FALSE.equals(isNew);
    }

    @Override
    public boolean isDuplicate(String topic, UUID id) {
        var key = DEDUP_PREFIX + topic + ":" + id;
        var isNew = redis.opsForValue().setIfAbsent(key, "1", DEDUP_TTL);
        return Boolean.FALSE.equals(isNew);
    }

    @Override
    public boolean isDuplicate(String topic, String key) {
        var fullKey = DEDUP_PREFIX + topic + ":" + key;
        var isNew = redis.opsForValue().setIfAbsent(fullKey, "1", DEDUP_TTL);
        return Boolean.FALSE.equals(isNew);
    }
}
