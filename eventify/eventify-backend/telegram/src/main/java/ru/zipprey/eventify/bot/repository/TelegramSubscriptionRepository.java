package ru.zipprey.eventify.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zipprey.eventify.bot.model.entity.TelegramSubscription;

import java.util.Optional;

public interface TelegramSubscriptionRepository extends JpaRepository<TelegramSubscription, Long> {

    Optional<TelegramSubscription> findByCustomerEmail(String email);
}

