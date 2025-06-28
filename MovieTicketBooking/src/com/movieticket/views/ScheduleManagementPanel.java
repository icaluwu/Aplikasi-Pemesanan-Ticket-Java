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
    private List<Schedule> currentSchedules;

    public ScheduleManagementPanel() {
        setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"No", "Film", "Showtime", "Price", "Available Seats", "Schedule ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new JTable(tableModel);
        scheduleTable.removeColumn(scheduleTable.getColumnModel().getColumn(5)); // Hide Schedule ID

        add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        // Buttons
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
            currentSchedules = AdminController.getAllSchedules();
            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");

            int index = 1;
            for (Schedule schedule : currentSchedules) {
                tableModel.addRow(new Object[]{
                    index++,
                    schedule.getFilmTitle(),
                    sdf.format(schedule.getShowtime()),
                    "Rp" + String.format("%,.2f", schedule.getPrice()),
                    schedule.getAvailableSeats(),
                    schedule.getId()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading schedules: " + e.getMessage());
        }
    }

    private void showAddScheduleDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Add Schedule", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));

        try {
            List<Film> films = AdminController.getAllFilms();
            JComboBox<Film> filmCombo = new JComboBox<>(films.toArray(new Film[0]));

            Timestamp defaultTimestamp = new Timestamp(System.currentTimeMillis() + 3600000);
            JTextField showtimeField = new JTextField(defaultTimestamp.toString().substring(0, 19));

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

                    Schedule schedule = new Schedule(0, selectedFilm.getId(), showtime, price, seats);
                    AdminController.addSchedule(schedule);
                    loadSchedules();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Schedule added successfully!");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid showtime format! Format: yyyy-MM-dd HH:mm:ss");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            });

            dialog.add(new JLabel());
            dialog.add(saveButton);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Error loading films: " + e.getMessage());
        }
    }

    private void showUpdateScheduleDialog() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a schedule to update!");
            return;
        }

        int scheduleId = (int) tableModel.getValueAt(row, 5);

        try {
            Schedule selectedSchedule = currentSchedules.stream()
                    .filter(s -> s.getId() == scheduleId)
                    .findFirst().orElse(null);

            if (selectedSchedule == null) {
                JOptionPane.showMessageDialog(this, "Schedule not found!");
                return;
            }

            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Update Schedule", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setLayout(new GridLayout(5, 2, 10, 10));

            List<Film> films = AdminController.getAllFilms();
            JComboBox<Film> filmCombo = new JComboBox<>(films.toArray(new Film[0]));
            for (Film film : films) {
                if (film.getId() == selectedSchedule.getFilmId()) {
                    filmCombo.setSelectedItem(film);
                }
            }

            JTextField showtimeField = new JTextField(selectedSchedule.getShowtime().toString().substring(0, 19));
            JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(selectedSchedule.getPrice(), 0.0, 1000000.0, 5000.0));
            JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(selectedSchedule.getAvailableSeats(), 1, 500, 1));

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

                    Schedule updated = new Schedule(scheduleId, selectedFilm.getId(), showtime, price, seats);
                    AdminController.updateSchedule(updated);
                    loadSchedules();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Schedule updated successfully!");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid showtime format! Format: yyyy-MM-dd HH:mm:ss");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            });

            dialog.add(new JLabel());
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

        int scheduleId = (int) tableModel.getValueAt(row, 5); // Hidden column for ID
        String filmTitle = (String) tableModel.getValueAt(row, 1);
        String showtime = (String) tableModel.getValueAt(row, 2);

        try {
            // Validasi: apakah schedule digunakan?
            if (AdminController.isScheduleUsed(scheduleId)) {
                JOptionPane.showMessageDialog(this,
                        "Schedule ini tidak dapat dihapus karena sudah memiliki data pemesanan (bookings).");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the schedule for '" + filmTitle + "' at " + showtime + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                AdminController.deleteSchedule(scheduleId);
                loadSchedules();
                JOptionPane.showMessageDialog(this, "Schedule deleted successfully!");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

}
