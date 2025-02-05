package controller;

import service.ManagementService;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ManagerController {
    private final ManagementService managementService = ManagementService.getInstance();

    @FXML
    private Button viewVenuesButton, viewOrdersButton, calculateCommissionButton, staffManagementButton, viewAllOrdersButon,
            importVenuesCSVButton, importEventsCSVButton, backupDataButton, masterBackupButton, transactionBackupButton, importTransactionButton, importMasterBackupButton, backButton1;

    // View Venues Page
    @FXML
    private void viewVenues() {
        SceneManager.switchScene("view-venue-details.fxml");
    }

    // View Orders Page
    @FXML
    private void viewOrders() {
        System.out.println("Staff View Orders Clicked");
        // TODO Implement Staff Order view

    }

    // Calculate Commission
    @FXML
    private void calculateCommission() {
        System.out.println("Calculating Commission Clicked");
        // TODO Implement Calculate Commision
    }

    // User Management
    @FXML
    private void staffManagement() {
        SceneManager.switchScene("staff-management-view.fxml");
    }

    // View All Orders
    @FXML
    private void viewAllOrders() {
        SceneManager.switchScene("order-view.fxml");
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
    private void transactionBackup() {
        System.out.println("Backing up Transactions...");
        // TODO Implement the actual logic here
    }

    @FXML
    private void importTransaction() {
        System.out.println("Importing Transaction Data...");
        // TODO Implement the actual logic here
    }

    @FXML
    private void importMasterBackup() {
        System.out.println("Importing Master Backup...");
        // TODO Implement the actual logic here
    }

    @FXML
    private void goToDashboard() {

        SceneManager.switchScene("dashboard.fxml");
    }

}
