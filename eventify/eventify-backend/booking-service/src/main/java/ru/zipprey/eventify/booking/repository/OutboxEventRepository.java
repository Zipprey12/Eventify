package ru.zipprey.eventify.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zipprey.eventify.booking.model.outbox.OutboxEvent;
import ru.zipprey.eventify.booking.model.outbox.OutboxStatus;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findAllByStatusOrderByCreatedAtAsc(OutboxStatus status);
}