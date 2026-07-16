package ru.zipprey.eventify.notification.service.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.kafka.booking.BookingConfirmedMessage;
import ru.zipprey.eventify.notification.mapper.EventReminderMapper;
import ru.zipprey.eventify.notification.repository.email.EventReminderRepository;
import ru.zipprey.eventify.notification.repository.SettingsRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventReminderServiceImpl implements EventReminderService {

    private final EventReminderRepository repository;
    private final SettingsRepository settingsRepository;
    private final EventReminderMapper mapper;

    @Override
    @Transactional
    public void scheduleIfRequested(BookingConfirmedMessage message) {
        var settings = settingsRepository.findById(message.customerEmail()).orElse(null);
        if (settings == null || !Boolean.TRUE.equals(settings.getNotifyUpcoming())) {
            return;
        }

        var hours = settings.getNotifyBeforeHours();
        if (hours == null) {
            return;
        }
        if (repository.existsByBookingId(message.bookingId())) {
            return;
        }

        var remindAt = message.eventDateTime().minus(hours, ChronoUnit.HOURS);
        var reminder = mapper.toEntity(message, remindAt, hours);
        repository.save(reminder);

        if (remindAt.isBefore(Instant.now())) {
            log.info("Бронь id={}: до события меньше времени, чем указано до напоминания в настройках ({} ч.)"
                    , message.bookingId(), hours);
        } else {
            log.info("Запланировано напоминание для брони id={} на {}", message.bookingId(), remindAt);
        }
    }

    @Override
    @Transactional
    public void cancel(Long bookingId) {
        repository.deleteByBookingId(bookingId);
    }

    @Override
    @Transactional
    public void cancelAll(List<Long> bookingIds) {
        repository.deleteAllByBookingIdIn(bookingIds);
    }

    @Override
    @Transactional
    public void rescheduleForEventDateChange(Long eventId, Instant newEventDateTime) {
        var updated = repository.rescheduleForEventDateChange(eventId, newEventDateTime);
        if (updated > 0) {
            log.info("Пересчитаны напоминания ({} шт.) для события id={} — новая дата {}",
                    updated, eventId, newEventDateTime);
        }
    }

    @Override
    @Transactional
    public void rescheduleForSettingsChange(String customerEmail, Boolean notifyUpcoming, Integer notifyBeforeHours) {
        if (!Boolean.TRUE.equals(notifyUpcoming) || notifyBeforeHours == null) {
            var deleted = repository.deleteAllByCustomerEmail(customerEmail);
            if (deleted > 0) {
                log.info("Напоминание о брони на событие отключено. Удалено {} " +
                        "напоминаний для {}", deleted, customerEmail);
            }
            return;
        }

        var updated = repository.rescheduleForHoursChange(customerEmail, notifyBeforeHours);
        if (updated > 0) {
            log.info("Пересчитаны напоминания ({} шт.) для {} — новый интервал {} ч.",
                    updated, customerEmail, notifyBeforeHours);
        }
    }

    @Override
    @Transactional
    public boolean claim(Long reminderId) {
        return repository.markSentIfNotAlready(reminderId) > 0;
    }
}
