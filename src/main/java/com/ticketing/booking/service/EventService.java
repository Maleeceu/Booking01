package com.ticketing.booking.service;

import com.ticketing.booking.dto.EventResponse;
import com.ticketing.booking.entity.Event;
import com.ticketing.booking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    public List<EventResponse> findAll() {
        return eventRepository.findAllWithVenue().stream()
                .map(e -> new EventResponse(
                        e.getId(),
                        e.getTitle(),
                        e.getStartsAt(),
                        e.getPrice(),
                        e.getVenue().getName()
                )).toList();
    }

}
