package com.example.cinema.repository;

import com.example.cinema.entity.SeatLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatLockRepository extends JpaRepository<SeatLock, Long> {

    void deleteByExpiryTimeBefore(LocalDateTime time);

    boolean existsByMovieIdAndTheatreAndShowTimeAndSeatNumber(
            Long movieId,
            String theatre,
            String showTime,
            String seatNumber);

    List<SeatLock> findByMovieIdAndTheatreAndShowTime(
            Long movieId,
            String theatre,
            String showTime);

    void deleteByMovieIdAndTheatreAndShowTime(
            Long movieId,
            String theatre,
            String showTime);

    void deleteByMovieIdAndTheatreAndShowTimeAndSeatNumberAndLockedBy(
            Long movieId,
            String theatre,
            String showTime,
            String seatNumber,
            String lockedBy);
}