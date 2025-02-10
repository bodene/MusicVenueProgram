package controller;

import dao.BookingDAO;
import dao.ClientDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import util.NumberUtils;
import javafx.collections.transformation.FilteredList;

import java.sql.SQLException;
import java.util.List;


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
        // Initialize Booking Table
        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        eventDateColumn.setCellValueFactory(cellData -> cellData.getValue().getEvent().eventDateProperty());
        eventNameColumn.setCellValueFactory(cellData -> cellData.getValue().getEvent().eventNameProperty());
        venueNameColumn.setCellValueFactory(cellData -> cellData.getValue().getVenue().venueNameProperty());
        bookingCostColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingHirePriceProperty());
        bookingCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingEventCommissionProperty());
        bookingTotalColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingTotalProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().getStatusProperty());

        // Load Booking data once
        originalBookingList = BookingDAO.getBookingOrderSummary();
        filteredBookingList = new FilteredList<>(originalBookingList, p -> true);
        bookingOrderSummaryTable.setItems(filteredBookingList);

        // Initialize Client Table
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        totalJobsColumn.setCellValueFactory(cellData -> cellData.getValue().confirmedJobCountProperty().asObject());
        totalEventSpendColumn.setCellValueFactory(cellData -> cellData.getValue().getClientTotalHireProperty());
        clientCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalCommissionProperty());
        totalClientSpendColumn.setCellValueFactory(cellData -> cellData.getValue().getClientBookingTotalProperty());

        // Load Client data
        List<Client> clientList = ClientDAO.getAllClientSummaries();
        ObservableList<Client> observableClientList = FXCollections.observableArrayList(clientList);
        clientOrderSummaryTable.setItems(observableClientList);
    }

    private ObservableList<Client> getClientOrderSummaryData() {
        ObservableList<Client> data = BookingDAO.getAllCommissionSummaries();
        return data != null ? data : FXCollections.observableArrayList();
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

            refreshBookingData();  // Refresh table after updating
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
        // Refresh Booking Data
        List<Booking> updatedBookings = BookingDAO.getBookingOrderSummary();
        originalBookingList.setAll(updatedBookings);
        filteredBookingList.setPredicate(filterConfirmedOnlyToggle.isSelected()
                ? booking -> booking.getStatus() == BookingStatus.CONFIRMED
                : booking -> true);
        bookingOrderSummaryTable.setItems(filteredBookingList);

        // Refresh Client Summary Data
        List<Client> updatedClients = ClientDAO.getAllClientSummaries(); // Ensure this method recalculates totals and commissions
        clientOrderSummaryTable.setItems(FXCollections.observableArrayList(updatedClients));
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


