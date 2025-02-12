package controller;

import service.ManagementService;
import service.SceneManager;
import javafx.fxml.FXML;


/**
 * Controller class for the admin dashboard.
 * <p>
 * This class handles various administrative operations such as navigating to venue and booking views,
 * importing CSV data for venues and events, updating user details, navigating to the dashboard, and logging out.
 * It interacts with the {@link ManagementService} for CSV imports and uses the {@link SceneManager} for scene transitions.
 * </p>
 *
 * <p>
 * The class demonstrates encapsulation by utilising a singleton instance of {@code ManagementService} and provides
 * clear separation of concerns by managing only the admin-related actions.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class AdminController {

    /**
     * Singleton instance of the ManagementService used to handle CSV imports and other management operations.
     */
    private final ManagementService managementService = ManagementService.getInstance();

    /**
     * Navigates to the view that displays venue details.
     * <p>
     * This method is invoked via an FXML event when the admin chooses to view the list of venues.
     * It uses the {@link SceneManager} to switch the scene to "view-venue-details.fxml".
     * </p>
     */
    @FXML private void viewVenues() {
        SceneManager.switchScene("view-venue-details.fxml");
    }

    /**
     * Navigates to the bookings view.
     * <p>
     * This method is called when the admin wants to view booking details.
     * It switches the scene to "bookings-view.fxml" using the {@link SceneManager}.
     * </p>
     */
    @FXML private void viewBookings() {
        SceneManager.switchScene("bookings-view.fxml");
    }

    /**
     * Imports venue data from a CSV file.
     * <p>
     * This method delegates the CSV import operation to the {@link ManagementService}.
     * The implementation details of the CSV import are handled within the service.
     * </p>
     */
    @FXML private void importVenuesCSV() {
        managementService.importVenuesCSV();
    }

    /**
     * Imports event data from a CSV file.
     * <p>
     * Similar to {@link #importVenuesCSV()}, this method delegates the task of importing event data
     * from a CSV file to the {@link ManagementService}.
     * </p>
     */
    @FXML private void importEventsCSV() {
        managementService.importEventsCSV();
    }

    /**
     * Navigates to the user profile edit view.
     * <p>
     * This method is used for updating user details. It switches the current scene to "user-profile-edit-view.fxml"
     * where the user can update their information.
     * </p>
     */
    @FXML private void updateUserDetails() {
        SceneManager.switchScene("user-profile-edit-view.fxml");
    }

    /**
     * Navigates to the dashboard view.
     * <p>
     * This method is invoked when the admin chooses to go back to the dashboard.
     * It switches the scene to "dashboard.fxml" using the {@link SceneManager}.
     * </p>
     */
    @FXML private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }

    /**
     * Logs out the user and returns to the main view.
     * <p>
     * This method is triggered when the user chooses to log out of the system.
     * It uses the {@link SceneManager} to switch the scene to "main-view.fxml".
     * </p>
     */
    @FXML private void logout() {
        SceneManager.switchScene("main-view.fxml");
    }
}