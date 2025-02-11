package controller;

import com.sun.jdi.request.EventRequest;
import dao.*;
import javafx.scene.control.Alert;
import model.*;
import service.BackupHandler;
import service.ManagementService;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import util.AlertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManagerController {
    private final ManagementService managementService = ManagementService.getInstance();

    @FXML
    private Button viewVenuesButton, viewBookingsButton, staffManagementButton, managerSummaryButton,
            importVenuesCSVButton, importEventsCSVButton, backupTransactionDataButton,
            restoreTransactionDataButton, backupMasterDataButton, restoreMasterDataButton, dashboardButton, logoutButton;

    // View Venues Page
    @FXML
    private void viewVenues() {
        SceneManager.switchScene("view-venue-details.fxml");
    }

    // View Orders Page
    @FXML
    private void viewBookings() {SceneManager.switchScene("bookings-view.fxml");}

    // User Management
    @FXML private void staffManagement() {SceneManager.switchScene("staff-management-view.fxml");}

    // View Management Summary
    @FXML private void managerSummary() {
        SceneManager.switchScene("management-summary.fxml");
    }

    // Import Venues CSV
    @FXML private void importVenuesCSV() {
        managementService.importVenuesCSV();
    }

    // Import Events CSV
    @FXML private void importEventsCSV() {
        managementService.importEventsCSV();
    }

    @FXML
    private void backupTransactionData() {
        try {
            List<Booking> bookings = BookingDAO.getAllBookingsBU();
            List<Event> events = EventDAO.getAllEventsBU();
            List<Venue> venues = VenueDAO.getAllVenuesBU();  // Venues without types yet
            List<VenueType> venueTypes = VenueTypeDAO.getAllVenueTypesBU();  // Fetch all venue types
            Map<Integer, List<Integer>> venueTypeVenueMap = VenueTypeDAO.getAllVenueTypesVenuesBU();  // Venue-to-type mapping

            // Associate each venue with its venue types
            for (Venue venue : venues) {
                List<Integer> typeIds = venueTypeVenueMap.getOrDefault(venue.getVenueId(), new ArrayList<>());
                List<VenueType> typesForVenue = typeIds.stream()
                        .map(id -> venueTypes.stream().filter(type -> type.getVenueTypeId() == id).findFirst().orElse(null))
                        .filter(type -> type != null)  // Ensure no nulls
                        .toList();

                venue.setVenueTypes(typesForVenue);  // Venue has a method to set a list of VenueType objects
            }

            // Backup the data
            BackupHandler.backupTransactionData(bookings, events, venues);

            // Show a summary popup
            String summary = String.format("""
                Backup Successful:
                - Bookings: %d
                - Events: %d
                - Venues: %d
                """, bookings.size(), events.size(), venues.size());

            AlertUtils.showAlert("Backup Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Backup Failed", "An error occurred during the backup process: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void restoreTransactionData() {
        try {
            List<Booking> bookings = BackupHandler.restoreBookings();
            List<Event> events = BackupHandler.restoreEvents();
            List<Venue> venues = BackupHandler.restoreVenues();

            // Show a summary popup
            String summary = String.format("""
                Restore Successful:
                - Bookings: %d
                - Events: %d
                - Venues: %d
                """, bookings.size(), events.size(), venues.size());

            AlertUtils.showAlert("Restore Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Restore Failed", "An error occurred during the restore process: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void backupMasterData() {
        try {
            List<User> users = UserDAO.getAllUsers();
            List<Client> clients = ClientDAO.getAllClientsBU();

            BackupHandler.backupMasterData(users, clients);

            String summary = String.format("""
                Backup Successful:
                - Users: %d
                - Clients: %d
                """, users.size(), clients.size());

            AlertUtils.showAlert("Master Data Backup Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Backup Failed", "An error occurred during the master data backup process.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void restoreMasterData() {
        try {
            List<User> users = BackupHandler.restoreUsers();
            List<Client> clients = BackupHandler.restoreClients();

            String summary = String.format("""
                Restore Successful:
                - Users: %d
                - Clients: %d
                """, users.size(), clients.size());

            AlertUtils.showAlert("Master Data Restore Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Restore Failed", "An error occurred during the master data restore process.", Alert.AlertType.ERROR);
        }
    }

    @FXML private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }

    @FXML private void logout() {SceneManager.switchScene("main-view.fxml");}
}