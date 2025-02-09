package controller;

import service.ManagementService;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminController {
    private final ManagementService managementService = ManagementService.getInstance();

    @FXML private Button viewVenuesButton, viewBookingsButton,
            importVenuesCSVButton, importEventsCSVButton, updateUserDetailsButton, dashboardButton, logoutButton;

    @FXML private void viewVenues() {SceneManager.switchScene("view-venue-details.fxml");}
    @FXML private void viewBookings() {
        SceneManager.switchScene("bookings-view.fxml");
    }
    @FXML private void importVenuesCSV() {
        managementService.importVenuesCSV();
    }
    @FXML private void importEventsCSV() {
        managementService.importEventsCSV();
    }
    @FXML private void updateUserDetails() {
        SceneManager.switchScene("user-profile-edit-view.fxml");
    }
    @FXML private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }
    @FXML private void logout() {SceneManager.switchScene("main-view.fxml");}
}
