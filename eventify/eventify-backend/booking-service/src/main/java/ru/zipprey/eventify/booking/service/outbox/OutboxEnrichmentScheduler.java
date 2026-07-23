package ru.zipprey.eventify.booking.service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.booking.service.event.EventsServiceCaller;
import ru.zipprey.eventify.eventapi.model.EventDto;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedByAdminMessage;
import ru.zipprey.eventify.kafka.booking.BookingDeletedMessage;
import ru.zipprey.eventify.outbox.entity.OutboxEvent;
import ru.zipprey.eventify.outbox.service.OutboxDataService;
import ru.zipprey.eventify.outbox.service.OutboxTypeRegistry;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongFunction;

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
    private final OutboxTypeRegistry typeRegistry;

    @Async
    @Scheduled(fixedDelay = 5000)
    public void enrich() {
        var pending = outboxDataService.getPendingEnrichment();
        if (pending.isEmpty()) {
            return;
        }

        Map<Long, EventDto> eventsById;
        try {
            eventsById = fetchNeededEvents(pending);
        } catch (Exception e) {
            log.error("Не удалось получить данные событий. Повтор на следующем цикле: {}",
                    e.getMessage());
            return;
        }

        for (var event : pending) {
            try {
                enrichEvent(event, eventsById);
            } catch (Exception e) {
                log.error("Ошибка заполнения события id={}: {}", event.getId(), e.getMessage());
            }
        }
    }

    private Map<Long, EventDto> fetchNeededEvents(List<OutboxEvent> pending) {
        var eventIds = pending.stream()
                .map(this::extractEventId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (eventIds.isEmpty()) {
            return Map.of();
        }

        return caller.findByIds(eventIds)
                .collectMap(EventDto::id, Function.identity())
                .blockOptional()
                .orElse(Map.of());
    }

    private Long extractEventId(OutboxEvent event) {
        try {
            return switch (event.getTopic()) {
                case CONFIRMED -> objectMapper.readValue(event.getPayload(), BookingConfirmedMessage.class).eventId();
                case DELETED_BY_ADMIN ->
                        objectMapper.readValue(event.getPayload(), BookingDeletedByAdminMessage.class).eventId();
                case CANCELED -> objectMapper.readValue(event.getPayload(), BookingDeletedMessage.class).eventId();
                default -> null;
            };
        } catch (Exception e) {
            log.warn("Не удалось прочитать тело outbox-события id={} для извлечения eventId: {}",
                    event.getId(), e.getMessage());
            return null;
        }
    }

    private void enrichEvent(OutboxEvent event, Map<Long, EventDto> eventsById) {
        switch (event.getTopic()) {
            case CONFIRMED -> enrichConfirmedMessage(event, eventsById);
            case DELETED_BY_ADMIN -> enrichDeletedByAdminMessage(event, eventsById);
            case CANCELED -> enrichDeletedMessage(event, eventsById);
            default -> log.warn("Неизвестный топик для заполнения: {}", event.getTopic());
        }
    }

    private void enrichConfirmedMessage(OutboxEvent event, Map<Long, EventDto> eventsById) {
        enrich(event, eventsById, BookingConfirmedMessage.class, BookingConfirmedMessage::eventId,
                (p, e) -> new BookingConfirmedMessage(
                        p.bookingId(),
                        p.eventId(),
                        p.customerEmail(),
                        e.title(),
                        e.dateTime(),
                        p.ticketsCount()));
    }

    private void enrichDeletedByAdminMessage(OutboxEvent event, Map<Long, EventDto> eventsById) {
        enrich(event, eventsById, BookingDeletedByAdminMessage.class, BookingDeletedByAdminMessage::eventId,
                (p, e) -> new BookingDeletedByAdminMessage(
                        p.bookingId(),
                        p.eventId(),
                        p.customerEmail(),
                        e.title(),
                        e.dateTime(),
                        p.ticketsCount(),
                        p.wasConfirmed()));
    }

    private void enrichDeletedMessage(OutboxEvent event, Map<Long, EventDto> eventsById) {
        enrich(event, eventsById, BookingDeletedMessage.class, BookingDeletedMessage::eventId,
                (p, e) -> new BookingDeletedMessage(
                        p.eventId(),
                        p.bookingId(),
                        p.customerEmail(),
                        e.title(),
                        e.dateTime(),
                        p.ticketsCount(),
                        p.wasConfirmed())
        );
    }

    private <P> void enrich(
            OutboxEvent event,
            Map<Long, EventDto> eventsById,
            Class<P> payloadClass,
            ToLongFunction<P> eventIdExtractor,
            BiFunction<P, EventDto, Object> enricher) {

        var payload = objectMapper.readValue(event.getPayload(), payloadClass);
        var eventDto = eventsById.get(eventIdExtractor.applyAsLong(payload));
        if (eventDto == null) {
            log.info("Не удалось заполнить {}, т.к событие {} удалено или недоступно. Сообщение не будет опубликовано",
                    event.getTopic(), eventIdExtractor.applyAsLong(payload));
            outboxDataService.markUnavailable(event);
            return;
        }

        var enriched = enricher.apply(payload, eventDto);
        log.info("Заполнение {} ", event.getTopic());

        var applied = outboxDataService.markReady(
                event,
                objectMapper.writeValueAsString(enriched),
                typeRegistry.aliasFor(enriched.getClass())
        );
        if (!applied) {
            log.debug("Событие id={} обработано, пропуск.", event.getId());
        }
    }
}