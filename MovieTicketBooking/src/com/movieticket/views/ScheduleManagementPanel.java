package com.movieticket.views;

import com.movieticket.controllers.AdminController;
import com.movieticket.models.Film;
import com.movieticket.models.Schedule;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class ScheduleManagementPanel extends JPanel {
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton;

    public ScheduleManagementPanel() {
        setLayout(new BorderLayout());
        
        // Table setup
        String[] columns = {"ID", "Film", "Showtime", "Price", "Available Seats"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new JTable(tableModel);
        add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        
        // Control buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Schedule");
        updateButton = new JButton("Update Schedule");
        deleteButton = new JButton("Delete Schedule");
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data
        loadSchedules();
        
        // Event handlers
        addButton.addActionListener(e -> showAddScheduleDialog());
        updateButton.addActionListener(e -> showUpdateScheduleDialog());
        deleteButton.addActionListener(e -> deleteSchedule());
    }

    private void loadSchedules() {
    try {
        List<Schedule> schedules = AdminController.getAllSchedules();
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        for (Schedule schedule : schedules) {
            tableModel.addRow(new Object[]{
                schedule.getId(),
                schedule.getFilmTitle(),
                sdf.format(schedule.getShowtime()), // Format tanggal di sini
                "Rp" + String.format("%,.2f", schedule.getPrice()),
                schedule.getAvailableSeats()
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading schedules: " + e.getMessage());
    }
}
    
    private void showAddScheduleDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Schedule");
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        
        // Get films for combo box
        List<Film> films = null;
        try {
            films = AdminController.getAllFilms();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Error loading films: " + e.getMessage());
            return;
        }
        
        JComboBox<Film> filmCombo = new JComboBox<>();
        for (Film film : films) {
            filmCombo.addItem(film);
        }
        
        JTextField showtimeField = new JTextField("2023-12-31 19:00:00");
        JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(50000.0, 0.0, 1000000.0, 5000.0));
        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 500, 1));
        
        dialog.add(new JLabel("Film:"));
        dialog.add(filmCombo);
        dialog.add(new JLabel("Showtime (yyyy-MM-dd HH:mm:ss):"));
        dialog.add(showtimeField);
        dialog.add(new JLabel("Price:"));
        dialog.add(priceSpinner);
        dialog.add(new JLabel("Available Seats:"));
        dialog.add(seatsSpinner);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Film selectedFilm = (Film) filmCombo.getSelectedItem();
                Timestamp showtime = Timestamp.valueOf(showtimeField.getText());
                double price = (Double) priceSpinner.getValue();
                int seats = (Integer) seatsSpinner.getValue();
                
                Schedule schedule = new Schedule(
                    0,
                    selectedFilm.getId(),
                    showtime,
                    price,
                    seats
                );
                
                AdminController.addSchedule(schedule);
                loadSchedules();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Schedule added successfully!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid showtime format! Contoh: 2023-12-31 19:00:00");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        
        dialog.add(new JLabel());
        dialog.add(saveButton);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showUpdateScheduleDialog() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a schedule to update!");
            return;
        }
        
        int scheduleId = (int) tableModel.getValueAt(row, 0);
        
        try {
            // Find the selected schedule
            Schedule selectedSchedule = null;
            List<Schedule> schedules = AdminController.getAllSchedules();
            for (Schedule schedule : schedules) {
                if (schedule.getId() == scheduleId) {
                    selectedSchedule = schedule;
                    break;
                }
            }
            
            if (selectedSchedule == null) {
                JOptionPane.showMessageDialog(this, "Schedule not found!");
                return;
            }
            
            JDialog dialog = new JDialog();
            dialog.setTitle("Update Schedule");
            dialog.setLayout(new GridLayout(5, 2, 10, 10));
            
            // Get films for combo box
            List<Film> films = AdminController.getAllFilms();
            JComboBox<Film> filmCombo = new JComboBox<>();
            for (Film film : films) {
                filmCombo.addItem(film);
                if (film.getId() == selectedSchedule.getFilmId()) {
                    filmCombo.setSelectedItem(film);
                }
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JTextField showtimeField = new JTextField(sdf.format(selectedSchedule.getShowtime()));
            JTextField priceField = new JTextField(String.valueOf(selectedSchedule.getPrice()));
            JTextField seatsField = new JTextField(String.valueOf(selectedSchedule.getAvailableSeats()));
            
            dialog.add(new JLabel("Film:"));
            dialog.add(filmCombo);
            dialog.add(new JLabel("Showtime (yyyy-MM-dd HH:mm:ss):"));
            dialog.add(showtimeField);
            dialog.add(new JLabel("Price:"));
            dialog.add(priceField);
            dialog.add(new JLabel("Available Seats:"));
            dialog.add(seatsField);
            
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                try {
                    Film selectedFilm = (Film) filmCombo.getSelectedItem();
                    Timestamp showtime = Timestamp.valueOf(showtimeField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    int seats = Integer.parseInt(seatsField.getText());
                    
                    Schedule updatedSchedule = new Schedule(
                        scheduleId,
                        selectedFilm.getId(),
                        showtime,
                        price,
                        seats
                    );
                    
                    AdminController.updateSchedule(updatedSchedule);
                    loadSchedules();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Schedule updated successfully!");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid showtime format! Use yyyy-MM-dd HH:mm:ss");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            });
            
            dialog.add(new JLabel()); // Empty cell
            dialog.add(saveButton);
            
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void deleteSchedule() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a schedule to delete!");
            return;
        }
        
        int scheduleId = (int) tableModel.getValueAt(row, 0);
        String filmTitle = (String) tableModel.getValueAt(row, 1);
        String showtime = (String) tableModel.getValueAt(row, 2);
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete schedule for '" + filmTitle + "' at " + showtime + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                AdminController.deleteSchedule(scheduleId);
                loadSchedules();
                JOptionPane.showMessageDialog(this, "Schedule deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}