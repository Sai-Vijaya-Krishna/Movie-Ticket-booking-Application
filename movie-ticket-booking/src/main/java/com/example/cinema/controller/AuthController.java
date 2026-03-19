package com.example.cinema.controller;

import com.example.cinema.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // USER LOGIN
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) {
        String mobile = req.get("mobile");
        String token = authService.loginUser(mobile);
        return Map.of("token", token);
    }

    // ADMIN STEP 1
    @PostMapping("/admin-login-step1")
    public Map<String, String> step1(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String password = req.get("password");

        String otp = authService.adminStep1(email, password);
        return Map.of("otp", otp);
    }

    // ADMIN STEP 2
    @PostMapping("/admin-login-step2")
    public Map<String, String> step2(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String otp = req.get("otp");
        String token = authService.adminStep2(email, otp);
        return Map.of("token", token);
    }
}