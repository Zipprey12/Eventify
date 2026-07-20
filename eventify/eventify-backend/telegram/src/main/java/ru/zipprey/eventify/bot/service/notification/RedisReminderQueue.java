package ru.zipprey.eventify.bot.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.bot.model.dto.DueReminder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisReminderQueue implements ReminderQueue {

    private static final String QUEUE_KEY = "reminder:queue";
    private static final String PAYLOAD_KEY_PREFIX = "reminder:payload:";
    private static final String TITLE_FIELD = "title";
    private static final String DESCRIPTION_FIELD = "description";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void add(long bookingId, long chatId, Instant notifyAt, String eventTitle, String eventDescription) {
        redisTemplate.opsForZSet().add(QUEUE_KEY, createMember(bookingId, chatId), notifyAt.getEpochSecond());

        var payloadKey = createPayloadKey(bookingId, chatId);
        redisTemplate.opsForHash().put(payloadKey, TITLE_FIELD, nullToEmpty(eventTitle));
        redisTemplate.opsForHash().put(payloadKey, DESCRIPTION_FIELD, nullToEmpty(eventDescription));
    }

    @Override
    public void remove(long bookingId, long chatId) {
        redisTemplate.opsForZSet().remove(QUEUE_KEY, createMember(bookingId, chatId));
        redisTemplate.delete(createPayloadKey(bookingId, chatId));
    }

    @Override
    public List<DueReminder> dequeueDue(Instant instant, int maxCount) {
        Set<String> due = redisTemplate.opsForZSet()
                .rangeByScore(QUEUE_KEY, 0, instant.getEpochSecond(), 0, maxCount);
        if (due == null || due.isEmpty()) {
            return List.of();
        }
        redisTemplate.opsForZSet().remove(QUEUE_KEY, due.toArray());

        var result = new ArrayList<DueReminder>(due.size());
        for (var member : due) {
            var parsed = parse(member);
            if (parsed == null) {
                continue;
            }

            var payloadKey = createPayloadKey(parsed[0], parsed[1]);
            var payload = redisTemplate.opsForHash().entries(payloadKey);
            redisTemplate.delete(payloadKey);

            var title = (String) payload.getOrDefault(TITLE_FIELD, "");
            var description = (String) payload.getOrDefault(DESCRIPTION_FIELD, "");
            result.add(new DueReminder(parsed[0], parsed[1], title, description));
        }
        return result;
    }

    @Override
    public void clear() {
        redisTemplate.delete(QUEUE_KEY);
    }

    private String createMember(long bookingId, long chatId) {
        return bookingId + ":" + chatId;
    }

    private String createPayloadKey(long bookingId, long chatId) {
        return PAYLOAD_KEY_PREFIX + bookingId + ":" + chatId;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private Long[] parse(String member) {
        var parts = member.split(":");
        if (parts.length != 2) {
            return null;
        }
        try {
            return new Long[]{
                    Long.parseLong(parts[0]),
                    Long.parseLong(parts[1])
            };
        } catch (NumberFormatException e) {
            return null;
        }
    }
}