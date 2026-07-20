package ru.zipprey.eventify.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zipprey.eventify.booking.model.entity.Booking;

import java.time.Instant;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByCustomerEmail(String email);

    List<Booking> findAllByConfirmedTrue();

    @Query("""
            SELECT b FROM Booking b
            WHERE (:eventId IS NULL OR b.eventId = :eventId)
            AND (:unconfirmedOnly = false OR b.confirmed = false)
            """)
    Page<Booking> findAllFiltered(@Param("eventId") Long eventId,
                                  @Param("unconfirmedOnly") boolean unconfirmedOnly,
                                  Pageable pageable);

    List<Booking> findAllByEventIdAndConfirmedTrueOrderByCreatedAtDesc(Long eventId);

    List<Booking> deleteAllByEventId(long eventId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Booking b WHERE b.id = :id AND b.confirmed = false")
    int deleteByIdIfUnconfirmed(@Param("id") Long id);

    @Query("SELECT b.id FROM Booking b WHERE b.confirmed = false AND b.expiryTime < :instant")
    List<Long> findExpiredUnconfirmedIds(@Param("instant") Instant instant);

    @Transactional
    @Modifying
    @Query("DELETE FROM Booking b WHERE b.confirmed = false AND b.expiryTime < :instant")
    int deleteAllExpiredUnconfirmed(@Param("instant") Instant instant);

    @Query("SELECT b.customerEmail FROM Booking b WHERE b.eventId = :eventId")
    List<String> findCustomerEmailsByEventId(@Param("eventId") Long eventId);
}