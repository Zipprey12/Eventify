package ru.zipprey.eventify.bot.message.consuption.deduplication;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisKafkaDeduplicateService implements KafkaDeduplicateService {

    public static final String DEDUP_PREFIX = "dedup:";
    private static final Duration DEDUP_TTL = Duration.ofMinutes(30);
    private final StringRedisTemplate redis;

    @Override
    public boolean isDuplicate(String topic, Object key) {
        var fullKey = DEDUP_PREFIX + topic + ":" + key;
        var isNew = redis.opsForValue().setIfAbsent(fullKey, "1", DEDUP_TTL);
        return Boolean.FALSE.equals(isNew);
    }
}
