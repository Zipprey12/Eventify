package ru.zipprey.eventify.booking.messaging.production;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.booking.BookingsCanceledMessage;
import ru.zipprey.eventify.kafka.booking.CancellationFailedMessage;
import ru.zipprey.eventify.kafka.booking.Topics;

import static ru.zipprey.eventify.kafka.booking.Topics.FORCE_CANCELED;
import static ru.zipprey.eventify.kafka.booking.Topics.FORCE_CANCELED_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingMessageProducer {

    public static final String PUBLISH_LOG = "Опубликовано {} для события {}";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(BookingsCanceledMessage message) {
        kafkaTemplate.send(FORCE_CANCELED.getTopic(), message.eventId().toString(), message);
        logPublishing(FORCE_CANCELED, message.eventId());
    }

    public void publish(CancellationFailedMessage message) {
        kafkaTemplate.send(FORCE_CANCELED_ERROR.getTopic(), message.eventId().toString(), message);
        logPublishing(FORCE_CANCELED_ERROR, message.eventId());
    }

    private void logPublishing(Topics topic, Long id) {
        log.info(PUBLISH_LOG, topic.getTopic(), id);
    }
}
