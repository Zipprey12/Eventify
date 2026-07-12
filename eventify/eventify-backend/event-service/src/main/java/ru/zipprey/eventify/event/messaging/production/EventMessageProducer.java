package ru.zipprey.eventify.event.messaging.production;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.kafka.event.EventOverbookedMessage;
import ru.zipprey.eventify.kafka.event.Topics;

import static ru.zipprey.eventify.kafka.event.Topics.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class EventMessageProducer {

    public static final String PUBLISH_LOG = "Опубликовано {} для события {}";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(EventCreatedMessage message) {
        kafkaTemplate.send(CREATED.getTopic(), message.eventId().toString(), message);
        logPublishing(CREATED, message.eventId());
    }

    public void publish(EventDateChangedMessage message) {
        kafkaTemplate.send(DATE_CHANGED.getTopic(), message.eventId().toString(), message);
        logPublishing(DATE_CHANGED, message.eventId());
    }

    public void publish(EventOverbookedMessage message) {
        kafkaTemplate.send(OVERBOOKED.getTopic(), message.eventId().toString(), message);
        logPublishing(OVERBOOKED, message.eventId());
    }

    private void logPublishing(Topics topic, Long id) {
        log.info(PUBLISH_LOG, topic.getTopic(), id);
    }
}