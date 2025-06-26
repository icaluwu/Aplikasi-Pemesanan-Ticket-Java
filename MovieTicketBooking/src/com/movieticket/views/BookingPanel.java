package com.movieticket.views;

import com.movieticket.controllers.UserController;
import com.movieticket.models.Schedule;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BookingPanel extends JPanel {
    private JComboBox<Schedule> scheduleComboBox;
    private JSpinner ticketSpinner;
    private JLabel totalLabel;
    private String username;

    public BookingPanel(String username) {
        this.username = username;
        setLayout(new GridLayout(5, 2, 10, 10));
        
        // Schedule selection
        add(new JLabel("Select Schedule:"));
        scheduleComboBox = new JComboBox<>();
        loadSchedules();
        add(scheduleComboBox);
        
        // Ticket quantity
        add(new JLabel("Number of Tickets:"));
        ticketSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        add(ticketSpinner);
        
        // Total price
        add(new JLabel("Total Price:"));
        totalLabel = new JLabel("Rp 0");
        add(totalLabel);
        
        // Book button
        JButton bookButton = new JButton("Book Now");
        bookButton.addActionListener(e -> bookTickets());
        add(bookButton);
        
        // Add listener for price calculation
        scheduleComboBox.addActionListener(e -> updateTotal());
        ticketSpinner.addChangeListener(e -> updateTotal());
    }
    
    private void loadSchedules() {
        try {
            List<Schedule> schedules = UserController.getAvailableSchedules();
            for (Schedule s : schedules) {
                scheduleComboBox.addItem(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void updateTotal() {
        Schedule selected = (Schedule) scheduleComboBox.getSelectedItem();
        int tickets = (int) ticketSpinner.getValue();
        if (selected != null) {
            double total = selected.getPrice() * tickets;
            totalLabel.setText("Rp " + String.format("%,.2f", total));
        }
    }
    
    private void bookTickets() {
    Schedule selected = (Schedule) scheduleComboBox.getSelectedItem();
    int tickets = (int) ticketSpinner.getValue();
    
    // Validasi kursi tersedia
    if (tickets > selected.getAvailableSeats()) {
        JOptionPane.showMessageDialog(this, 
            "Not enough seats available! Only " + selected.getAvailableSeats() + " seats left.");
        return;
    }
    
    try {
        if (UserController.bookTicket(username, selected.getId(), tickets)) {
            JOptionPane.showMessageDialog(this, "Booking successful!");
            
            // Update available seats in UI
            selected.setAvailableSeats(selected.getAvailableSeats() - tickets);
            scheduleComboBox.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to book tickets!");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
    }
}