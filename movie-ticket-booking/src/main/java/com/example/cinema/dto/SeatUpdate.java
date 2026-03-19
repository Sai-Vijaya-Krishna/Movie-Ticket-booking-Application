package com.example.cinema.dto;

public class SeatUpdate {

    private Long movieId;
    private String theatre;
    private String showTime;
    private String seatNumbers;

    public SeatUpdate(Long movieId, String theatre, String showTime, String seatNumbers) {
        this.movieId = movieId;
        this.theatre = theatre;
        this.showTime = showTime;
        this.seatNumbers = seatNumbers;
    }

    public Long getMovieId() { return movieId; }
    public String getTheatre() { return theatre; }
    public String getShowTime() { return showTime; }
    public String getSeatNumbers() { return seatNumbers; }
}
