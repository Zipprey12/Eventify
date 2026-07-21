package ru.zipprey.eventify.notification.repository.telegram;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.notification.model.enity.TelegramLink;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramLinkRepository extends JpaRepository<TelegramLink, String> {

    Optional<TelegramLink> findByChatId(Long chatId);

    Optional<TelegramLink> findByLinkCode(String linkCode);

    @Transactional
    @Modifying
    @Query("DELETE FROM TelegramLink t WHERE t.chatId = :chatId")
    void deleteByChatId(@Param("chatId") Long chatId);

    List<TelegramLink> findAllByChatIdIsNotNull();

}