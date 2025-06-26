package com.movieticket.controllers;

import com.movieticket.models.Schedule;
import com.movieticket.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    public static List<Schedule> getAvailableSchedules() throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT s.*, f.title AS film_title " +
                     "FROM schedules s " +
                     "JOIN films f ON s.film_id = f.id";

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

    public static boolean bookTicket(String username, int scheduleId, int numTickets) throws SQLException {
        String checkSql = "SELECT available_seats, price FROM schedules WHERE id = ? FOR UPDATE";
        String insertSql = "INSERT INTO bookings (user_id, schedule_id, num_tickets, total_price) " +
                           "VALUES ((SELECT id FROM users WHERE username = ?), ?, ?, ?);";
        String updateSql = "UPDATE schedules SET available_seats = available_seats - ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1) Cek sisa kursi dan harga per tiket
                int availableSeats;
                double price;
                try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                    psCheck.setInt(1, scheduleId);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Jadwal tidak ditemukan: id=" + scheduleId);
                        }
                        availableSeats = rs.getInt("available_seats");
                        price = rs.getDouble("price");
                    }
                }

                // 2) Validasi jumlah tiket
                if (numTickets <= 0) {
                    throw new IllegalArgumentException("Jumlah tiket harus lebih besar dari 0.");
                }
                if (numTickets > availableSeats) {
                    throw new IllegalArgumentException(
                        "Jumlah tiket (" + numTickets + ") melebihi sisa kursi (" + availableSeats + ").");
                }

                // 3) INSERT booking
                double totalPrice = price * numTickets;
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setString(1, username);
                    psInsert.setInt(2, scheduleId);
                    psInsert.setInt(3, numTickets);
                    psInsert.setDouble(4, totalPrice);
                    psInsert.executeUpdate();
                }

                // 4) UPDATE sisa kursi
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setInt(1, numTickets);
                    psUpdate.setInt(2, scheduleId);
                    psUpdate.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
