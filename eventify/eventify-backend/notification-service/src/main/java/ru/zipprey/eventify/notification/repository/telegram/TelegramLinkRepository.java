package ru.zipprey.eventify.notification.repository.telegram;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zipprey.eventify.notification.model.enity.TelegramLink;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramLinkRepository extends JpaRepository<TelegramLink, String> {

    Optional<TelegramLink> findByChatId(Long chatId);

    Optional<TelegramLink> findByLinkCode(String linkCode);

    void deleteByChatId(Long chatId);

    List<TelegramLink> findAllByChatIdIsNotNull();

}