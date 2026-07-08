package ru.zipprey.eventify.booking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "ticket_count")
    private Integer ticketsCount;

    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "expiry_time")
    private Instant expiryTime;

    private Boolean confirmed;

    private String timezone;
}
