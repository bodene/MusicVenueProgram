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

public class ManagerController {
    private final ManagementService managementService = ManagementService.getInstance();

    // VIEW VENUES PAGE
    @FXML private void viewVenues() {
        SceneManager.switchScene("view-venue-details.fxml");
    }

    // VIEW BOOKINGS PAGE
    @FXML private void viewBookings() {SceneManager.switchScene("bookings-view.fxml");}

    // USER MANAGEMENT
    @FXML private void staffManagement() {SceneManager.switchScene("staff-management-view.fxml");}

    // VIEW MANAGEMENT SUMMARY
    @FXML private void managerSummary() {
        SceneManager.switchScene("management-summary.fxml");
    }

    // IMPORT VENUES CSV
    @FXML private void importVenuesCSV() {
        managementService.importVenuesCSV();
    }

    // IMPORT EVENTS CSV
    @FXML private void importEventsCSV() {
        managementService.importEventsCSV();
    }

    // BACK-UP TRANSACTION DATA
    @FXML
    private void backupTransactionData() {
        try {
            List<Booking> bookings = BookingDAO.getAllBookingsBU();
            List<Event> events = EventDAO.getAllEventsBU();
            List<Venue> venues = VenueDAO.getAllVenuesBU();  // VENUES WITHOUT TYPES
            List<VenueType> venueTypes = VenueTypeDAO.getAllVenueTypesBU();  // FETCH ALL VENUE TYPES
            Map<Integer, List<Integer>> venueTypeVenueMap = VenueTypeDAO.getAllVenueTypesVenuesBU();  // VENUE-TO-TYPE MATCHING

            // ASSOCIATE EACH VENUE WITH ITS TYPES
            for (Venue venue : venues) {
                List<Integer> typeIds = venueTypeVenueMap.getOrDefault(venue.getVenueId(), new ArrayList<>());
                List<VenueType> typesForVenue = typeIds.stream()
                        .map(id -> venueTypes.stream().filter(type -> type.getVenueTypeId() == id).findFirst().orElse(null))
                        .filter(type -> type != null)
                        .toList();

                venue.setVenueTypes(typesForVenue);
            }

            // BACKUP DATA
            BackupHandler.backupTransactionData(bookings, events, venues);

            // SUMMARY POP-UP
            String summary = String.format("""
                Backup Successful:
                - Bookings: %d
                - Events: %d
                - Venues: %d
                """, bookings.size(), events.size(), venues.size());

            AlertUtils.showAlert("Backup Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            AlertUtils.showAlert("Backup Failed", "An error occurred during the backup process: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // RESTORE TRANSACTION DATA
    @FXML
    private void restoreTransactionData() {
        try {
            List<Booking> bookings = BackupHandler.restoreBookings();
            List<Event> events = BackupHandler.restoreEvents();
            List<Venue> venues = BackupHandler.restoreVenues();

            // SUMMARY POP-UP
            String summary = String.format("""
                Restore Successful:
                - Bookings: %d
                - Events: %d
                - Venues: %d
                """, bookings.size(), events.size(), venues.size());

            AlertUtils.showAlert("Restore Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            AlertUtils.showAlert("Restore Failed", "An error occurred during the restore process: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // BACKUP MASTER DATA
    @FXML
    private void backupMasterData() {
        try {
            List<User> users = UserDAO.getAllUsers();
            List<Client> clients = ClientDAO.getAllClientsBU();

            BackupHandler.backupMasterData(users, clients);

            // POP-UP SUMMARY
            String summary = String.format("""
                Backup Successful:
                - Users: %d
                - Clients: %d
                """, users.size(), clients.size());

            AlertUtils.showAlert("Master Data Backup Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            AlertUtils.showAlert("Backup Failed", "An error occurred during the master data backup process.", Alert.AlertType.ERROR);
        }
    }

    // RESTORE MASTER BACKUP
    @FXML
    private void restoreMasterData() {

        try {
            List<User> users = BackupHandler.restoreUsers();
            List<Client> clients = BackupHandler.restoreClients();

            // POP-UP SUMMARY
            String summary = String.format("""
                Restore Successful:
                - Users: %d
                - Clients: %d
                """, users.size(), clients.size());

            AlertUtils.showAlert("Master Data Restore Summary", summary, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            AlertUtils.showAlert("Restore Failed", "An error occurred during the master data restore process.", Alert.AlertType.ERROR);
        }
    }

    @FXML private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }

    @FXML private void logout() {SceneManager.switchScene("main-view.fxml");}
}