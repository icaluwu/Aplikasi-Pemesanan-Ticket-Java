package com.movieticket.models;

public class Film {
    private int id;
    private String title;
    private String genre;
    private int duration;
    private String director;
    private String synopsis;

    public Film(int id, String title, String genre, int duration, String director, String synopsis) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.director = director;
        this.synopsis = synopsis;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getDuration() { return duration; }
    public String getDirector() { return director; }
    public String getSynopsis() { return synopsis; }
    
    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setDirector(String director) { this.director = director; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }
    
    @Override
    public String toString() {
        return title; // Hanya menampilkan judul film
    }
}