package com.movieticket.views;

import com.movieticket.controllers.AdminController;
import com.movieticket.models.Film;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class FilmManagementPanel extends JPanel {
    private JTable filmTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton;

    public FilmManagementPanel() {
        setLayout(new BorderLayout());
        
        // Table setup
        String[] columns = {"ID", "Title", "Genre", "Duration", "Director", "Synopsis"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        filmTable = new JTable(tableModel);
        add(new JScrollPane(filmTable), BorderLayout.CENTER);
        
        // Control buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Film");
        updateButton = new JButton("Update Film");
        deleteButton = new JButton("Delete Film");
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data
        loadFilms();
        
        // Event handlers
        addButton.addActionListener(e -> showAddFilmDialog());
        updateButton.addActionListener(e -> showUpdateFilmDialog());
        deleteButton.addActionListener(e -> deleteFilm());
    }

    private void loadFilms() {
        try {
            List<Film> films = AdminController.getAllFilms();
            tableModel.setRowCount(0);
            for (Film film : films) {
                tableModel.addRow(new Object[]{
                    film.getId(),
                    film.getTitle(),
                    film.getGenre(),
                    film.getDuration(),
                    film.getDirector(),
                    film.getSynopsis()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading films: " + e.getMessage());
        }
    }
    
    private void showAddFilmDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Film");
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        
        JTextField titleField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField directorField = new JTextField();
        JTextArea synopsisArea = new JTextArea();
        JScrollPane synopsisScroll = new JScrollPane(synopsisArea);
        
        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Genre:"));
        dialog.add(genreField);
        dialog.add(new JLabel("Duration (minutes):"));
        dialog.add(durationField);
        dialog.add(new JLabel("Director:"));
        dialog.add(directorField);
        dialog.add(new JLabel("Synopsis:"));
        dialog.add(synopsisScroll);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Film film = new Film(
                    0, // ID will be auto-generated
                    titleField.getText(),
                    genreField.getText(),
                    Integer.parseInt(durationField.getText()),
                    directorField.getText(),
                    synopsisArea.getText()
                );
                
                AdminController.addFilm(film);
                loadFilms();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Film added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Duration must be a number!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        
        dialog.add(new JLabel()); // Empty cell
        dialog.add(saveButton);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showUpdateFilmDialog() {
        int row = filmTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a film to update!");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);
        String genre = (String) tableModel.getValueAt(row, 2);
        int duration = (int) tableModel.getValueAt(row, 3);
        String director = (String) tableModel.getValueAt(row, 4);
        String synopsis = (String) tableModel.getValueAt(row, 5);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Update Film");
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        
        JTextField titleField = new JTextField(title);
        JTextField genreField = new JTextField(genre);
        JTextField durationField = new JTextField(String.valueOf(duration));
        JTextField directorField = new JTextField(director);
        JTextArea synopsisArea = new JTextArea(synopsis);
        JScrollPane synopsisScroll = new JScrollPane(synopsisArea);
        
        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Genre:"));
        dialog.add(genreField);
        dialog.add(new JLabel("Duration (minutes):"));
        dialog.add(durationField);
        dialog.add(new JLabel("Director:"));
        dialog.add(directorField);
        dialog.add(new JLabel("Synopsis:"));
        dialog.add(synopsisScroll);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Film film = new Film(
                    id,
                    titleField.getText(),
                    genreField.getText(),
                    Integer.parseInt(durationField.getText()),
                    directorField.getText(),
                    synopsisArea.getText()
                );
                
                AdminController.updateFilm(film);
                loadFilms();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Film updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Duration must be a number!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        
        dialog.add(new JLabel()); // Empty cell
        dialog.add(saveButton);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteFilm() {
        int row = filmTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a film to delete!");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete '" + title + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                AdminController.deleteFilm(id);
                loadFilms();
                JOptionPane.showMessageDialog(this, "Film deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}