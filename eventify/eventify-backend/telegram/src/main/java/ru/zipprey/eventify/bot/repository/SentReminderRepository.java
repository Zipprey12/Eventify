package ru.zipprey.eventify.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zipprey.eventify.bot.model.entity.SentReminder;

public interface SentReminderRepository extends JpaRepository<SentReminder, Long> {

    boolean existsByBookingIdAndChatId(Long bookingId, Long chatId);

    void deleteByChatId(Long chatId);
}
