package controller;

import dao.BookingDAO;
import dao.ClientDAO;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;
import service.SceneManager;
import service.SessionManager;
import util.AlertUtils;


/**
 * Controller class for managing bookings and client summaries.
 * <p>
 * This class is responsible for displaying booking and client data in two separate tables,
 * providing filtering functionality, and handling user actions such as updating and cancelling bookings.
 * It interacts with the data access layer (via {@link BookingDAO} and {@link ClientDAO}) to fetch and update data.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class BookingsController {

    /** TableView for displaying booking order summaries. */
    @FXML private TableView<Booking> bookingOrderSummaryTable;
    @FXML private TableColumn<Booking, Integer> requestIdColumn;
    @FXML private TableColumn<Booking, String> eventDateColumn;
    @FXML private TableColumn<Booking, String> eventNameColumn;
    @FXML private TableColumn<Booking, String> venueNameColumn;
    @FXML private TableColumn<Booking, String> bookingCostColumn;
    @FXML private TableColumn<Booking, String> bookingCommissionColumn;
    @FXML private TableColumn<Booking, String> bookingTotalColumn;
    @FXML private TableColumn<Booking, String> statusColumn;

    /** TableView for displaying client order summaries. */
    @FXML private TableView<Client> clientOrderSummaryTable;
    @FXML private TableColumn<Client, Integer> clientIdColumn;
    @FXML private TableColumn<Client, String> clientNameColumn;
    @FXML private TableColumn<Client, Integer> totalJobsColumn;
    @FXML private TableColumn<Client, String> totalEventSpendColumn;
    @FXML private TableColumn<Client, String> clientCommissionColumn;
    @FXML private TableColumn<Client, String> totalClientSpendColumn;

    @FXML private ToggleButton filterConfirmedOnlyToggle;

    /** FilteredList to manage booking filtering based on booking status. */
    private FilteredList<Booking> filteredBookingList;

    /**
     * Initialises the controller after the FXML elements have been loaded.
     * <p>
     * This method sets up the booking and client tables and initialises the filter toggle button.
     * </p>
     */
    @FXML
    private void initialize() {
        setupTables();
        setupToggleButton();
    }

    /**
     * Sets up the booking and client tables by initialising columns and fetching data from the database.
     * <p>
     * The method configures cell value factories for each table column to map data properties,
     * retrieves data via {@link ClientDAO} and {@link BookingDAO}, and initialises the tables with observable lists.
     * </p>
     */
    private void setupTables() {

        // Initialise Booking Table columns.
        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        eventDateColumn.setCellValueFactory(cellData -> cellData.getValue().getEvent().eventDateProperty());
        eventNameColumn.setCellValueFactory(cellData -> cellData.getValue().getEvent().eventNameProperty());
        venueNameColumn.setCellValueFactory(cellData -> cellData.getValue().getVenue().venueNameProperty());
        bookingCostColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingHirePriceProperty());
        bookingCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingEventCommissionProperty());
        bookingTotalColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingTotalProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().getStatusProperty());

        // Fetch client summaries, which include associated bookings.
        List<Client> clientList = ClientDAO.getAllClientSummaries();

        // Extract all bookings from the client list.
        List<Booking> allBookings = clientList.stream()
                .flatMap(client -> client.getBookings().stream())
                .toList();

        // Wrap bookings in an ObservableList and apply filtering.
        ObservableList<Booking> bookingObservableList = FXCollections.observableArrayList(allBookings);
        filteredBookingList = new FilteredList<>(bookingObservableList, p -> true);
        bookingOrderSummaryTable.setItems(filteredBookingList);

        // Setup Client Table columns.
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        totalJobsColumn.setCellValueFactory(cellData -> cellData.getValue().confirmedJobCountProperty().asObject());
        totalEventSpendColumn.setCellValueFactory(cellData -> cellData.getValue().getClientTotalHireProperty());
        clientCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalCommissionProperty());
        totalClientSpendColumn.setCellValueFactory(cellData -> cellData.getValue().getClientBookingTotalProperty());

        // Convert client list into an ObservableList and set it in the client table.
        ObservableList<Client> observableClientList = FXCollections.observableArrayList(clientList);
        clientOrderSummaryTable.setItems(observableClientList);
    }

    /**
     * Sets up the toggle button used to filter bookings based on their status.
     * <p>
     * When the toggle is selected, the table will display only bookings with a status of CONFIRMED.
     * When unselected, all bookings are displayed.
     * </p>
     */
    private void setupToggleButton() {
        filterConfirmedOnlyToggle.setOnAction(event -> {
            if (filterConfirmedOnlyToggle.isSelected()) {
                // When selected, update button text and apply filter for confirmed bookings.
                filterConfirmedOnlyToggle.setText("Show All Bookings");
                filteredBookingList.setPredicate(booking -> booking.getStatus() == BookingStatus.CONFIRMED);
            } else {
                // When unselected, revert button text and remove the filter.
                filterConfirmedOnlyToggle.setText("Confirmed Bookings Only");
                filteredBookingList.setPredicate(booking -> true);
            }
        });
    }

    /**
     * Updates the selected booking.
     * <p>
     * This method retrieves the booking selected by the user from the table and opens a new window
     * with the booking details loaded in the edit view. After editing, the booking data is refreshed.
     * If no booking is selected, a warning alert is shown.
     * </p>
     */
    @FXML
    private void updateBooking() {

        // Retrieve the selected booking from the table.
        Booking selectedBooking = bookingOrderSummaryTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            AlertUtils.showAlert("No Booking Selected", "Please select a booking to update.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Load the booking update view.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/update-booking-details.fxml"));
            Parent root = loader.load();

            // Pass the selected booking to the update controller.
            UpdateBookingDetailsController controller = loader.getController();
            controller.setBooking(selectedBooking);

            // Create a new stage for the update window.
            Stage stage = new Stage();
            stage.setTitle("Edit Booking");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh booking data after the update operation.
            refreshBookingData();
        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Failed to load booking edit view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Cancels the selected booking.
     * <p>
     * This method retrieves the selected booking and, after user confirmation,
     * initiates an asynchronous task to cancel the booking via {@link BookingDAO}.
     * Upon success or failure, the UI is updated accordingly.
     * </p>
     */
    @FXML
    private void cancelBooking() {

        // Retrieve the selected booking from the table.
        Booking selectedBooking = bookingOrderSummaryTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            AlertUtils.showAlert("No Booking Selected", "Please select a booking to cancel.", Alert.AlertType.WARNING);
            return;
        }

        // Confirm the cancellation action with the user.
        boolean confirmed = AlertUtils.showConfirmation("Confirm Cancellation",
                "Are you sure you want to cancel this booking? This action cannot be undone.");

        if (confirmed) {
            // Create an asynchronous task to cancel the booking.
            Task<Boolean> cancelTask = new Task<>() {
                @Override
                protected Boolean call() throws SQLException {
                    return BookingDAO.cancelBooking(selectedBooking.getBookingId());
                }
            };

            // Handle task success.
            cancelTask.setOnSucceeded(event -> {
                boolean success = cancelTask.getValue();
                if (success) {
                    AlertUtils.showAlert("Success", "Booking canceled successfully.", Alert.AlertType.INFORMATION);
                    refreshBookingData();
                } else {
                    AlertUtils.showAlert("Error", "Failed to cancel the booking. Please try again.", Alert.AlertType.ERROR);
                }
            });

            // Handle task failure.
            cancelTask.setOnFailed(event -> {
                AlertUtils.showAlert("Database Error",
                        "An unexpected error occurred while canceling the booking. Please try again later.",
                        Alert.AlertType.ERROR);
                cancelTask.getException().printStackTrace();
            });

            // Start the task in a new thread.
            new Thread(cancelTask).start();
        }
    }

    /**
     * Refreshes the booking and client data in the tables.
     * <p>
     * This method fetches updated client summaries and extracts the latest booking data,
     * reapplying any active filters before updating the table views.
     * </p>
     */
    private void refreshBookingData() {

        // Retrieve updated client summaries.
        List<Client> updatedClients = ClientDAO.getAllClientSummaries();

        // Extract updated bookings from the clients.
        List<Booking> updatedBookings = updatedClients.stream()
                .flatMap(client -> client.getBookings().stream())
                .toList();

        // Create an observable list for the updated bookings.
        ObservableList<Booking> updatedBookingList = FXCollections.observableArrayList(updatedBookings);

        // Create a new FilteredList with the updated bookings.
        FilteredList<Booking> newFilteredList = new FilteredList<>(updatedBookingList, p -> true);

        // Reapply the confirmed bookings filter if the toggle is selected.
        if (filterConfirmedOnlyToggle.isSelected()) {
            newFilteredList.setPredicate(booking -> booking.getStatus() == BookingStatus.CONFIRMED);
        }

        // Update the filtered booking list and table view.
        filteredBookingList = newFilteredList;
        bookingOrderSummaryTable.setItems(filteredBookingList);

        // Update the client table with the latest client data.
        ObservableList<Client> updatedClientList = FXCollections.observableArrayList(updatedClients);
        clientOrderSummaryTable.setItems(updatedClientList);
    }

    /**
     * Navigates to the dashboard view.
     * <p>
     * This method switches the scene to "dashboard.fxml" using the {@link SceneManager}.
     * </p>
     */
    @FXML private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }

    /**
     * Navigates to the settings view.
     * <p>
     * This method checks the session to determine if the user is a manager or staff,
     * and navigates to the appropriate settings view.
     * </p>
     */
    @FXML private void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {
            SceneManager.switchScene("admin-view.fxml");
        }
    }

    /**
     * Logs out the current user.
     * <p>
     * This method switches the scene to "main-view.fxml" effectively logging out the user.
     * </p>
     */
    @FXML private void logout() {
        SceneManager.switchScene("main-view.fxml");
    }
}