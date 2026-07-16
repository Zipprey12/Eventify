package ru.zipprey.eventify.notification.model.enity;

import jakarta.persistence.*;
import lombok.*;
import ru.zipprey.eventify.notification.model.email.EmailTemplate;
import ru.zipprey.eventify.notification.model.email.PendingEmailStatus;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pending_emails")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailTemplate template;

    @Column(name = "arguments_json", nullable = false, columnDefinition = "text")
    private String argumentsJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PendingEmailStatus status;

    @Column(name = "sent_at")
    private Instant sentAt;
}


