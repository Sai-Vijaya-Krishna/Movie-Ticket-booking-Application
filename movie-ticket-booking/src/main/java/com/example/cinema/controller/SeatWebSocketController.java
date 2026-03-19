package com.example.cinema.controller;

import com.example.cinema.dto.SeatLockRequest;
import com.example.cinema.dto.SeatUpdateResponse;
import com.example.cinema.service.SeatLockService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SeatWebSocketController {

    private final SeatLockService service;
    private final SimpMessagingTemplate template;

    public SeatWebSocketController(SeatLockService service,
                                   SimpMessagingTemplate template) {
        this.service = service;
        this.template = template;
    }

    @MessageMapping("/lock-seat")
    public void lockSeat(SeatLockRequest request) {

        service.lockSeat(
                request.getMovieId(),
                request.getTheatre(),
                request.getShowTime(),
                request.getSeatNumber(),
                request.getUser()
        );

        broadcastUpdatedSeats(request);
    }

    @MessageMapping("/unlock-seat")
    public void unlockSeat(SeatLockRequest request) {

        service.unlockSeat(
                request.getMovieId(),
                request.getTheatre(),
                request.getShowTime(),
                request.getSeatNumber(),
                request.getUser()
        );

        broadcastUpdatedSeats(request);
    }

    private void broadcastUpdatedSeats(SeatLockRequest request) {

        SeatUpdateResponse response = service.getSeatStatus(
                request.getMovieId(),
                request.getTheatre(),
                request.getShowTime()
        );

        template.convertAndSend("/topic/seats-updates", response);
    }
}