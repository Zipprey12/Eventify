package ru.zipprey.eventify.notification.model.enity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "event_reminders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false, unique = true)
    private Long bookingId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "event_title", nullable = false)
    private String eventTitle;

    @Column(name = "event_date_time", nullable = false)
    private Instant eventDateTime;

    @Column(name = "tickets_count", nullable = false)
    private Integer ticketsCount;

    @Column(name = "remind_at", nullable = false)
    private Instant remindAt;

    @Column(name = "notify_before_hours", nullable = false)
    private Integer notifyBeforeHours;

    @Column(nullable = false)
    private Boolean sent;

    @Column(name = "sent_at")
    private Instant sentAt;
}
