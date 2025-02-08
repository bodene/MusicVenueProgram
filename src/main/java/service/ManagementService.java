package service;

import dao.EventDAO;
import dao.VenueDAO;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import model.Venue;
import model.Event;
import model.VenueType;
import util.AlertUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ManagementService {

    private static ManagementService instance;

    private ManagementService() {
        // Private constructor for Singleton Pattern
    }

    public static ManagementService getInstance() {
        if (instance == null) {
            instance = new ManagementService();
        }
        return instance;
    }

    // IMPORT VENUES FROM CSV
    public void importVenuesCSV() {
        File selectedFile = selectCSVFile("Choose Venues CSV File to Import");
        if (selectedFile != null) {
            try {
                List<Venue> venues = CSVHandler.importVenueDataCSV(selectedFile.getAbsolutePath());

                if (!venues.isEmpty()) {
                    for (Venue venue : venues) {
                        boolean success = VenueDAO.addVenue(venue, venue.getVenueTypes().stream()
                                .map(VenueType::getVenueType)
                                .collect(Collectors.toList()));
                        if (!success) {
                            AlertUtils.showAlert("Error", "Failed to add venue: " + venue.getName(), Alert.AlertType.ERROR);
                        }
                    }
                    AlertUtils.showAlert("Success", "Venues imported successfully!", Alert.AlertType.INFORMATION);
                } else {
                    AlertUtils.showAlert("Warning", "No venues found in the CSV file.", Alert.AlertType.WARNING);
                }

            } catch (SQLException | FileNotFoundException e) {
                e.printStackTrace();
                AlertUtils.showAlert("Error", "Failed to import venues: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            AlertUtils.showAlert("Warning", "No file selected.", Alert.AlertType.WARNING);
        }
    }

    // Import Events from CSV
    public void importEventsCSV() {
        File selectedFile = selectCSVFile("Choose Events CSV File to Import");
        if (selectedFile != null) {
            try {
                List<Event> events = CSVHandler.importEventDataCSV(selectedFile.getAbsolutePath());

                if (!events.isEmpty()) {
                    EventDAO.saveEvents(events);
                    AlertUtils.showAlert("Success", "Events imported successfully!", Alert.AlertType.INFORMATION);
                } else {
                    AlertUtils.showAlert("Warning", "No events found in the CSV file.", Alert.AlertType.WARNING);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                AlertUtils.showAlert("Error", "Failed to import events: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            AlertUtils.showAlert("Warning", "No file selected.", Alert.AlertType.WARNING);
        }
    }

    // FileChooser Helper Method
    private File selectCSVFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        return fileChooser.showOpenDialog(null);
    }
}
