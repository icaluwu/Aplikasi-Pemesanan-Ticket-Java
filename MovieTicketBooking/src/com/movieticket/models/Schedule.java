package com.movieticket.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Schedule {
    private int id;
    private int filmId;
    private Timestamp showtime;
    private double price;
    private int availableSeats;
    private String filmTitle; // Tambahkan ini

    public Schedule(int id, int filmId, Timestamp showtime, double price, int availableSeats) {
        this.id = id;
        this.filmId = filmId;
        this.showtime = showtime;
        this.price = price;
        this.availableSeats = availableSeats;
    }

    // Getters
    public int getId() { return id; }
    public int getFilmId() { return filmId; }
    public Timestamp getShowtime() { return showtime; }
    public double getPrice() { return price; }
    public int getAvailableSeats() { return availableSeats; }
    public String getFilmTitle() { return filmTitle; } // Tambahkan getter
    
    // Setters
    public void setFilmTitle(String filmTitle) { this.filmTitle = filmTitle; }
    public void setShowtime(Timestamp showtime) { this.showtime = showtime; }
    public void setPrice(double price) { this.price = price; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    
    @Override
public String toString() {
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        return filmTitle + " | " + sdf.format(showtime) + 
               " | Rp" + String.format("%,.2f", price) +
               " | " + availableSeats + " seats";
       } catch (Exception e) {
        return "Invalid date format";
        }
    }
}