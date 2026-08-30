package com.ticketing.booking.repository;

import com.ticketing.booking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e JOIN FETCH e.venue")
    List<Event> findAllWithVenue();

}
