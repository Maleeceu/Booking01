package com.ticketing.booking.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record EventResponse(
        Long id,
        String title,
        OffsetDateTime startsAt,
        BigDecimal price,
        String venueName
) {}
