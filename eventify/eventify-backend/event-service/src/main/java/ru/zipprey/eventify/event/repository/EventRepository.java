package ru.zipprey.eventify.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zipprey.eventify.event.model.entity.Event;

import java.time.Instant;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
            select e from Event e
            where (cast(:from as timestamp) is null or e.date >= :from)
            and (cast(:to as timestamp) is null or e.date <= :to)
            """)
    Page<Event> findAllFiltered(@Param("from") Instant from, @Param("to") Instant to, Pageable pageable);
}
