package controller;

import dao.BookingDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.sql.SQLException;


public class BookingsController {

    @FXML private TableView<Booking> bookingOrderSummaryTable;
    @FXML private TableColumn<Booking, Integer> requestIdColumn;
    @FXML private TableColumn<Client, String> clientNameColumn;
    @FXML private TableColumn<Event, String> eventDateColumn;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Venue, String> venueNameColumn;
    @FXML private TableColumn<Booking, Double> eventCostColumn;
    @FXML private TableColumn<Booking, Double> eventCommissionColumn;
    @FXML private TableColumn<Booking, Double> bookingTotalColumn;
    @FXML private TableColumn<Booking, String> statusColumn;

    @FXML private TableView<Client> clientOrderSummaryTable;
    @FXML private TableColumn<Client, Integer> clientIdColumn;
    @FXML private TableColumn<Client, Integer> totalJobsColumn;
    @FXML private TableColumn<Client, Integer> totalEventSpendColumn;
    @FXML private TableColumn<Client, Double> clientCommissionColumn;
    @FXML private TableColumn<Client, Double> totalClientSpendColumn;

    @FXML private Button updateBookingButton, cancelBookingButton, dashboardButton;
    @FXML private Button logoutButton;
    @FXML private Button adminButton;
    @FXML private ToggleButton filterConfirmedOnlyToggle;

    @FXML
    private void initialize() {
        setupTables();
    }

    private void setupTables() {
        // BOOKING ORDER SUMMARY TABLE SETUP
        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        venueNameColumn.setCellValueFactory(new PropertyValueFactory<>("venueName"));
        eventCostColumn.setCellValueFactory(new PropertyValueFactory<>("eventCost"));
        eventCommissionColumn.setCellValueFactory(new PropertyValueFactory<>("eventCommission"));
        bookingTotalColumn.setCellValueFactory(new PropertyValueFactory<>("bookingTotal"));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        // CLIENT ORDER SUMMARY TABLE SETUP
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        totalJobsColumn.setCellValueFactory(new PropertyValueFactory<>("totalJobs"));
        totalEventSpendColumn.setCellValueFactory(new PropertyValueFactory<>("totalEventSpend"));
        clientCommissionColumn.setCellValueFactory(new PropertyValueFactory<>("clientCommission"));
        totalClientSpendColumn.setCellValueFactory(new PropertyValueFactory<>("totalClientSpend"));

        bookingOrderSummaryTable.setItems(getBookingOrderSummaryData());
        clientOrderSummaryTable.setItems(getClientOrderSummaryData());
    }

    private ObservableList<Client> getClientOrderSummaryData() {
        ObservableList<Client> data = BookingDAO.getAllCommissionSummaries();
        return data != null ? data : FXCollections.observableArrayList();
    }

    private ObservableList<Booking> getBookingOrderSummaryData() {
        ObservableList<Booking> data = BookingDAO.getBookingOrderSummary();
        return data != null ? data : FXCollections.observableArrayList();
    }

    @FXML
    private void toggleConfirmedOnly() {
        if (filterConfirmedOnlyToggle.isSelected()) {
            ObservableList<Booking> confirmedBookings = bookingOrderSummaryTable.getItems().filtered(b -> b.getStatus() == BookingStatus.CONFIRMED);
            bookingOrderSummaryTable.setItems(confirmedBookings);
        } else {
            refreshBookingData();
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/booking-edit-view.fxml"));
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
            try {
                boolean success = BookingDAO.cancelBooking(selectedBooking.getBookingId());
                if (success) {
                    AlertUtils.showAlert("Success", "Booking canceled successfully.", Alert.AlertType.INFORMATION);
                    bookingOrderSummaryTable.getItems().remove(selectedBooking);
                    refreshBookingData();
                } else {
                    AlertUtils.showAlert("Error", "Failed to cancel the booking. Please try again.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                AlertUtils.showAlert("Database Error", "Error canceling booking: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    // REFRESH BOOKINGS TABLE
    private void refreshBookingData() {
        ObservableList<Booking> updatedBookings = BookingDAO.getBookingOrderSummary();
        bookingOrderSummaryTable.setItems(updatedBookings);
        ObservableList<Client> updatedCommissions = BookingDAO.getAllCommissionSummaries();
        clientOrderSummaryTable.setItems(updatedCommissions);
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


