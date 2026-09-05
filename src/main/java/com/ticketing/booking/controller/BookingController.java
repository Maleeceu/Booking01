package com.ticketing.booking.controller;

import com.ticketing.booking.dto.BookingRequest;
import com.ticketing.booking.dto.BookingResponse;
import com.ticketing.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@RequestBody BookingRequest request) {
        BookingResponse response = bookingService.create(
                request.userId(), request.eventId(), request.seatIds());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
