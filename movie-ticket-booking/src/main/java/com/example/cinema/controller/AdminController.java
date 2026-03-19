package com.example.cinema.controller;

import com.example.cinema.entity.User;
import com.example.cinema.repository.UserRepository;
import com.example.cinema.service.OfferService;
import com.example.cinema.entity.Offer;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final OfferService offerService;

    public AdminController(UserRepository userRepository,
                           OfferService offerService) {
        this.userRepository = userRepository;
        this.offerService = offerService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/offer")
    public Offer createOffer(@RequestBody Map<String, String> body) {
        return offerService.createOffer(body.get("message"));
    }
}