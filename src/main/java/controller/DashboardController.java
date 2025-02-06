package controller;

import dao.EventDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.SessionManager;

import java.sql.SQLException;
import java.util.List;

public class DashboardController {

    // Event Table
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Integer> eventIdColumn;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> eventArtistColumn;
    @FXML private TableColumn<Event, String> eventClientColumn;
    private ObservableList<Event> eventList;

    // Venue Table
    @FXML private TableView<Venue> venueTable;
    @FXML private TableColumn<Venue, Integer> venueNoColumn;
    @FXML private TableColumn<Venue, String> venueNameColumn;
    @FXML private TableColumn<Venue, String> compatibilityScoreColumn;
    private ObservableList<Venue> venueList = FXCollections.observableArrayList();

    // Bookings Table
    @FXML private TableView<Booking> currentBookingTable;
    @FXML private TableColumn<Booking, String> bookingDateColumn;
    @FXML private TableColumn<Booking, String> bookingTimeColumn;
    @FXML private TableColumn<Booking, String> bookingRequestColumn;
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    @FXML private CheckBox availableCheckbox, sufficientCapacityCheckbox, eventTypeCheckbox, venueCategoryCheckbox;
    @FXML private Button showRequestDetailsButton, filterVenuesButton, autoMatchButton, showVenueDetailsButton, bookVenueButton, logoutButton, settingsButton;

    @FXML
    public void initialize() {
        setupTableColumns();
        eventList = FXCollections.observableArrayList();
        Platform.runLater(this::loadData);
    }

    /**
     * Set up columns to match Event object properties.
     */
    private void setupTableColumns() {
        // EVENT TABLE
        eventIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventId()));
        eventNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventName()));
        eventArtistColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getArtist()));
        eventClientColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getClientName()));

        // VENUE TABLE
        venueNoColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getVenueId()));
        venueNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
        //compatibilityScoreColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventId()));

        // BOOKING TABLE
       // bookingDateColumn.setCellValueFactory(cellData -> cellData.getValue().bookingIdProperty().asObject());
       // bookingTimeColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
       // bookingRequestColumn.setCellValueFactory(cellData -> cellData.getValue().eventDateProperty());
    }

    private void loadData() {
        List<Event> eventResults = EventDAO.getAllEvents();

        if (eventResults == null || eventResults.isEmpty()) {
            System.out.println("❌ No events found in the database!");
        } else {
            eventResults.forEach(event -> System.out.println("✅ Loaded event: " + event.getEventName()));
        }
        if (eventResults != null) {
            eventList.setAll(eventResults);
        } else {
            eventList.clear(); // Ensure table is cleared if no results
        }
        eventTable.setItems(eventList);

//            List<Venue> venueResults = VenueDAO.getAllVenues();
//            venueList.setAll(venueResults);
//            venueTable.setItems(venueList);
//
//            List<Booking> bookingResults = BookingDAO.getAllBookings();
//            bookingList.setAll(bookingResults);
//            currentBookingTable.setItems(bookingList);

    }

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

    // When manager logs in go to manager setting and admin for Admin Settings
    @FXML
    private void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {
            SceneManager.switchScene("admin-view.fxml");
        }
    }

    @FXML
    private void showEventDetails() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("No Event Selected", "Please select an event to view details.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/event-details-view.fxml"));
            Parent root = loader.load();

            // Pass event data to the popup controller
            EventDetailsController controller = loader.getController();
            controller.setEventDetails(selectedEvent);

            // Create popup window
            Stage stage = new Stage();
            stage.setTitle("Event Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load event details.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void logout() {
        SceneManager.switchScene("main-view.fxml");
    }
}
