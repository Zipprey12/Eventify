package ru.zipprey.eventify.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.zipprey.eventify.outbox.OutboxStatus;
import ru.zipprey.eventify.outbox.entity.OutboxEvent;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query(value = """
            SELECT * FROM outbox_events
            WHERE status = :status
            ORDER BY created_at ASC
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> findAndLockByStatus(@Param("status") String status, @Param("limit") int limit);

    List<OutboxEvent> findAllByStatusOrderByCreatedAtAsc(OutboxStatus status);

    @Modifying
    @Query("""
            UPDATE OutboxEvent e
            SET e.payload = :payload, e.payloadType = :payloadType, e.status = :newStatus
            WHERE e.id = :id AND e.status = :expectedStatus
            """)
    int updatePayloadAndStatusIfCurrent(@Param("id") Long id,
                                        @Param("payload") String payload,
                                        @Param("payloadType") String payloadType,
                                        @Param("newStatus") OutboxStatus newStatus,
                                        @Param("expectedStatus") OutboxStatus expectedStatus);
}
