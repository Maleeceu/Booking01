package com.ticketing.booking.dto;

import java.util.List;

public record BookingRequest(
        Long userId,
        Long eventId,
        List<Long> seatIds
) {}