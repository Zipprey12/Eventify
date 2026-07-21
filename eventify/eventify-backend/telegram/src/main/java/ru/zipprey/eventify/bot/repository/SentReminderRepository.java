package ru.zipprey.eventify.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.bot.model.entity.SentReminder;

public interface SentReminderRepository extends JpaRepository<SentReminder, Long> {

    boolean existsByBookingIdAndChatId(Long bookingId, Long chatId);

    @Transactional
    @Modifying
    @Query("DELETE FROM SentReminder s WHERE s.chatId = :chatId")
    void deleteByChatId(@Param("chatId") Long chatId);
}
