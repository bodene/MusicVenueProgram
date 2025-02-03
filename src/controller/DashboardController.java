package controller;

import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DashboardController {

    @FXML
    private TableView<?> eventTable;

    @FXML
    private TableView<?> venueTable;

    @FXML
    private TableView<?> currentBookingTable;

    @FXML
    private CheckBox availableCheckbox, sufficientCapacityCheckbox, eventTypeCheckbox, venueCategoryCheckbox;

    @FXML
    private Button showRequestDetailsButton, filterVenuesButton, autoMatchButton, showVenueDetailsButton, bookVenueButton;

    @FXML
    private MenuItem importVenuesCSV, addVenueMenu, removeVenue, importEventsCSV, calculateCommissionMenu, backupMenu;

    // Show Request Details
    @FXML
    private void showRequestDetails() {
        System.out.println("Show Request Details clicked");
        // TODO Implement logic to display request details
    }

    // Filter Venues Based on Selected Checkboxes
    @FXML
    private void filterVenues() {
        boolean available = availableCheckbox.isSelected();
        boolean sufficientCapacity = sufficientCapacityCheckbox.isSelected();
        boolean eventType = eventTypeCheckbox.isSelected();
        boolean venueCategory = venueCategoryCheckbox.isSelected();

        System.out.println("Filtering venues with options:");
        System.out.println("Available: " + available);
        System.out.println("Sufficient Capacity: " + sufficientCapacity);
        System.out.println("Event Type: " + eventType);
        System.out.println("Venue Category: " + venueCategory);

        // TODO implement filtering logic
    }

    //  Automatch Venues with Events
    @FXML
    private void autoMatch() {
        System.out.println("Auto Match Clicked");
        // TODO Implement automatching logic
    }

    // Show Venue Details
    @FXML
    private void showVenueDetails() {
        System.out.println("Show Venue Details Clicked");
        SceneManager.switchScene("view-venue-details.fxml");
    }

    // Book Selected Venue
    @FXML
    private void bookVenue() {
        System.out.println("Book Venue Clicked");
        // TODO Implement Book Venue logic
    }

    // Admin Settings
    @FXML
    private void admin() {
        SceneManager.switchScene("admin-view.fxml");
    }

    @FXML
    private void logout() {
        SceneManager.switchScene("main-view.fxml");
    }
}
