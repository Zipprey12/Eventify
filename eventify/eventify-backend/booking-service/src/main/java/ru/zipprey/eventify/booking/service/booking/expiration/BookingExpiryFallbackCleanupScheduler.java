package ru.zipprey.eventify.booking.service.booking.expiration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.booking.repository.BookingRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpiryFallbackCleanupScheduler {

    private final BookingRepository repository;
    private final ExpiryQueueService queueService;

    @Transactional
    @Scheduled(fixedDelayString = "${booking.expiry-fallback-delay:3600000}")
    public void verify() {
        var start = Instant.now().minus(1, ChronoUnit.MINUTES);
        var ids = repository.findExpiredUnconfirmedIds(start);
        if (ids.isEmpty()) {
            return;
        }

        var deleted = repository.deleteAllExpiredUnconfirmed(start);
        log.warn("Проверка нашла {} просроченных неподтвержденных броней, " +
                "которые не были автоматически отменены. Удалено {}", ids.size(), deleted);
        queueService.removeAll(ids);
    }
}
