package com.example.cinema.dto;

import java.util.List;

public class SeatUpdateResponse {

    private List<String> locked;
    private List<String> booked;

    public SeatUpdateResponse(List<String> locked, List<String> booked) {
        this.locked = locked;
        this.booked = booked;
    }

    public List<String> getLocked() {
        return locked;
    }

    public List<String> getBooked() {
        return booked;
    }
}