package com.example.cinema.service;

import com.example.cinema.dto.SeatUpdateResponse;
import com.example.cinema.entity.Booking;
import com.example.cinema.entity.SeatLock;
import com.example.cinema.repository.BookingRepository;
import com.example.cinema.repository.SeatLockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatLockService {

    private final SeatLockRepository lockRepo;
    private final BookingRepository bookingRepo;

    public SeatLockService(SeatLockRepository lockRepo,
                           BookingRepository bookingRepo) {
        this.lockRepo = lockRepo;
        this.bookingRepo = bookingRepo;
    }

    // LOCK SEAT
    @Transactional
    public void lockSeat(Long movieId,
                         String theatre,
                         String showTime,
                         String seat,
                         String user) {

        // Remove expired locks first
        lockRepo.deleteByExpiryTimeBefore(LocalDateTime.now());

        boolean alreadyLocked =
                lockRepo.existsByMovieIdAndTheatreAndShowTimeAndSeatNumber(
                        movieId, theatre, showTime, seat);

        if (alreadyLocked) {
            return;
        }

        SeatLock lock = new SeatLock();
        lock.setMovieId(movieId);
        lock.setTheatre(theatre);
        lock.setShowTime(showTime);
        lock.setSeatNumber(seat);
        lock.setLockedBy(user);
        lock.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        lockRepo.save(lock);
    }

    // UNLOCK SEAT
    @Transactional
    public void unlockSeat(Long movieId,
                           String theatre,
                           String showTime,
                           String seat,
                           String user) {

        // If seat or user is null, remove all locks for that show
        if (seat == null || user == null) {
            lockRepo.deleteByMovieIdAndTheatreAndShowTime(
                    movieId, theatre, showTime);
        } else {
            lockRepo.deleteByMovieIdAndTheatreAndShowTimeAndSeatNumberAndLockedBy(
                    movieId, theatre, showTime, seat, user);
        }
    }

    // GET CURRENT STATUS
    @Transactional
    public SeatUpdateResponse getSeatStatus(Long movieId,
                                            String theatre,
                                            String showTime) {

        // Clean expired locks
        lockRepo.deleteByExpiryTimeBefore(LocalDateTime.now());

        List<String> lockedSeats =
                lockRepo.findByMovieIdAndTheatreAndShowTime(
                                movieId, theatre, showTime)
                        .stream()
                        .map(SeatLock::getSeatNumber)
                        .collect(Collectors.toList());

        List<Booking> bookings =
                bookingRepo.findByMovieIdAndTheatreAndShowTime(
                        movieId, theatre, showTime);

        List<String> bookedSeats = bookings.stream()
                .flatMap(b -> {
                    if (b.getSeatNumbers() == null || b.getSeatNumbers().isEmpty()) {
                        return List.<String>of().stream();
                    }
                    return List.of(b.getSeatNumbers().split(",")).stream();
                })
                .map(String::trim)
                .collect(Collectors.toList());

        return new SeatUpdateResponse(lockedSeats, bookedSeats);
    }
}