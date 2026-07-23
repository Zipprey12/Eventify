package ru.zipprey.eventify.notification.repository.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zipprey.eventify.notification.model.email.PendingEmailStatus;
import ru.zipprey.eventify.notification.model.enity.PendingEmail;

import java.util.List;

public interface PendingEmailRepository extends JpaRepository<PendingEmail, Long> {

    List<PendingEmail> findAllByStatus(PendingEmailStatus status);

    @Modifying
    @Query("""
            UPDATE PendingEmail p
            SET p.status = :newStatus, p.sentAt = CURRENT_TIMESTAMP
            WHERE p.id = :id AND p.status = :expectedStatus
            """)
    int updateStatusIfCurrent(@Param("id") Long id,
                              @Param("newStatus") PendingEmailStatus newStatus,
                              @Param("expectedStatus") PendingEmailStatus expectedStatus);
}