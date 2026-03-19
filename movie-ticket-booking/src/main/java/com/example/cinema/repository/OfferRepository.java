package com.example.cinema.repository;

import com.example.cinema.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    // Get active offers (expiry after now)
    List<Offer> findByExpiryTimeAfter(LocalDateTime time);

    // Get expired offers (expiry before now) → Needed for your scheduled task
    List<Offer> findByExpiryTimeBefore(LocalDateTime time);
}