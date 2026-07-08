package ru.zipprey.eventify.event.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private Instant date;

    @Column(name = "total_places")
    private Integer totalTickets;

    @Column(name = "available_places")
    private Integer availableTickets;

    @Column(name = "image_url")
    private String imageUrl;
}