package controller;

import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminController {

    @FXML
    private Button viewVenuesButton, viewOrders, calculateCommissionButton,
            importVenuesCSVButton, importEventsCSVButton, backupDataButton, masterBackupButton, backButton1;

    // View Venues Page
    @FXML
    private void viewVenues() {
        SceneManager.switchScene("view-venues.fxml");
    }

    // View Orders Page
    @FXML
    private void viewOrders() {
        SceneManager.switchScene("view-orders.fxml");
    }

    // Calculate Commission
    @FXML
    private void calculateCommission() {
        System.out.println("Calculating Commission Clicked");
        // TODO Implement Calculate Commision
    }

    // Import Venues CSV
    @FXML
    private void importVenuesCSV() {
        System.out.println("import Venues Clicked");
        // TODO Implement import venues
    }

    // Import Events CSV
    @FXML
    private void importEventsCSV() {
        System.out.println("Import Events Clicked");
        // TODO Implement import events
    }

    // Backup Data
    @FXML
    private void backupData() {
        System.out.println("Backup Data Clicked");
        // TODO Implement Backup Data
    }

    // Master Backup
    @FXML
    private void masterBackup() {
        System.out.println("Master Backup Clicked");
        // TODO Implement Master Backup
    }

    @FXML
    private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }

}
