package com.ticketing.booking.repository;

import com.ticketing.booking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.JpqlQueryBuilder;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    @Query("""
    SELECT bs.seat.id FROM BookingSeat bs
    WHERE bs.eventId = :eventId
      AND bs.seat.id IN :seatIds
      AND bs.booking.status = com.ticketing.booking.entity.BookingStatus.ACTIVE
    """)
    List<Long> findTakenSeatIds(@Param("eventId") Long eventId,
                                @Param("seatIds") List<Long> seatIds);
}
