package ru.zipprey.eventify.booking.service.booking.expiration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.zipprey.eventify.booking.repository.BookingRepository;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpiryScheduler {

    public static final long SCHEDULE_DELAY_MS = 30_000L;

    private final BookingRepository repository;
    private final ExpiryQueueService queueService;

    @Value("${booking.expiry-batch-size:50}")
    private long batchSize;

    @Scheduled(fixedDelay = SCHEDULE_DELAY_MS)
    public void expireBookings() {
        var ids = queueService.pollDue(Instant.now(), batchSize);
        if (ids.isEmpty()) {
            return;
        }

        for (var strId : ids) {
            var id = Long.parseLong(strId);
            try {
                var deleted = repository.deleteByIdIfUnconfirmed(id);
                if (deleted > 0) {
                    log.info("Автоматическое удаление просроченной заявки: {}", id);
                }
                queueService.remove(id);
            } catch (Exception e) {
                log.error("Ошибка автоматического удаления заявки id ={} {}", id, e.getMessage());
            }
        }
    }
}
