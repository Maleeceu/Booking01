package com.ticketing.booking.exception;

import com.ticketing.booking.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(SeatUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleSeatTaken(SeatUnavailableException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("SEAT_UNAVAILABLE", e.getMessage()));
    }

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(InvalidBookingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_REQUEST", e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException e) {
        String msg = e.getMostSpecificCause().getMessage();
        if (msg != null && msg.contains("booking_seat_unique")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("SEAT_UNAVAILABLE", "Bu koltuk az önce satıldı."));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("CONFLICT", "Veri bütünlüğü hatası."));
    }
}
