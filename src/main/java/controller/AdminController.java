package controller;

import service.ManagementService;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminController {
    private final ManagementService managementService = ManagementService.getInstance();

    @FXML
    private Button viewVenuesButton, viewEvents, viewOrders, calculateCommissionButton,
            importVenuesCSVButton, importEventsCSVButton, updateUserDetailsButton, backButton1;

    @FXML private void viewVenues() {SceneManager.switchScene("view-venue-details.fxml");}
    @FXML private void viewOrders() {
        SceneManager.switchScene("order-view.fxml");
    }

    // Calculate Commission
    @FXML
    private void calculateCommission() {
        System.out.println("Calculating Commission Clicked");
        // TODO Implement Calculate Commision
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
}
