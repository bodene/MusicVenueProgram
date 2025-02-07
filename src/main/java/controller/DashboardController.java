package controller;

import dao.BookingDAO;
import dao.EventDAO;
import dao.VenueDAO;
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
import util.AlertUtils;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    @FXML private TableColumn<Venue, Integer> compatibilityScoreColumn;
    private ObservableList<Venue> venueList = FXCollections.observableArrayList();

    // Bookings Table
    @FXML private TableView<Booking> currentBookingTable;
    @FXML private TableColumn<Booking, Integer> bookingIdColumn;
    @FXML private TableColumn<Booking, String> eventDateColumn;
    @FXML private TableColumn<Booking, String> eventTimeColumn;
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    @FXML private CheckBox availableCheckbox, sufficientCapacityCheckbox, eventTypeCheckbox, venueCategoryCheckbox;
    @FXML private Button showRequestDetailsButton, filterVenuesButton, autoMatchButton, showVenueDetailsButton, bookVenueButton, logoutButton, settingsButton;
    private Event selectedEvent;
    private Venue selectedVenue;

    @FXML
    public void initialize() {
        setupEventTableColumns();
        eventList = FXCollections.observableArrayList();
        setupVenueTableColumns();
        //setupBookingTableColumns();
        Platform.runLater(this::loadEventData);
    }

    // Set up columns to match Event object properties and allow user selection of event
    private void setupEventTableColumns() {
        // EVENT TABLE
        eventIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventId()));
        eventNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventName()));
        eventArtistColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getArtist()));
        eventClientColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getClientName()));

        // Listen for event selection to load matching venues
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEvent = newSelection;
                loadVenuesForEvent(selectedEvent);
            }
        });
    }

    // Helper Method - Get Selected Event
    private Event getSelectedEvent() {
        return eventTable.getSelectionModel().getSelectedItem();
    }

    // Set up Venue Table based on the event selected
    private void setupVenueTableColumns() {
        venueNoColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getVenueId()));
        venueNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        compatibilityScoreColumn.setCellValueFactory(cellData -> {
            try {
                Event selectedEvent = getSelectedEvent();
                if (selectedEvent == null) return new SimpleObjectProperty<>(0);

                return new SimpleObjectProperty<>(calculateCompatibility(cellData.getValue(), selectedEvent));
            } catch (SQLException e) {
                System.err.println("Error calculating venue compatibility: " + e.getMessage());
                return new SimpleObjectProperty<>(0);
            }
        });
        // Listen for venue selection changes
        venueTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedVenue = newSelection;
            }
        });

        // Sort venues by compatibility
        sortVenuesByCompatibility();
    }

    // Sort by compatibility
    @FXML
    private void sortVenuesByCompatibility() {
        venueTable.getSortOrder().clear();
        venueTable.getSortOrder().add(compatibilityScoreColumn);
        compatibilityScoreColumn.setSortType(TableColumn.SortType.DESCENDING);
    }

    // Set up booking Table
//    private void setupBookingTableColumns() {
//        bookingIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBookingId()));
//        eventDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEventDate().toString()));
//        eventTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEventTime().toString()));
//
//        currentBookingTable.setItems(bookingList);
//    }

    // Load event Data from database
    private void loadEventData() {
        List<Event> events = EventDAO.getAllEvents();

        if (events != null) {
            eventList.setAll(events);
        } else {
            eventList.clear();
        }
        eventTable.setItems(eventList);
    }

    // Load Venues for selected Event and apply filters
    private void loadVenuesForEvent(Event event) {
        try {
            List<Venue> allVenues = VenueDAO.getAllVenues();

            List<Venue> filteredVenues = allVenues.stream()
                .map(venue -> {
                    try {
                        venue.setCompatibilityScore(calculateCompatibility(venue, event));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return venue;
                })
        .sorted((v1, v2) -> Double.compare(v2.getCompatibilityScore(), v1.getCompatibilityScore()))
                        .collect(Collectors.toList());

                venueList.setAll(filteredVenues);
                venueTable.setItems(venueList);

//            if (!filteredVenues.isEmpty()) {
//                Venue selectedVenue = venueTable.getSelectionModel().getSelectedItem();
//                if (selectedVenue != null) {
//                    //loadBookingsForVenue(selectedVenue.getVenueId());
//                }
//            }

                if (filteredVenues.isEmpty()) {
                    AlertUtils.showAlert("No Matches", "Unable to find a match, please add more venue data or try loosening up your criteria.", Alert.AlertType.WARNING);
                }

            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper Method - Load Bookings for Venue
//    private void loadBookingsForVenue(int venueId) {
//        try {
//            List<Booking> bookings = BookingDAO.getBookingsByVenueId(venueId);
//            bookingList.setAll(bookings);
//            bookingTable.setItems(bookingList);
//        } catch (SQLException e) {
//            System.err.println("Error loading bookings: " + e.getMessage());
//        }
//    }

    // Calculate Venue Compatibility
    private int calculateCompatibility(Venue venue, Event event) throws SQLException {
        int score = 0;

        // 1. Check Availability
        boolean isAvailable = BookingDAO.checkAvailability(venue.getVenueId(), event.getEventDate(), event.getEventTime(), event.getDuration());
        if (isAvailable) score += 25;

        // 2. Check Capacity
        if (venue.getCapacity() >= event.getRequiredCapacity()) score += 25;

        // 3. Check Event Category Matching
        boolean eventCategoryMatch = switch (event.getCategory()) {
            case INDOOR -> venue.getCategory() == VenueCategory.INDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
            case OUTDOOR -> venue.getCategory() == VenueCategory.OUTDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
            case CONVERTIBLE -> venue.getCategory() == VenueCategory.CONVERTIBLE;
            default -> false;
        };

        if (eventCategoryMatch) {
            score += 25;}

        // 4. Check Venue Types Match
                if (venue.getVenueTypes().stream()
                .map(type -> type.toString().trim().toLowerCase())
                .collect(Collectors.toSet()) // Ensure uniqueness
                .contains(event.getEventType().trim().toLowerCase())) {
            score += 25;
        }

        return score; // 0 to 100%
    }

    // Filter Venues Based on Selected Checkboxes
    @FXML
    private void filterVenues() {
        if (selectedEvent == null) {
            AlertUtils.showAlert("No Event Selected", "Please select an event before filtering venues.", Alert.AlertType.WARNING);
            return;
        }
        List<Venue> filteredList = venueList.stream()
                .filter(venue -> {
                    boolean match = true;

                    // Filter by Availability
                    if (availableCheckbox.isSelected()) {
                        try {
                            match &= BookingDAO.checkAvailability(venue.getVenueId(), selectedEvent.getEventDate(), selectedEvent.getEventTime(), selectedEvent.getDuration());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // Filter by Capacity
                    if (sufficientCapacityCheckbox.isSelected()) {
                        match &= venue.getCapacity() >= selectedEvent.getRequiredCapacity();
                    }

                    // Filter by **Event Category** (Indoor/Outdoor/Convertible)
                    if (venueCategoryCheckbox.isSelected()) {
                        boolean categoryMatch = switch (selectedEvent.getCategory()) {
                            case INDOOR -> venue.getCategory() == VenueCategory.INDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
                            case OUTDOOR -> venue.getCategory() == VenueCategory.OUTDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
                            case CONVERTIBLE -> venue.getCategory() == VenueCategory.CONVERTIBLE;
                            default -> false;
                        };

                        match &= categoryMatch;
                    }

                    // Filter by **Venue Type** (gig, concert, etc.)
                    if (eventTypeCheckbox.isSelected()) {
                        boolean typeMatch = venue.getVenueTypes().stream()
                                .map(type -> type.toString().trim().toLowerCase())
                                .collect(Collectors.toSet())
                                .contains(selectedEvent.getEventType().toLowerCase().trim());

                        match &= typeMatch;
                    }

                    return match;
                })
                .sorted((v1, v2) -> Double.compare(v2.getCompatibilityScore(), v1.getCompatibilityScore()))
                .collect(Collectors.toList());

        venueTable.setItems(FXCollections.observableArrayList(filteredList));
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
        Venue selectedVenue = venueTable.getSelectionModel().getSelectedItem();
        if (selectedVenue == null) {
            AlertUtils.showAlert("No Venue Selected", "Please select a venue to view details.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/comparison-venue-event-view.fxml"));
            Parent root = loader.load();

            ComparisonVenueEventController controller = loader.getController();
            controller.setVenueAndEvent(selectedVenue, selectedEvent, BookingDAO.checkAvailability(
                    selectedVenue.getVenueId(), selectedEvent.getEventDate(), selectedEvent.getEventTime(), selectedEvent.getDuration()
            ));

            Stage stage = new Stage();
            stage.setTitle("Venue & Event Comparison");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Failed to load venue details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    // Book Selected Venue
    @FXML
    private void bookVenue() {
        if (selectedEvent == null || selectedVenue == null) {
            AlertUtils.showAlert("Booking Error", "Please select both an event and a venue before booking.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Capture today's date
            LocalDate bookingDate = LocalDate.now();

            // Calculate hire price
            double hirePrice = selectedVenue.getHirePricePerHour() * selectedEvent.getDuration();

            // Check if the venue is available
            boolean isAvailable = BookingDAO.checkAvailability(
                    selectedVenue.getVenueId(),
                    selectedEvent.getEventDate(),
                    selectedEvent.getEventTime(),
                    selectedEvent.getDuration()
            );

            if (!isAvailable) {
                AlertUtils.showAlert("Booking Error", "The selected venue is not available for the chosen time slot.", Alert.AlertType.WARNING);
                return;
            }

            // Set booking status
            String bookingStatus = "CONFIRMED";

            // Get the logged-in user
            String bookedBy = SessionManager.getCurrentUser().getUsername();

            // Insert the booking into the database
            boolean success = BookingDAO.bookVenue(
                    bookingDate,
                    hirePrice,
                    bookingStatus,
                    selectedEvent.getEventId(),
                    selectedVenue.getVenueId(),
                    selectedEvent.getClientId(),
                    bookedBy
            );

            // 7️Notify the user
            if (success) {
                AlertUtils.showAlert("Success", "Venue successfully booked!", Alert.AlertType.INFORMATION);
                loadVenuesForEvent(selectedEvent); // Refresh venue list
            } else {
                AlertUtils.showAlert("Booking Error", "Failed to book the venue.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            AlertUtils.showAlert("Database Error", "Error booking venue: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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
            AlertUtils.showAlert("No Event Selected", "Please select an event to view details.", Alert.AlertType.ERROR);
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
            AlertUtils.showAlert("Error", "Failed to load event details.", Alert.AlertType.ERROR);
        }
    }

    @FXML private void logout() {SceneManager.switchScene("main-view.fxml");}
}
