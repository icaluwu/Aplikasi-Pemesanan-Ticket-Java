package com.movieticket.views;

import com.movieticket.utils.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class MyBookingsPanel extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private String username;
    private JButton deleteButton;

    public MyBookingsPanel(String username) {
        this.username = username;
        setLayout(new BorderLayout());

        // Tambahkan kolom "ID (hidden)" untuk referensi penghapusan
        String[] columns = {"ID", "Film", "Showtime", "Tickets", "Total Price", "Booking Time"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsTable = new JTable(tableModel);
        bookingsTable.removeColumn(bookingsTable.getColumnModel().getColumn(0)); // Sembunyikan kolom ID
        add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        // Tombol hapus
        deleteButton = new JButton("Delete Booking");
        deleteButton.addActionListener(e -> deleteBooking());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load data
        loadBookings();
    }

    private void loadBookings() {
        try {
            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");

            String sql = "SELECT b.id, f.title, s.showtime, b.num_tickets, b.total_price, b.booking_time " +
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
                        rs.getInt("id"),
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

    private void deleteBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.");
            return;
        }

        // Ambil ID booking (kolom tersembunyi)
        int modelRow = bookingsTable.convertRowIndexToModel(selectedRow);
        int bookingId = (int) tableModel.getValueAt(modelRow, 0); // kolom 0 = ID
        String film = (String) tableModel.getValueAt(modelRow, 1);
        String showtime = (String) tableModel.getValueAt(modelRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete booking for '" + film + "' at " + showtime + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM bookings WHERE id = ?")) {

                stmt.setInt(1, bookingId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    loadBookings();
                    JOptionPane.showMessageDialog(this, "Booking deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete booking.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting booking: " + e.getMessage());
            }
        }
    }
}
