package com.ticketing.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private String eventName;
    private List<String> seatNumbers;
    private BigDecimal totalPrice;

}
