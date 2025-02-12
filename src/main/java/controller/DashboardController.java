package controller;

import dao.BookingDAO;
import dao.ClientDAO;
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

    // EVENT TABLE
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Integer> eventIdColumn;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> eventArtistColumn;
    @FXML private TableColumn<Event, String> eventClientColumn;
    @FXML private TableColumn<Event, String> eventDateColumn;
    @FXML private TableColumn<Event, String> eventTimeColumn;
    private ObservableList<Event> eventList;

    // VENUE TABLE
    @FXML private TableView<Venue> venueTable;
    @FXML private TableColumn<Venue, Integer> venueNoColumn;
    @FXML private TableColumn<Venue, String> venueNameColumn;
    @FXML private TableColumn<Venue, Integer> compatibilityScoreColumn;
    private ObservableList<Venue> venueList = FXCollections.observableArrayList();

    // BOOKINGS TABLE
    @FXML private TableView<Booking> currentBookingTable;
    @FXML private TableColumn<Booking, Integer> bookingIdColumn;
    @FXML private TableColumn<Booking, String> bookedEventDateColumn;
    @FXML private TableColumn<Booking, String> bookedEventTimeColumn;
    @FXML private TableColumn<Booking, String> bookedEventNameColumn;
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    @FXML private CheckBox availableCheckbox;
    @FXML private CheckBox sufficientCapacityCheckbox;
    @FXML private CheckBox eventTypeCheckbox;
    @FXML private CheckBox venueCategoryCheckbox;

    private Event selectedEvent;
    private Venue selectedVenue;

    @FXML
    public void initialize() {
        setupEventTableColumns();
        eventList = FXCollections.observableArrayList();
        setupVenueTableColumns();
        setUpBookingTableColumns();
        currentBookingTable.setPlaceholder(new Label("Please select a venue to view bookings."));
        Platform.runLater(this::loadEventData);
    }

    // SET TO COLUMNS TO MATCH EVENT OBJECT PROPERTIES AND ALLOW USER SELECTION
    private void setupEventTableColumns() {

        // EVENT TABLE
        eventIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventId()));
        eventNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventName()));
        eventArtistColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getArtist()));
        eventClientColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getClientName()));
        eventDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventDate().toString()));
        eventTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventTime().toString()));

        // LISTEN TO EVENT SELECTION & LOAD VENUES
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEvent = newSelection;
                loadVenuesForEvent(selectedEvent);
            }
        });
    }

    // HELPER METHOD - GET SELECTED EVENT
    private Event getSelectedEvent() {
        return eventTable.getSelectionModel().getSelectedItem();
    }

    // SET UP VENUE TABLE BASED ON EVENT SELECTED
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

        // LISTEN FOR VENUE SELECTION
        venueTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedVenue = newSelection;
                loadConfirmedBookingsForVenue(selectedVenue);  // Load confirmed bookings for the selected venue
            }
        });

        // SORT VENUES BY COMPATIBILITY
        sortVenuesByCompatibility();
    }

    // SORT BY COMPATIBILITY
    @FXML
    private void sortVenuesByCompatibility() {
        venueTable.getSortOrder().clear();
        venueTable.getSortOrder().add(compatibilityScoreColumn);
        compatibilityScoreColumn.setSortType(TableColumn.SortType.DESCENDING);
    }

    // LOAD EVENT DATA FROM DB
    private void loadEventData() {
        // Step 1: Retrieve all events from EventDAO
        List<Event> allEvents = EventDAO.getAllEvents();

        // Step 2: Retrieve all bookings to identify already booked events
        List<Client> allClients = ClientDAO.getAllClientSummaries();
        List<Integer> bookedEventIds = allClients.stream()
                .flatMap(client -> client.getBookings().stream())
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .map(booking -> booking.getEvent().getEventId())
                .collect(Collectors.toList());

        // Step 3: Filter events to exclude those that are already booked
        List<Event> availableEvents = allEvents.stream()
                .filter(event -> !bookedEventIds.contains(event.getEventId()))
                .collect(Collectors.toList());

        // Step 4: Set the available events in the event table
        if (!availableEvents.isEmpty()) {
            eventList.setAll(availableEvents);
        } else {
            eventList.clear();
            eventTable.setPlaceholder(new Label("No available events."));
        }

        eventTable.setItems(eventList);
    }


    // LOAD VENUES FOR SELECTED EVENT AND APPLY FILTERS
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

                if (filteredVenues.isEmpty()) {
                    AlertUtils.showAlert("No Matches", "Unable to find a match, please add more venue data or try loosening up your criteria.", Alert.AlertType.WARNING);
                }

            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CALCULATE VENUE COMPATIBILITY
    private int calculateCompatibility(Venue venue, Event event) throws SQLException {
        int score = 0;

        // 1. CHECK AVAILABILITY
        boolean isAvailable = BookingDAO.checkAvailability(venue.getVenueId(), event.getEventDate(), event.getEventTime(), event.getDuration());
        if (isAvailable) score += 25;

        // 2. CHECK CAPACITY
        if (venue.getCapacity() >= event.getRequiredCapacity()) score += 25;

        // 3. CHECK EVENT CATEGORY MATCHING
        boolean eventCategoryMatch = switch (event.getCategory()) {
            case INDOOR -> venue.getCategory() == VenueCategory.INDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
            case OUTDOOR -> venue.getCategory() == VenueCategory.OUTDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
            case CONVERTIBLE -> venue.getCategory() == VenueCategory.CONVERTIBLE;
        };

        if (eventCategoryMatch) {
            score += 25;}

        // 4. CHECK VENUE TYPES MATCH
                if (venue.getVenueTypes().stream()
                .map(type -> type.toString().trim().toLowerCase())
                .collect(Collectors.toSet()) // Ensure uniqueness
                .contains(event.getEventType().trim().toLowerCase())) {
            score += 25;
        }

        return score; // 0 TO 100%
    }

    // SET UP TABLE FOR VENUES BOOKINGS
    private void setUpBookingTableColumns() {
        bookingIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBookingId()));
        bookedEventDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvent().getEventDate().toString()));
        bookedEventTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvent().getEventTime().toString()));
        bookedEventNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvent().getEventName()));
    }

    // LOAD CONFIRMED BOOKINGS FOR SELECTED VENUE
    private void loadConfirmedBookingsForVenue(Venue venue) {
        List<Client> clients = ClientDAO.getAllClientSummaries();  // Fetch all clients and bookings
        List<Booking> filteredBookings = clients.stream()
                .flatMap(client -> client.getBookings().stream())
                .filter(booking -> booking.getVenue().getVenueId() == venue.getVenueId() && booking.getStatus().equals(BookingStatus.CONFIRMED))
                .collect(Collectors.toList());

        if (filteredBookings.isEmpty()) {
            currentBookingTable.setPlaceholder(new Label("No confirmed bookings for this venue."));
            bookingList.clear();
        } else {
            bookingList.setAll(filteredBookings);
            currentBookingTable.setItems(bookingList);
        }
    }



    // FILTER VENUES BASED ON SELECTED CHECKBOXES
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
            // Step 1: Check if the venue is already booked
            boolean isAvailable = BookingDAO.checkAvailability(
                    selectedVenue.getVenueId(),
                    selectedEvent.getEventDate(),
                    selectedEvent.getEventTime(),
                    selectedEvent.getDuration()
            );

            if (!isAvailable) {
                AlertUtils.showAlert("Booking Error", "The selected venue is already booked for the chosen time slot.", Alert.AlertType.WARNING);
                return;
            }

            // Step 2: Check criteria and build a list of unmet criteria
            StringBuilder unmetCriteria = new StringBuilder();
            if (selectedVenue.getCapacity() < selectedEvent.getRequiredCapacity()) {
                unmetCriteria.append("- Insufficient Capacity (Required: ").append(selectedEvent.getRequiredCapacity())
                        .append(", Available: ").append(selectedVenue.getCapacity()).append(")\n");
            }

            boolean eventCategoryMatch = switch (selectedEvent.getCategory()) {
                case INDOOR -> selectedVenue.getCategory() == VenueCategory.INDOOR || selectedVenue.getCategory() == VenueCategory.CONVERTIBLE;
                case OUTDOOR -> selectedVenue.getCategory() == VenueCategory.OUTDOOR || selectedVenue.getCategory() == VenueCategory.CONVERTIBLE;
                case CONVERTIBLE -> selectedVenue.getCategory() == VenueCategory.CONVERTIBLE;
            };
            if (!eventCategoryMatch) {
                unmetCriteria.append("- Event Category Mismatch (Event: ").append(selectedEvent.getCategory())
                        .append(", Venue: ").append(selectedVenue.getCategory()).append(")\n");
            }

            boolean eventTypeMatch = selectedVenue.getVenueTypes().stream()
                    .map(type -> type.toString().trim().toLowerCase())
                    .collect(Collectors.toSet())
                    .contains(selectedEvent.getEventType().toLowerCase().trim());
            if (!eventTypeMatch) {
                unmetCriteria.append("- Venue Type Mismatch (Event Type: ").append(selectedEvent.getEventType()).append(")\n");
            }

            // Step 3: Show confirmation alert if there are unmet criteria
            if (unmetCriteria.length() > 0) {
                boolean proceed = AlertUtils.showConfirmation("Compatibility Warning",
                        "The selected venue does not meet the following criteria:\n\n" + unmetCriteria + "\nDo you still want to proceed?");
                if (!proceed) {
                    return;
                }
            }

            // Step 4: Book the venue
            LocalDate bookingDate = LocalDate.now();
            String bookingStatus = "CONFIRMED";
            String bookedBy = SessionManager.getCurrentUser().getUsername();

            boolean success = BookingDAO.bookVenue(
                    bookingDate,
                    bookingStatus,
                    selectedEvent.getEventId(),
                    selectedVenue.getVenueId(),
                    selectedEvent.getClientId(),
                    bookedBy
            );

            // Step 5: Notify the user and refresh data
            if (success) {
                AlertUtils.showAlert("Success", "Venue successfully booked!", Alert.AlertType.INFORMATION);
                loadEventData();  // Refresh event list to exclude the booked event
            } else {
                AlertUtils.showAlert("Booking Error", "Failed to book the venue.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
