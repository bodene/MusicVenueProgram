package controller;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import model.Event;
import model.Venue;
import service.CSVHandler;
import service.ManagementService;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

public class AdminController {
    private final ManagementService managementService = ManagementService.getInstance();

    @FXML
    private Button viewVenuesButton, viewEvents, viewOrders, calculateCommissionButton,
            importVenuesCSVButton, importEventsCSVButton, updateUserDetailsButton, backButton1;

    // View Venues Page
    @FXML
    private void viewVenues() {
        SceneManager.switchScene("view-venue-details.fxml");
    }

    // View Events Page
    @FXML
    private void viewEvents() {
       System.out.println("View Events Clicked");
        // TODO View Events Page
    }

    // View Orders Page
    @FXML
    private void viewOrders() {
        SceneManager.switchScene("order-view.fxml");
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
        managementService.importVenuesCSV();
    }

    // Import Events CSV
    @FXML
    private void importEventsCSV() {
        managementService.importEventsCSV();
    }

    // Update User Details
    @FXML
    private void updateUserDetails() {
        SceneManager.switchScene("user-profile-edit-view.fxml");
    }

    @FXML
    private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }

}
