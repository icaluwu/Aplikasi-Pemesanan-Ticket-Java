package com.movieticket.controllers;

import com.movieticket.models.Film;
import com.movieticket.models.Schedule;
import com.movieticket.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController {
    // Film CRUD Operations
    public static List<Film> getAllFilms() throws SQLException {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM films";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                films.add(new Film(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getInt("duration"),
                    rs.getString("director"),
                    rs.getString("synopsis")
                ));
            }
        }
        return films;
    }

    public static void addFilm(Film film) throws SQLException {
        String sql = "INSERT INTO films (title, genre, duration, director, synopsis) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, film.getTitle());
            stmt.setString(2, film.getGenre());
            stmt.setInt(3, film.getDuration());
            stmt.setString(4, film.getDirector());
            stmt.setString(5, film.getSynopsis());
            stmt.executeUpdate();
        }
    }

    public static void updateFilm(Film film) throws SQLException {
        String sql = "UPDATE films SET title=?, genre=?, duration=?, director=?, synopsis=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, film.getTitle());
            stmt.setString(2, film.getGenre());
            stmt.setInt(3, film.getDuration());
            stmt.setString(4, film.getDirector());
            stmt.setString(5, film.getSynopsis());
            stmt.setInt(6, film.getId());
            stmt.executeUpdate();
        }
    }

    public static void deleteFilm(int filmId) throws SQLException {
        String sql = "DELETE FROM films WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, filmId);
            stmt.executeUpdate();
        }
    }

    // Schedule CRUD Operations
    public static List<Schedule> getAllSchedules() throws SQLException {
    List<Schedule> schedules = new ArrayList<>();
    String sql = "SELECT s.*, f.title AS film_title FROM schedules s JOIN films f ON s.film_id = f.id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Schedule schedule = new Schedule(
                    rs.getInt("id"),
                    rs.getInt("film_id"),
                    rs.getTimestamp("showtime"),
                    rs.getDouble("price"),
                    rs.getInt("available_seats")
                );
                schedule.setFilmTitle(rs.getString("film_title"));
                schedules.add(schedule);
            }
        }
        return schedules;
    }

    public static void addSchedule(Schedule schedule) throws SQLException {
        String sql = "INSERT INTO schedules (film_id, showtime, price, available_seats) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, schedule.getFilmId());
            stmt.setTimestamp(2, schedule.getShowtime());
            stmt.setDouble(3, schedule.getPrice());
            stmt.setInt(4, schedule.getAvailableSeats());
            stmt.executeUpdate();
        }
    }

    public static void updateSchedule(Schedule schedule) throws SQLException {
        String sql = "UPDATE schedules SET film_id=?, showtime=?, price=?, available_seats=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, schedule.getFilmId());
            stmt.setTimestamp(2, schedule.getShowtime());
            stmt.setDouble(3, schedule.getPrice());
            stmt.setInt(4, schedule.getAvailableSeats());
            stmt.setInt(5, schedule.getId());
            stmt.executeUpdate();
        }
    }

    public static void deleteSchedule(int scheduleId) throws SQLException {
        String sql = "DELETE FROM schedules WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, scheduleId);
            stmt.executeUpdate();
        }
    }
    
    public static boolean isScheduleUsed(int scheduleId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM bookings WHERE schedule_id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, scheduleId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
}

}