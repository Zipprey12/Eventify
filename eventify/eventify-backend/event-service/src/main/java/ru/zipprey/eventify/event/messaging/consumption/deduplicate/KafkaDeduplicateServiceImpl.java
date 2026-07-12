package ru.zipprey.eventify.event.messaging.consumption.deduplicate;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaDeduplicateServiceImpl implements KafkaDeduplicateService {

    private static final Duration DEDUP_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isDuplicate(String topic, UUID operationId) {
        var key = "dedup:" + topic + ":" + operationId;
        var isNew = redisTemplate.opsForValue().setIfAbsent(key, "1", DEDUP_TTL);
        return Boolean.FALSE.equals(isNew);
    }
}
