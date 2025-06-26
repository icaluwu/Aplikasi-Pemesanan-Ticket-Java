package com.movieticket.views;

import com.movieticket.utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class MyBookingsPanel extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private String username;

    public MyBookingsPanel(String username) {
        this.username = username;
        setLayout(new BorderLayout());
        
        // Table setup
        String[] columns = {"Film", "Showtime", "Tickets", "Total Price", "Booking Time"};
        tableModel = new DefaultTableModel(columns, 0);
        bookingsTable = new JTable(tableModel);
        add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
        
        // Load data
        loadBookings();
    }

    private void loadBookings() {
        try {
            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
            
            // Query untuk mendapatkan riwayat pemesanan user
            String sql = "SELECT f.title, s.showtime, b.num_tickets, b.total_price, b.booking_time " +
                         "FROM bookings b " +
                         "JOIN schedules s ON b.schedule_id = s.id " +
                         "JOIN films f ON s.film_id = f.id " +
                         "JOIN users u ON b.user_id = u.id " +
                         "WHERE u.username = ?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("title"),
                        sdf.format(rs.getTimestamp("showtime")),
                        rs.getInt("num_tickets"),
                        "Rp" + String.format("%,.2f", rs.getDouble("total_price")),
                        sdf.format(rs.getTimestamp("booking_time"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage());
        }
    }
}