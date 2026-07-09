package ru.zipprey.eventify.notification.repository.telegram;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zipprey.eventify.notification.model.enity.TelegramLink;

@Repository
public interface TelegramLinkRepository extends JpaRepository<TelegramLink, String> {
}