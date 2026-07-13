package ru.zipprey.eventify.booking.service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublishingScheduler {

    private final OutboxDataService outboxDataService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publish() {
        var ready = outboxDataService.getReady();

        for (var event : ready) {
            try {
                var clazz = Class.forName(event.getPayloadType());
                var payload = objectMapper.readValue(event.getPayload(), clazz);
                kafkaTemplate.send(event.getTopic(), event.getKey(), payload).get();
                log.info("Публикация {}. Ключ: {}", event.getTopic(), event.getKey());
                outboxDataService.markPublished(event);
            } catch (Exception e) {
                log.error("Ошибка публикации outbox id={}: {}", event.getId(), e.getMessage());
            }
        }
    }
}