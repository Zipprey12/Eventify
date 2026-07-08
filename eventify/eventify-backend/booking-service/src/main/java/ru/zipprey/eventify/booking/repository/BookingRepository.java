package ru.zipprey.eventify.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.zipprey.eventify.booking.model.entity.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByCustomerEmail(String email);

    @Query("""
            SELECT b FROM Booking b
            WHERE (:eventId IS NULL OR b.eventId = :eventId)
            AND (:unconfirmedOnly = false OR b.confirmed = false)
            """)
    Page<Booking> findAllFiltered(@Param("eventId") Long eventId,
                                  @Param("unconfirmedOnly") boolean unconfirmedOnly,
                                  Pageable pageable);
}
