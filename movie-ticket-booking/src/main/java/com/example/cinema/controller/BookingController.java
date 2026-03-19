package com.example.cinema.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.cinema.entity.Booking;
import com.example.cinema.repository.BookingRepository;
import com.example.cinema.service.BookingService;
import com.example.cinema.service.SeatLockService;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SeatLockService seatLockService;

    // ✅ Constructor Injection (ALL dependencies included)
    public BookingController(
            BookingService bookingService,
            BookingRepository bookingRepository,
            SimpMessagingTemplate messagingTemplate,
            SeatLockService seatLockService) {

        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.messagingTemplate = messagingTemplate;
        this.seatLockService = seatLockService;
    }

    // ✅ CONFIRM BOOKING
    @PostMapping("/confirm")
    public ResponseEntity<Booking> confirmBooking(@RequestBody Booking booking) {

        Booking saved = bookingService.confirmBooking(booking, booking.getMovieId());

        // Remove locks for booked seats
        seatLockService.unlockSeat(
                booking.getMovieId(),
                booking.getTheatre(),
                booking.getShowTime(),
                null,
                null
        );

        // Broadcast updated seats
        var response = seatLockService.getSeatStatus(
                booking.getMovieId(),
                booking.getTheatre(),
                booking.getShowTime()
        );

        messagingTemplate.convertAndSend("/topic/seats-updates", response);

        return ResponseEntity.ok(saved);
    }

    // ✅ OCCUPIED SEATS
    @GetMapping("/occupied-seats")
    public ResponseEntity<List<String>> getOccupiedSeats(
            @RequestParam Long movieId,
            @RequestParam String theatre,
            @RequestParam String showTime) {

        List<Booking> bookings = bookingRepository
                .findByMovieIdAndTheatreAndShowTime(movieId, theatre, showTime);

        List<String> seats = bookings.stream()
                .flatMap(b -> {
                    String seatsStr = b.getSeatNumbers();
                    if (seatsStr == null || seatsStr.isEmpty()) {
                        return List.<String>of().stream();
                    }
                    return List.of(seatsStr.split(",")).stream();
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(seats);
    }

    // ✅ BOOKING HISTORY
    @GetMapping("/history")
    public ResponseEntity<Object> getBookingHistory(@RequestParam String mobile) {
        return ResponseEntity.ok(
                bookingService.getBookingHistory(mobile)
        );
    }

    // ✅ CANCEL BOOKING 
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}