package ru.zipprey.eventify.notification.model.enity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "notification_settings")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Settings {

    @Id
    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "notify_new_events")
    private Boolean notifyNewEvents;

    @Column(name = "notify_upcoming")
    private Boolean notifyUpcoming;

    @Column(name = "notify_before_hours")
    private Integer notifyBeforeHours;

    @Column(name = "email_confirmed", nullable = false)
    private Boolean emailConfirmed;

    @Column(name = "verification_code")
    private String verificationCode;
}
