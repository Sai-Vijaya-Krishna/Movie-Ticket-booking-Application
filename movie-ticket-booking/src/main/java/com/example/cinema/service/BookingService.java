package com.example.cinema.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cinema.dto.SeatUpdate;
import com.example.cinema.entity.Booking;
import com.example.cinema.entity.Movie;
import com.example.cinema.repository.BookingRepository;
import com.example.cinema.repository.MovieRepository;

import org.springframework.messaging.simp.SimpMessagingTemplate;


@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final MovieRepository movieRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public BookingService(BookingRepository bookingRepo,
            MovieRepository movieRepo,
            SimpMessagingTemplate messagingTemplate) {this.bookingRepo = bookingRepo;this.movieRepo = movieRepo;this.messagingTemplate = messagingTemplate;
}

    @Transactional
    public Booking confirmBooking(Booking booking, Long movieId) {

        // 1. Fetch movie
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        // 2. Validate seat count
        if (booking.getSeatsBooked() == null || booking.getSeatsBooked() <= 0) {
            throw new RuntimeException("Invalid seat count");
        }
        // 3. Prevent double booking
        List<Booking> existingBookings =
                bookingRepo.findByMovieIdAndTheatreAndShowTime(
                        movieId,
                        booking.getTheatre(),
                        booking.getShowTime()
                );

        Set<String> alreadyBookedSeats = existingBookings.stream()
                .flatMap(b -> List.of(b.getSeatNumbers().split(",")).stream())
                .collect(Collectors.toSet());

        for (String seat : booking.getSeatNumbers().split(",")) {
            if (alreadyBookedSeats.contains(seat)) {
                throw new RuntimeException("Seat already booked: " + seat);
            }
        }

        // =========================
        // 4. Validate total available seats
        // =========================
        if (movie.getAvailableSeats() < booking.getSeatsBooked()) {
            throw new RuntimeException("Seats not available");
        }

        // =========================
        // 5. Reduce available seats
        // =========================
        movie.setAvailableSeats(
                movie.getAvailableSeats() - booking.getSeatsBooked()
        );
        movieRepo.save(movie);

        // =========================
        // 6. Finalize booking
        // =========================
        booking.setMovieId(movieId);
        booking.setMovieTitle(movie.getTitle());
        System.out.println("DEBUG MOVIE TITLE: " + movie.getTitle());
        booking.setRegisterNumber("REG" + System.currentTimeMillis());

        Booking savedBooking = bookingRepo.save(booking);

     // SEND REALTIME UPDATE
     SeatUpdate update = new SeatUpdate(
             savedBooking.getMovieId(),
             savedBooking.getTheatre(),
             savedBooking.getShowTime(),
             savedBooking.getSeatNumbers()
     );

     String topic = "/topic/seats/" +
    	        savedBooking.getMovieId() + "/" +
    	        savedBooking.getTheatre() + "/" +
    	        savedBooking.getShowTime().replace(" ", "_");

     messagingTemplate.convertAndSend(topic, update);

     return savedBooking;
    }

    public List<Booking> getBookingHistory(String mobile) {
        return bookingRepo.findByMobileOrderByIdDesc(mobile);
    }


    @Transactional
    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepo.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        Movie movie = movieRepo.findById(booking.getMovieId())
            .orElseThrow(() -> new RuntimeException("Movie not found"));

        // Restore seat count
        movie.setAvailableSeats(
            movie.getAvailableSeats() + booking.getSeatsBooked()
        );
        movieRepo.save(movie);

        // Delete booking (frees seats)
        bookingRepo.delete(booking);
        SeatUpdate update = new SeatUpdate(
                booking.getMovieId(),
                booking.getTheatre(),
                booking.getShowTime(),
                booking.getSeatNumbers()
        );

        String topic = "/topic/seats/" +
                booking.getMovieId() + "/" +
                booking.getTheatre() + "/" +
                booking.getShowTime().replace(" ", "_");

        messagingTemplate.convertAndSend(topic, update);
    }

	public SimpMessagingTemplate getMessagingTemplate() {
		return messagingTemplate;
	}

}