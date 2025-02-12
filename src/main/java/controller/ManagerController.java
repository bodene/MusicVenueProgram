package controller;

import dao.*;
import model.*;
import service.BackupHandler;
import service.ManagementService;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import util.AlertUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Controller class for handling various manager operations.
 * <p>
 * This class provides functionality for manager-specific actions such as navigating to different views
 * (venues, bookings, user management, management summary), importing CSV files, performing backups and restores
 * of both transactional and master data, and logging out. It leverages the {@link ManagementService} for CSV imports
 * and the {@link BackupHandler} for data backup/restore operations.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class ManagerController {

    /** Singleton instance of ManagementService used to perform management operations. */
    private final ManagementService managementService = ManagementService.getInstance();

    /**
     * Navigates to the view displaying venue details.
     * <p>
     * This method uses the {@link SceneManager} to switch the current scene to "view-venue-details.fxml".
     * </p>
     */
    @FXML private void viewVenues() {
        SceneManager.switchScene("view-venue-details.fxml");
    }

    /**
     * Navigates to the view displaying bookings.
     * <p>
     * This method switches the scene to "bookings-view.fxml" using the {@link SceneManager}.
     * </p>
     */
    @FXML private void viewBookings() {
        SceneManager.switchScene("bookings-view.fxml");
    }

    /**
     * Navigates to the staff management view.
     * <p>
     * This method is used for user management, switching to "staff-management-view.fxml".
     * </p>
     */
    @FXML private void staffManagement() {
        SceneManager.switchScene("staff-management-view.fxml");
    }

    /**
     * Navigates to the management summary view.
     * <p>
     * This method switches the scene to "management-summary.fxml" using the {@link SceneManager}.
     * </p>
     */
    @FXML private void managerSummary() {
        SceneManager.switchScene("management-summary.fxml");
    }

    /**
     * Imports venue data from a CSV file.
     * <p>
     * This method delegates the CSV import operation for venues to the {@link ManagementService}.
     * </p>
     */
    @FXML private void importVenuesCSV() {
        managementService.importVenuesCSV();
    }

    /**
     * Imports event data from a CSV file.
     * <p>
     * This method delegates the CSV import operation for events to the {@link ManagementService}.
     * </p>
     */
    @FXML private void importEventsCSV() {
        managementService.importEventsCSV();
    }

    /**
     * Backs up transactional data.
     * <p>
     * This method retrieves backup data for bookings, events, and venues from the corresponding DAO methods.
     * It then associates each venue with its types using mapping data retrieved from {@code VenueTypeDAO},
     * and finally calls {@link BackupHandler#backupTransactionData(List, List, List)} to perform the backup.
     * A summary popup is displayed upon success.
     * </p>
     */
    @FXML
    private void backupTransactionData() {
        try {
            // Retrieve transactional data for backup.
            List<Booking> bookings = BookingDAO.getAllBookingsBU();
            List<Event> events = EventDAO.getAllEventsBU();
            List<Venue> venues = VenueDAO.getAllVenuesBU();                                           // Venues without types.
            List<VenueType> venueTypes = VenueTypeDAO.getAllVenueTypesBU();                           // Fetch all venue types.
            Map<Integer, List<Integer>> venueTypeVenueMap = VenueTypeDAO.getAllVenueTypesVenuesBU();  // Mapping between venues and their type IDs.

            // Associate each venue with its corresponding types.
            for (Venue venue : venues) {
                // Get the list of type IDs for this venue or an empty list if none are found.
                List<Integer> typeIds = venueTypeVenueMap.getOrDefault(venue.getVenueId(), new ArrayList<>());

                // Map the type IDs to actual VenueType objects.
                List<VenueType> typesForVenue = typeIds.stream()
                        .map(id -> venueTypes.stream().filter(type -> type.getVenueTypeId() == id).findFirst().orElse(null))
                        .filter(type -> type != null)
                        .toList();

                venue.setVenueTypes(typesForVenue);
            }

            // Perform the backup using the BackupHandler.
            BackupHandler.backupTransactionData(bookings, events, venues);

            // Build a summary message.
            String summary = String.format("""
                Backup Successful:
                - Bookings: %d
                - Events: %d
                - Venues: %d
                """, bookings.size(), events.size(), venues.size());

            // Display the summary in an information alert.
            AlertUtils.showAlert("Backup Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            // Display an error alert if any exception occurs.
            AlertUtils.showAlert("Backup Failed", "An error occurred during the backup process: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Restores transactional data.
     * <p>
     * This method attempts to restore bookings, events, and venues using {@link BackupHandler}. It then displays
     * a summary popup indicating the number of restored records for each type.
     * </p>
     */
    @FXML
    private void restoreTransactionData() {
        try {
            // Restore transactional data from backup.
            List<Booking> bookings = BackupHandler.restoreBookings();
            List<Event> events = BackupHandler.restoreEvents();
            List<Venue> venues = BackupHandler.restoreVenues();

            // Build a summary message.
            String summary = String.format("""
                Restore Successful:
                - Bookings: %d
                - Events: %d
                - Venues: %d
                """, bookings.size(), events.size(), venues.size());

            // Display the summary in an information alert.
            AlertUtils.showAlert("Restore Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            // Display an error alert if an exception occurs.
            AlertUtils.showAlert("Restore Failed", "An error occurred during the restore process: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Backs up master data.
     * <p>
     * This method retrieves user and client data using the corresponding DAO methods and then backs up the data via
     * {@link BackupHandler#backupMasterData(List, List)}. A summary popup is displayed upon successful backup.
     * </p>
     */
    @FXML
    private void backupMasterData() {
        try {
            // Retrieve master data.
            List<User> users = UserDAO.getAllUsers();
            List<Client> clients = ClientDAO.getAllClientsBU();

            // Perform the backup.
            BackupHandler.backupMasterData(users, clients);

            // Build a summary message.
            String summary = String.format("""
                Backup Successful:
                - Users: %d
                - Clients: %d
                """, users.size(), clients.size());

            // Display the summary in an information alert.
            AlertUtils.showAlert("Master Data Backup Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            // Display an error alert in case of failure.
            AlertUtils.showAlert("Backup Failed", "An error occurred during the master data backup process.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Restores master data.
     * <p>
     * This method attempts to restore user and client data via the {@link BackupHandler} and displays a summary popup
     * indicating the number of restored records.
     * </p>
     */
    @FXML
    private void restoreMasterData() {
        try {
            // Restore master data.
            List<User> users = BackupHandler.restoreUsers();
            List<Client> clients = BackupHandler.restoreClients();

            // Build a summary message.
            String summary = String.format("""
                Restore Successful:
                - Users: %d
                - Clients: %d
                """, users.size(), clients.size());

            // Display the summary in an information alert.
            AlertUtils.showAlert("Master Data Restore Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            // Display an error alert if an exception occurs.
            AlertUtils.showAlert("Restore Failed", "An error occurred during the master data restore process.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Navigates to the dashboard view.
     * <p>
     * This method switches the current scene to "dashboard.fxml" using the {@link SceneManager}.
     * </p>
     */
    @FXML private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }


    /**
     * Logs out the current user.
     * <p>
     * This method switches the scene to "main-view.fxml", effectively logging out the user.
     * </p>
     */
    @FXML private void logout() {
        SceneManager.switchScene("main-view.fxml");
    }
}