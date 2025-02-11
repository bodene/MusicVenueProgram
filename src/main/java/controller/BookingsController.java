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

public class BookingsController {

    @FXML private TableView<Booking> bookingOrderSummaryTable;
    @FXML private TableColumn<Booking, Integer> requestIdColumn;
    @FXML private TableColumn<Booking, String> eventDateColumn;
    @FXML private TableColumn<Booking, String> eventNameColumn;
    @FXML private TableColumn<Booking, String> venueNameColumn;
    @FXML private TableColumn<Booking, String> bookingCostColumn;
    @FXML private TableColumn<Booking, String> bookingCommissionColumn;
    @FXML private TableColumn<Booking, String> bookingTotalColumn;
    @FXML private TableColumn<Booking, String> statusColumn;

    @FXML private TableView<Client> clientOrderSummaryTable;
    @FXML private TableColumn<Client, Integer> clientIdColumn;
    @FXML private TableColumn<Client, String> clientNameColumn;
    @FXML private TableColumn<Client, Integer> totalJobsColumn;
    @FXML private TableColumn<Client, String> totalEventSpendColumn;
    @FXML private TableColumn<Client, String> clientCommissionColumn;
    @FXML private TableColumn<Client, String> totalClientSpendColumn;

    @FXML private Button updateBookingButton, cancelBookingButton, dashboardButton;
    @FXML private Button logoutButton;
    @FXML private Button adminButton;
    @FXML private ToggleButton filterConfirmedOnlyToggle;
    private ObservableList<Booking> originalBookingList;
    private FilteredList<Booking> filteredBookingList;

    @FXML
    private void initialize() {
        setupTables();
        setupToggleButton();
    }

    private void setupTables() {
        // Initialise the booking list as an empty observable list
        originalBookingList = FXCollections.observableArrayList();

        // Initialise Booking Table
        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        eventDateColumn.setCellValueFactory(cellData -> cellData.getValue().getEvent().eventDateProperty());
        eventNameColumn.setCellValueFactory(cellData -> cellData.getValue().getEvent().eventNameProperty());
        venueNameColumn.setCellValueFactory(cellData -> cellData.getValue().getVenue().venueNameProperty());

        bookingCostColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingHirePriceProperty());
        bookingCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingEventCommissionProperty());
        bookingTotalColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingTotalProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().getStatusProperty());

        // Fetch data from ClientDAO.getAllClientSummaries()
        List<Client> clientList = ClientDAO.getAllClientSummaries();

        // Extract all bookings from the fetched clients
        List<Booking> allBookings = clientList.stream()
                .flatMap(client -> client.getBookings().stream())  // Flatten the list of bookings from all clients
                .toList();

        // Use the filtered bookings for the Booking table
        ObservableList<Booking> bookingObservableList = FXCollections.observableArrayList(allBookings);
        filteredBookingList = new FilteredList<>(bookingObservableList, p -> true);
        bookingOrderSummaryTable.setItems(filteredBookingList);

        // Setup Client table with the fetched client list
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        totalJobsColumn.setCellValueFactory(cellData -> cellData.getValue().confirmedJobCountProperty().asObject());
        totalEventSpendColumn.setCellValueFactory(cellData -> cellData.getValue().getClientTotalHireProperty());
        clientCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalCommissionProperty());
        totalClientSpendColumn.setCellValueFactory(cellData -> cellData.getValue().getClientBookingTotalProperty());

        // Convert client list to an ObservableList and set it in the client table
        ObservableList<Client> observableClientList = FXCollections.observableArrayList(clientList);
        clientOrderSummaryTable.setItems(observableClientList);
    }

    private void setupToggleButton() {
        filterConfirmedOnlyToggle.setOnAction(event -> {
            if (filterConfirmedOnlyToggle.isSelected()) {
                filterConfirmedOnlyToggle.setText("Show All Bookings");
                filteredBookingList.setPredicate(booking -> booking.getStatus() == BookingStatus.CONFIRMED);
            } else {
                filterConfirmedOnlyToggle.setText("Confirmed Bookings Only");
                filteredBookingList.setPredicate(booking -> true);
            }
        });
    }

    // UPDATE A BOOKING FROM USER SELECTION
    @FXML
    private void updateBooking() {
        Booking selectedBooking = bookingOrderSummaryTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            AlertUtils.showAlert("No Booking Selected", "Please select a booking to update.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/update-booking-details.fxml"));
            Parent root = loader.load();

            UpdateBookingDetailsController controller = loader.getController();
            controller.setBooking(selectedBooking);

            Stage stage = new Stage();
            stage.setTitle("Edit Booking");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshBookingData();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Failed to load booking edit view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // CANCEL A BOOKING FROM USER SELECTION
    @FXML
    private void cancelBooking() {
        Booking selectedBooking = bookingOrderSummaryTable.getSelectionModel().getSelectedItem();

        if (selectedBooking == null) {
            AlertUtils.showAlert("No Booking Selected", "Please select a booking to cancel.", Alert.AlertType.WARNING);
            return;
        }

        boolean confirmed = AlertUtils.showConfirmation("Confirm Cancellation", "Are you sure you want to cancel this booking? This action cannot be undone.");
        if (confirmed) {
            Task<Boolean> cancelTask = new Task<>() {
                @Override
                protected Boolean call() throws SQLException {
                    return BookingDAO.cancelBooking(selectedBooking.getBookingId());
                }
            };

            cancelTask.setOnSucceeded(event -> {
                boolean success = cancelTask.getValue();
                if (success) {
                    AlertUtils.showAlert("Success", "Booking canceled successfully.", Alert.AlertType.INFORMATION);
                    refreshBookingData();
                } else {
                    AlertUtils.showAlert("Error", "Failed to cancel the booking. Please try again.", Alert.AlertType.ERROR);
                }
            });

            cancelTask.setOnFailed(event -> {
                AlertUtils.showAlert("Database Error", "An unexpected error occurred while canceling the booking. Please try again later.", Alert.AlertType.ERROR);
                cancelTask.getException().printStackTrace();
            });

            new Thread(cancelTask).start();
        }
    }

    private void refreshBookingData() {
        // Retrieve all clients and their bookings
        List<Client> updatedClients = ClientDAO.getAllClientSummaries();

        // Extract all bookings
        List<Booking> updatedBookings = updatedClients.stream()
                .flatMap(client -> client.getBookings().stream())
                .toList();

        // Create a new ObservableList for bookings
        ObservableList<Booking> updatedBookingList = FXCollections.observableArrayList(updatedBookings);

        // Create a new FilteredList with the updated booking list
        FilteredList<Booking> newFilteredList = new FilteredList<>(updatedBookingList, p -> true);

        // Apply the filter if the toggle is selected
        if (filterConfirmedOnlyToggle.isSelected()) {
            newFilteredList.setPredicate(booking -> booking.getStatus() == BookingStatus.CONFIRMED);
        }

        // Set the new filtered list to the table
        filteredBookingList = newFilteredList;
        bookingOrderSummaryTable.setItems(filteredBookingList);

        // Update the client table similarly to ensure consistency
        ObservableList<Client> updatedClientList = FXCollections.observableArrayList(updatedClients);
        clientOrderSummaryTable.setItems(updatedClientList);
    }

    @FXML private void goToDashboard() {SceneManager.switchScene("dashboard.fxml");}

    @FXML private void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {
            SceneManager.switchScene("admin-view.fxml");
        }
    }

    @FXML private void logout() {SceneManager.switchScene("main-view.fxml");}
}