package ru.zipprey.eventify.outbox.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.outbox.OutboxStatus;
import ru.zipprey.eventify.outbox.entity.OutboxEvent;
import ru.zipprey.eventify.outbox.repository.OutboxEventRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxDataService {

    private final OutboxEventRepository repository;

    @Value("${outbox.fetch-limit:50}")
    private int fetchLimit;

    public void add(OutboxEvent event) {
        repository.save(event);
    }

    public List<OutboxEvent> getReady() {
        return repository.findAndLockByStatus(OutboxStatus.READY.name(), fetchLimit);
    }

    public List<OutboxEvent> getPendingEnrichment() {
        return repository.findAllByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING_ENRICHMENT);
    }

    public void markPublished(OutboxEvent event) {
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setProcessedAt(Instant.now());
        repository.save(event);
    }

    public boolean markReady(OutboxEvent event, String enrichedPayload, String payloadType) {
        var updated = repository.updatePayloadAndStatusIfCurrent(
                event.getId(), enrichedPayload, payloadType,
                OutboxStatus.READY, OutboxStatus.PENDING_ENRICHMENT);
        return updated > 0;
    }

    public boolean markUnavailable(OutboxEvent event) {
        var updated = repository.updatePayloadAndStatusIfCurrent(
                event.getId(), "", "unknown",
                OutboxStatus.UNAVAILABLE, OutboxStatus.PENDING_ENRICHMENT);
        return updated > 0;
    }
}