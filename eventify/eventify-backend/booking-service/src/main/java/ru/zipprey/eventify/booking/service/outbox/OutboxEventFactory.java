package ru.zipprey.eventify.booking.service.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.booking.model.dto.outbox.OutboxBookingDeletedPayload;
import ru.zipprey.eventify.booking.model.entity.Booking;
import ru.zipprey.eventify.booking.model.entity.OutboxEvent;
import ru.zipprey.eventify.booking.model.OutboxStatus;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OutboxEventFactory {

    private final ObjectMapper objectMapper;

    public OutboxEvent create(String topic, String key, Object payload, OutboxStatus status) {
        return OutboxEvent.builder()
                .topic(topic)
                .key(key)
                .payload(toJson(payload))
                .payloadType(payload.getClass().getName())
                .status(status)
                .build();
    }

    public OutboxEvent createDelete(String topic, Booking booking, OutboxStatus status) {
        return OutboxEvent.builder()
                .topic(topic)
                .key(String.valueOf(booking.getId()))
                .payload(toJson(createDeletePayload(booking)))
                .payloadType(OutboxBookingDeletedPayload.class.getName())
                .status(status)
                .build();
    }

    private OutboxBookingDeletedPayload createDeletePayload(Booking booking) {
        return new OutboxBookingDeletedPayload(
                booking.getId(),
                booking.getEventId(),
                booking.getCustomerEmail(),
                booking.getTicketsCount(),
                booking.getConfirmed()
        );
    }

    private String toJson(Object payload) {
        return objectMapper.writeValueAsString(payload);
    }
}