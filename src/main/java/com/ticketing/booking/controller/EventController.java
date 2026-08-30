package com.ticketing.booking.controller;

import com.ticketing.booking.dto.EventResponse;
import com.ticketing.booking.entity.Event;
import com.ticketing.booking.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @GetMapping()
    public ResponseEntity<List<EventResponse>> getAll() {
        List<EventResponse> eventList = eventService.findAll();

        return new ResponseEntity<>(eventList, HttpStatus.OK);
    }

}
