package com.example.cinema.service;

import com.example.cinema.entity.Admin;
import com.example.cinema.entity.User;
import com.example.cinema.repository.AdminRepository;
import com.example.cinema.repository.UserRepository;
import com.example.cinema.security.JwtUtil;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil,
                       AdminRepository adminRepository,
                       UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String loginUser(String mobile) {

        if (mobile == null || mobile.isBlank()) {
            throw new IllegalArgumentException("Mobile number is required");
        }

        User user = userRepository.findByMobile(mobile)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setMobile(mobile);
                    newUser.setUsername("USER");
                    return userRepository.save(newUser);
                });

        return jwtUtil.generateToken(user.getMobile(), "USER");
    }

    public String adminStep1(String email, String password) {

        if (email == null || password == null) {
            throw new RuntimeException("Email and password required");
        }

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String otp = String.valueOf((int)(Math.random() * 9000) + 1000);
        admin.setOtp(otp);
        admin.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        adminRepository.save(admin);

        return otp;
    }

    public String adminStep2(String email, String otp) {

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (admin.getOtp() == null || !admin.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        if (admin.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP expired");
        }

        return jwtUtil.generateToken(admin.getEmail(), "ADMIN");
    }
}