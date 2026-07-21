package ru.zipprey.eventify.notification.repository.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.zipprey.eventify.notification.model.enity.EventReminder;

import java.time.Instant;
import java.util.List;

@Repository
public interface EventReminderRepository extends JpaRepository<EventReminder, Long> {

    boolean existsByBookingId(Long bookingId);

    @Modifying
    @Query("DELETE FROM EventReminder r WHERE r.bookingId = :bookingId")
    void deleteByBookingId(@Param("bookingId") Long bookingId);

    @Modifying
    @Query("DELETE FROM EventReminder r WHERE r.bookingId IN :bookingIds")
    void deleteAllByBookingIdIn(@Param("bookingIds") List<Long> bookingIds);

    @Query("SELECT r FROM EventReminder r WHERE r.sent = false AND r.remindAt <= :instant")
    List<EventReminder> findReminders(@Param("instant") Instant instant);

    @Modifying
    @Query("""
            UPDATE EventReminder r SET r.sent = true,
            r.sentAt = CURRENT_TIMESTAMP
            WHERE r.id = :id AND r.sent = false
            """)
    int markSentIfNotAlready(@Param("id") Long id);

    @Modifying
    @Query(value = """
            UPDATE event_reminders
            SET event_date_time = :newDateTime, remind_at = :newDateTime - (notify_before_hours || ' hours')::interval,
            sent = (:newDateTime - (notify_before_hours || ' hours')::interval) <= now()
            WHERE event_id = :eventId
            """, nativeQuery = true)
    int rescheduleForEventDateChange(@Param("eventId") Long eventId, @Param("newDateTime") Instant newDateTime);

    @Modifying
    @Query(value = """
            UPDATE event_reminders
            SET notify_before_hours = :hours, remind_at = event_date_time - (:hours || ' hours')::interval,
                sent = (event_date_time - (:hours || ' hours')::interval) <= now()
            WHERE customer_email = :email AND event_date_time > now()
            """, nativeQuery = true)
    int rescheduleForHoursChange(@Param("email") String email, @Param("hours") Integer hours);

    @Modifying
    @Query("DELETE FROM EventReminder r WHERE r.customerEmail = :email AND r.sent = false")
    int deleteAllByCustomerEmail(@Param("email") String email);
}
