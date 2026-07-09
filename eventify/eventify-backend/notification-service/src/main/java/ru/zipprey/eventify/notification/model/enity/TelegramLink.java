package ru.zipprey.eventify.notification.model.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "telegram_links")
public class TelegramLink {

    @Id
    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "link_code")
    private String linkCode;

    @Column(name = "link_code_expires_at")
    private Instant linkCodeExpiresAt;

}
