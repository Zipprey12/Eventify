package ru.zipprey.eventify.booking.service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.booking.model.dto.outbox.OutboxBookingConfirmedPayload;
import ru.zipprey.eventify.booking.model.dto.outbox.OutboxBookingDeletedPayload;
import ru.zipprey.eventify.booking.model.entity.OutboxEvent;
import ru.zipprey.eventify.booking.service.event.EventsServiceCaller;
import ru.zipprey.eventify.eventapi.exception.EventNotFoundException;
import ru.zipprey.eventify.eventapi.model.EventDto;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedByAdminMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedMessage;
import tools.jackson.databind.ObjectMapper;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEnrichmentScheduler {

    private static final String CONFIRMED = "booking.confirmed";
    private static final String DELETED_BY_ADMIN = "booking.deleted-by-admin";
    private static final String CANCELED = "booking.canceled";

    private final OutboxDataService outboxDataService;
    private final EventsServiceCaller caller;
    private final ObjectMapper objectMapper;

    @Async
    @Scheduled(fixedDelay = 5000)
    public void enrich() {
        var pending = outboxDataService.getPendingEnrichment();
        if (pending.isEmpty()) {
            return;
        }

        for (var event : pending) {
            try {
                enrichEvent(event);
            } catch (Exception e) {
                log.error("Ошибка заполнения outbox-события id={}: {}", event.getId(), e.getMessage());
            }
        }
    }

    private void enrichEvent(OutboxEvent event) {
        switch (event.getTopic()) {
            case CONFIRMED -> enrichConfirmedMessage(event);
            case DELETED_BY_ADMIN -> enrichDeletedByAdminMessage(event);
            case CANCELED -> enrichDeletedMessage(event);
            default -> log.warn("Неизвестный топик для обогащения: {}", event.getTopic());
        }
    }

    private void enrichConfirmedMessage(OutboxEvent event) {
        enrich(event, OutboxBookingConfirmedPayload.class,
                (p, e) -> new BookingConfirmedMessage(
                        p.bookingId(),
                        p.eventId(),
                        p.customerEmail(),
                        e.getTitle(),
                        e.getDateTime(),
                        p.ticketsCount()));
    }

    private void enrichDeletedByAdminMessage(OutboxEvent event) {
        enrich(event, OutboxBookingDeletedPayload.class,
                (p, e) -> new BookingDeletedByAdminMessage(
                        p.bookingId(),
                        p.eventId(),
                        p.customerEmail(),
                        e.getTitle(),
                        e.getDateTime(),
                        p.ticketsCount(),
                        p.wasConfirmed()));
    }

    private void enrichDeletedMessage(OutboxEvent event) {
        enrich(event, OutboxBookingDeletedPayload.class,
                (p, e) -> new BookingDeletedMessage(
                        p.eventId(),
                        p.bookingId(),
                        p.customerEmail(),
                        e.getTitle(),
                        e.getDateTime(),
                        p.ticketsCount(),
                        p.wasConfirmed())
        );
    }

    private <P extends OutboxPayload> void enrich(
            OutboxEvent event,
            Class<P> payloadClass,
            BiFunction<P, EventDto, Object> enricher) {

        var payload = objectMapper.readValue(event.getPayload(), payloadClass);
        var eventDto = find(payload.eventId(), event);
        if (eventDto == null) return;

        var enriched = enricher.apply(payload, eventDto);
        logEnrich(event.getTopic());

        outboxDataService.markReady(
                event,
                objectMapper.writeValueAsString(enriched),
                enriched.getClass().getName()
        );
    }

    private EventDto find(Long eventId, OutboxEvent event) {
        try {
            return caller.findById(eventId).block();
        } catch (EventNotFoundException e) {
            log.info("Не удалось заполнить {}, т.к событие {} удалено. Сообщение не будет проброшено", event.getTopic(), eventId);
            outboxDataService.markUnavailable(event);
        }
        return null;
    }

    private void logEnrich(String topic) {
        log.info("Заполнение {} ", topic);
    }
}