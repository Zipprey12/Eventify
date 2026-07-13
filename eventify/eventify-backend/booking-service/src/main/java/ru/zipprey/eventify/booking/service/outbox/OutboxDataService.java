package ru.zipprey.eventify.booking.service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.zipprey.eventify.booking.model.entity.OutboxEvent;
import ru.zipprey.eventify.booking.model.OutboxStatus;
import ru.zipprey.eventify.booking.repository.OutboxEventRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxDataService {

    private static final int FLUSH_THRESHOLD = 50;
    private final OutboxEventRepository repository;
    private final List<OutboxEvent> writeBuffer = new CopyOnWriteArrayList<>();

    public void addUnprocessed(OutboxEvent event) {
        writeBuffer.add(event);
        if (writeBuffer.size() >= FLUSH_THRESHOLD) {
            flush();
        }
    }

    public List<OutboxEvent> getReady() {
        flush();
        return repository.findAllByStatusOrderByCreatedAtAsc(OutboxStatus.READY);
    }

    public List<OutboxEvent> getPendingEnrichment() {
        return repository.findAllByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING_ENRICHMENT);
    }

    public void markPublished(OutboxEvent event) {
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setProcessedAt(Instant.now());
        repository.save(event);
    }

    public void markReady(OutboxEvent event, String enrichedPayload, String payloadType) {
        event.setPayload(enrichedPayload);
        event.setPayloadType(payloadType);
        event.setStatus(OutboxStatus.READY);
        repository.save(event);
    }

    public void markUnavailable(OutboxEvent event) {
        event.setStatus(OutboxStatus.UNAVAILABLE);
        event.setPayloadType("unknown");
        event.setPayload("");
        repository.save(event);
    }

    @Scheduled(fixedDelay = 1000)
    public synchronized void flush() {
        if (writeBuffer.isEmpty()) {
            return;
        }

        var toFlush = new ArrayList<>(writeBuffer);
        writeBuffer.removeAll(toFlush);

        try {
            repository.saveAll(toFlush);
            log.debug("Сохранено {} outbox-событий в БД", toFlush.size());
        } catch (Exception e) {
            log.error("Ошибка сохранения outbox-буфера, возвращено {} событий: {}",
                    toFlush.size(), e.getMessage());
            writeBuffer.addAll(0, toFlush);
        }
    }
}
