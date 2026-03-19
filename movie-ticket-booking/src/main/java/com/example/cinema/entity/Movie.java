	package com.example.cinema.entity;
	
	import jakarta.persistence.*;
	
	@Entity
	@Table(name = "movies")
	public class Movie {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	
	    private String title;
	    private String genre;
	    private int duration;
	    private double price;
	    private int availableSeats;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getGenre() {
			return genre;
		}
		public void setGenre(String genre) {
			this.genre = genre;
		}
		public int getDuration() {
			return duration;
		}
		public void setDuration(int duration) {
			this.duration = duration;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public int getAvailableSeats() {
			return availableSeats;
		}
		public void setAvailableSeats(int availableSeats) {
			this.availableSeats = availableSeats;
		}
		public Object getCode() {
			// TODO Auto-generated method stub
			return null;
		}
		public void setCode(Object code) {
			// TODO Auto-generated method stub
			
		}
	
	}
