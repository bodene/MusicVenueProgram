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

/**
 * Provides management-level services for importing data and performing other administrative tasks.
 * <p>
 * The {@code ManagementService} class implements the Singleton pattern and provides methods to import
 * venues and events from CSV files. It uses helper classes like {@link CSVHandler} to parse CSV files,
 * and DAO classes to save data to the database. The service also uses a {@code FileChooser} to let users
 * select CSV files for import.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class ManagementService {

    /**
     * The singleton instance of ManagementService.
     */
    private static ManagementService instance;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private ManagementService() {}

    /**
     * Returns the singleton instance of ManagementService.
     *
     * @return the single instance of ManagementService
     */
    public static ManagementService getInstance() {
        if (instance == null) {
            instance = new ManagementService();
        }
        return instance;
    }

    /**
     * Imports venues from a CSV file.
     * <p>
     * This method opens a file chooser for the user to select a CSV file containing venue data.
     * It then uses the {@link CSVHandler#importVenueDataCSV(String)} method to parse the file into a list of
     * {@code Venue} objects. Each venue is then saved into the database via {@link dao.VenueDAO#addVenue(Venue, List)}.
     * Appropriate alerts are displayed to inform the user of success, warnings, or errors.
     * </p>
     */
    public void importVenuesCSV() {
        // Open a file chooser to select the CSV file.
        File selectedFile = selectCSVFile("Choose Venues CSV File to Import");
        if (selectedFile != null) {
            try {
                // Parse CSV file into a list of Venue objects.
                List<Venue> venues = CSVHandler.importVenueDataCSV(selectedFile.getAbsolutePath());

                if (!venues.isEmpty()) {
                    // For each venue, attempt to add it to the database.
                    for (Venue venue : venues) {
                        boolean success = VenueDAO.addVenue(venue,
                                venue.getVenueTypes().stream()
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

    /**
     * Imports events from a CSV file.
     * <p>
     * This method opens a file chooser for the user to select a CSV file containing event data.
     * It then uses the {@link CSVHandler#importEventDataCSV(String)} method to parse the file into a list of
     * {@code Event} objects. The events are saved to the database via {@link dao.EventDAO#saveEvents(List)}.
     * Appropriate alerts are displayed to notify the user of the outcome.
     * </p>
     */
    public void importEventsCSV() {
        // Open a file chooser to select the CSV file.
        File selectedFile = selectCSVFile("Choose Events CSV File to Import");
        if (selectedFile != null) {
            try {
                // Parse CSV file into a list of Event objects.
                List<Event> events = CSVHandler.importEventDataCSV(selectedFile.getAbsolutePath());

                if (!events.isEmpty()) {
                    // Save the list of events to the database.
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

    /**
     * Opens a file chooser dialog to allow the user to select a CSV file.
     * <p>
     * This helper method configures the {@code FileChooser} to only show CSV files.
     * </p>
     *
     * @param title the title of the file chooser dialog
     * @return the selected {@code File}, or {@code null} if no file is selected
     */
    private File selectCSVFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        // Add a file extension filter for CSV files.
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        return fileChooser.showOpenDialog(null);
    }
}
