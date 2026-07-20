package ru.zipprey.eventify.bot.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "sent_reminders")
public class SentReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;
}
