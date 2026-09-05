package com.ticketing.booking.service;

import com.ticketing.booking.dto.BookingResponse;
import com.ticketing.booking.entity.*;
import com.ticketing.booking.exception.InvalidBookingException;
import com.ticketing.booking.exception.ResourceNotFoundException;
import com.ticketing.booking.exception.SeatUnavailableException;
import com.ticketing.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final AppUserRepository appUserRepository;
    private final EventRepository eventRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public BookingResponse create(Long userId, Long eventId, List<Long> seatIds) {

        if(seatIds.isEmpty()) {
            throw new InvalidBookingException("Seat Id can not be empty!");
        }

        List<String> seatNumbers = new ArrayList<>();

        // 1. kullanıcı var mı
        AppUser user = appUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("There is no user with that id."));

        // 2. etkinlik var mı
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("There is no event with that id"));

        // 3. dolu koltuk var mı → varsa hata
        List<Long> takenSeats = bookingSeatRepository.findTakenSeatIds(eventId, seatIds);
        if(!takenSeats.isEmpty()) {
            throw new SeatUnavailableException("Seat taken.");
        }

        List<Seat> seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new ResourceNotFoundException("Seat not found.");
        }

        Long venueId = event.getVenue().getId();
        for (Seat seat : seats) {
            if (!seat.getVenue().getId().equals(venueId)) {
                throw new InvalidBookingException("Seat does not belong to this event's venue.");
            }
        }

        for(Seat seat : seats) {
            String seatNumber = seat.getRowLabel()+ seat.getSeatNumber();
            seatNumbers.add(seatNumber);
        }

        // 4. Booking oluştur, kaydet
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setStatus(BookingStatus.ACTIVE);

        bookingRepository.save(booking);


        // 5. her seat için BookingSeat oluştur, kaydet


        for (Seat seat : seats) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);
            bookingSeat.setEventId(eventId);
            bookingSeatRepository.save(bookingSeat);
        }

        BigDecimal totalPrice = event.getPrice().multiply(BigDecimal.valueOf(seatIds.size()));

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setBookingId(booking.getId());
        bookingResponse.setEventName(event.getTitle());
        bookingResponse.setSeatNumbers(seatNumbers);
        bookingResponse.setTotalPrice(totalPrice);

        return bookingResponse;
    }
}
