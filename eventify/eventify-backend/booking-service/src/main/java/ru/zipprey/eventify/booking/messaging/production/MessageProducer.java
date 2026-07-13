package ru.zipprey.eventify.booking.messaging.production;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.kafka.booking.*;

import static ru.zipprey.eventify.kafka.booking.Topics.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

    private static final String PUBLISH_LOG = "Опубликовано {} для {}";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(BookingConfirmedMessage message) {
        send(CONFIRMED, message.bookingId().toString(), message);
    }

    public void publish(BookingDeletedMessage message) {
        send(DELETED, message.bookingId().toString(), message);
    }

    public void publish(BookingDeletedByAdminMessage message) {
        send(DELETED_BY_ADMIN, message.bookingId().toString(), message);
    }

    public void publish(BookingsCanceledMessage message) {
        send(FORCE_CANCELED, message.eventId().toString(), message);
    }

    public void publish(CancellationFailedMessage message) {
        send(FORCE_CANCELED_ERROR, message.eventId().toString(), message);
    }

    private void send(Topics topic, String key, Object message) {
        kafkaTemplate.send(topic.getTopic(), key, message);
        log.info(PUBLISH_LOG, topic.getTopic(), key);
    }
}