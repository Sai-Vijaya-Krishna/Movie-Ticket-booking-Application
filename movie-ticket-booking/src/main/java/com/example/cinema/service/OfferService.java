package com.example.cinema.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cinema.entity.Offer;
import com.example.cinema.repository.OfferRepository;

@Service
public class OfferService {

    private final OfferRepository repo;
    private final SimpMessagingTemplate messagingTemplate;

    public OfferService(OfferRepository repo,
                        SimpMessagingTemplate messagingTemplate) {
        this.repo = repo;
        this.messagingTemplate = messagingTemplate;
    }

    public Offer createOffer(String message) {

        Offer offer = new Offer();
        offer.setMessage(message);
        offer.setExpiryTime(LocalDateTime.now().plusMinutes(1));

        Offer saved = repo.save(offer);

        // Notify clients of new offer
        messagingTemplate.convertAndSend("/topic/offers", saved);

        return saved;
    }

    public List<Offer> getActiveOffers() {
        return repo.findByExpiryTimeAfter(LocalDateTime.now());
    }

    // 🔥 Scheduled task to expire offers every 60 seconds
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireOffers() {
        List<Offer> expired = repo.findByExpiryTimeBefore(LocalDateTime.now());
        expired.forEach(o -> 
            messagingTemplate.convertAndSend("/topic/offers", "expired"));
    }
}