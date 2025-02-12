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
import service.VenueMatchingService;
import util.AlertUtils;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Controller class for the Dashboard view.
 * <p>
 * This class is responsible for managing and displaying events, venues, and bookings on the dashboard.
 * It fetches data from the database via DAO classes, sets up table views with appropriate columns and listeners,
 * and provides functionality for filtering, sorting, and booking venues for selected events.
 * </p>
 * <p>
 * The dashboard supports:
 * <ul>
 *   <li>Displaying available events and enabling event selection.</li>
 *   <li>Loading and sorting venues based on compatibility with the selected event.</li>
 *   <li>Displaying current confirmed bookings for a selected venue.</li>
 *   <li>Filtering venues using various criteria such as availability, capacity, event category, and venue type.</li>
 *   <li>Providing detailed views for events and venue comparisons.</li>
 *   <li>Booking venues for events and handling booking errors.</li>
 * </ul>
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class DashboardController {

    /* EVENT TABLE COMPONENTS */
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Integer> eventIdColumn;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> eventArtistColumn;
    @FXML private TableColumn<Event, String> eventClientColumn;
    @FXML private TableColumn<Event, String> eventDateColumn;
    @FXML private TableColumn<Event, String> eventTimeColumn;
    private ObservableList<Event> eventList;

    /* VENUE TABLE COMPONENTS */
    @FXML private TableView<Venue> venueTable;
    @FXML private TableColumn<Venue, Integer> venueNoColumn;
    @FXML private TableColumn<Venue, String> venueNameColumn;
    @FXML private TableColumn<Venue, Integer> compatibilityScoreColumn;
    private ObservableList<Venue> venueList = FXCollections.observableArrayList();

    /* BOOKINGS TABLE COMPONENTS */
    @FXML private TableView<Booking> currentBookingTable;
    @FXML private TableColumn<Booking, Integer> bookingIdColumn;
    @FXML private TableColumn<Booking, String> bookedEventDateColumn;
    @FXML private TableColumn<Booking, String> bookedEventTimeColumn;
    @FXML private TableColumn<Booking, String> bookedEventNameColumn;
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    /* CHECK BOX FILTERS */
    @FXML private CheckBox availableCheckbox;
    @FXML private CheckBox sufficientCapacityCheckbox;
    @FXML private CheckBox eventTypeCheckbox;
    @FXML private CheckBox venueCategoryCheckbox;

    /** The event and venue selected by the user. */
    private Event selectedEvent;
    private Venue selectedVenue;

    /**
     * Initialises the Dashboard controller after the FXML elements have been loaded.
     * <p>
     * This method sets up the event, venue, and booking table columns, initialises the placeholder for
     * the booking table, and loads the event data asynchronously.
     * </p>
     */
    @FXML
    public void initialize() {
        setupEventTableColumns();
        eventList = FXCollections.observableArrayList();
        setupVenueTableColumns();
        setUpBookingTableColumns();
        currentBookingTable.setPlaceholder(new Label("Please select a venue to view bookings."));

        // Load event data after UI initialisation.
        Platform.runLater(this::loadEventData);
    }

    /**
     * Configures the columns of the event table and sets a listener for event selection.
     * <p>
     * The listener loads the venues related to the selected event.
     * </p>
     */
    private void setupEventTableColumns() {

        // Set up event table columns with property value factories.
        eventIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventId()));
        eventNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventName()));
        eventArtistColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getArtist()));
        eventClientColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getClientName()));
        eventDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventDate().toString()));
        eventTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEventTime().toString()));

        // Add listener to load venues when an event is selected.
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEvent = newSelection;
                loadVenuesForEvent(selectedEvent);
            }
        });
    }

    /**
     * Returns the currently selected event from the event table.
     *
     * @return the selected {@code Event} or {@code null} if none is selected
     */
    private Event getSelectedEvent() {
        return eventTable.getSelectionModel().getSelectedItem();
    }

    /**
     * Configures the columns of the venue table and sets a listener for venue selection.
     * <p>
     * This method also sorts the venues by their compatibility score with the selected event.
     * </p>
     */
    private void setupVenueTableColumns() {

        // Set up venue table columns.
        venueNoColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getVenueId()));
        venueNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        // Calculate and display compatibility score for each venue.
        compatibilityScoreColumn.setCellValueFactory(cellData -> {
            try {
                Event selectedEvent = getSelectedEvent();
                if (selectedEvent == null) return new SimpleObjectProperty<>(0);
                return new SimpleObjectProperty<>(VenueMatchingService.calculateCompatibility(cellData.getValue(), selectedEvent));
            } catch (SQLException e) {
                AlertUtils.showAlert("Error calculating venue compatibility: ", e.getMessage(), Alert.AlertType.ERROR);
                return new SimpleObjectProperty<>(0);
            }
        });

        // Add listener to load confirmed bookings when a venue is selected.
        venueTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedVenue = newSelection;
                loadConfirmedBookingsForVenue(selectedVenue);
            }
        });

        // Sort venues by compatibility score.
        sortVenuesByCompatibility();
    }

    /**
     * Sorts the venue table by compatibility score in descending order.
     */
    @FXML
    private void sortVenuesByCompatibility() {
        venueTable.getSortOrder().clear();
        venueTable.getSortOrder().add(compatibilityScoreColumn);
        compatibilityScoreColumn.setSortType(TableColumn.SortType.DESCENDING);
    }

    /**
     * Loads event data from the database.
     * <p>
     * This method retrieves all events, excludes those that have confirmed bookings,
     * and updates the event table with the available events.
     * </p>
     */
    private void loadEventData() {
        // Retrieve all events from the database.
        List<Event> allEvents = EventDAO.getAllEvents();

        // Retrieve all clients and their bookings.
        List<Client> allClients = ClientDAO.getAllClientSummaries();

        // Extract event IDs that already have confirmed bookings.
        List<Integer> bookedEventIds = allClients.stream()
                .flatMap(client -> client.getBookings().stream())
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .map(booking -> booking.getEvent().getEventId())
                .collect(Collectors.toList());

        // Filter events to include only those that are not booked.
        List<Event> availableEvents = allEvents.stream()
                .filter(event -> !bookedEventIds.contains(event.getEventId()))
                .collect(Collectors.toList());

        // Update the event table with available events.
        if (!availableEvents.isEmpty()) {
            eventList.setAll(availableEvents);
        } else {
            eventList.clear();
            eventTable.setPlaceholder(new Label("No available events."));
        }

        eventTable.setItems(eventList);
    }


    /**
     * Loads venues for the selected event and applies compatibility calculations.
     * <p>
     * The method retrieves all venues, calculates their compatibility with the given event,
     * sorts them by compatibility score, and updates the venue table.
     * </p>
     *
     * @param event the selected event for which venues are to be loaded
     */
    private void loadVenuesForEvent(Event event) {
        try {
            // Retrieve all venues from the database.
            List<Venue> allVenues = VenueDAO.getAllVenues();

            // Calculate compatibility scores and sort venues.
            List<Venue> filteredVenues = allVenues.stream()
                .map(venue -> {
                    try {
                        venue.setCompatibilityScore(VenueMatchingService.calculateCompatibility(venue, event));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return venue;
                })
                .sorted((v1, v2) -> Double.compare(v2.getCompatibilityScore(), v1.getCompatibilityScore()))
                        .collect(Collectors.toList());

                venueList.setAll(filteredVenues);
                venueTable.setItems(venueList);

                // Alert if no matching venues are found.
                if (filteredVenues.isEmpty()) {
                    AlertUtils.showAlert("No Matches", "Unable to find a match, please add more venue data or try loosening up your criteria.", Alert.AlertType.WARNING);
                }

            } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Sets up the columns for the booking table.
     * <p>
     * This method configures the booking table to display booking ID, event date, event time, and event name.
     * </p>
     */
    private void setUpBookingTableColumns() {
        bookingIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBookingId()));
        bookedEventDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvent().getEventDate().toString()));
        bookedEventTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvent().getEventTime().toString()));
        bookedEventNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvent().getEventName()));
    }

    /**
     * Loads confirmed bookings for the selected venue.
     * <p>
     * The method retrieves all clients and their bookings, filters the bookings by the selected venue and
     * confirmed status, and updates the booking table.
     * </p>
     *
     * @param venue the selected venue for which bookings are to be loaded
     */
    private void loadConfirmedBookingsForVenue(Venue venue) {

        // Retrieve all client summaries, which include bookings.
        List<Client> clients = ClientDAO.getAllClientSummaries();
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

    /**
     * Filters the list of venues based on the selected checkboxes.
     * <p>
     * The filtering criteria include availability, capacity, event category, and venue type matching.
     * If no event is selected, a warning alert is displayed.
     * </p>
     */
    @FXML
    private void filterVenues() {
        if (selectedEvent == null) {
            AlertUtils.showAlert("No Event Selected", "Please select an event before filtering venues.", Alert.AlertType.WARNING);
            return;
        }
        List<Venue> filteredList = venueList.stream()
                .filter(venue -> {
                    boolean match = true;

                    // Filter by Availability.
                    if (availableCheckbox.isSelected()) {
                        try {
                            match &= BookingDAO.checkAvailability(venue.getVenueId(), selectedEvent.getEventDate(), selectedEvent.getEventTime(), selectedEvent.getDuration());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // Filter by Capacity.
                    if (sufficientCapacityCheckbox.isSelected()) {
                        match &= venue.getCapacity() >= selectedEvent.getRequiredCapacity();
                    }

                    // Filter by Venue Category.
                    if (venueCategoryCheckbox.isSelected()) {
                        boolean categoryMatch = switch (selectedEvent.getCategory()) {
                            case INDOOR -> venue.getCategory() == VenueCategory.INDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
                            case OUTDOOR -> venue.getCategory() == VenueCategory.OUTDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
                            case CONVERTIBLE -> venue.getCategory() == VenueCategory.CONVERTIBLE;
                        };
                        match &= categoryMatch;
                    }

                    // Filter by Venue Type.
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
        VenueMatchingService matchingService = new VenueMatchingService();

        // Get current events
        List<Event> activeEvents = new ArrayList<>(eventList);
        List<VenueMatchingService.AutoMatchResult> recommendations = matchingService.getRecommendations(activeEvents);

        // Build recommendation text.
        StringBuilder recommendationText = new StringBuilder();
        for (VenueMatchingService.AutoMatchResult result : recommendations) {
            recommendationText.append("Event: ").append(result.event.getEventName()).append("\n");
            if (result.candidate == null) {
                recommendationText.append("  Recommended Venue: NONE\n")
                        .append("  Unmet Criteria: ").append(String.join(", ", result.unmetCriteria))
                        .append("\n\n");
            } else {
                recommendationText.append("  Recommended Venue: ").append(result.candidate.venue.getName()).append("\n")
                        .append("  Compatibility Score: ").append(result.candidate.score).append("\n")
                        .append("  Capacity: ").append(result.candidate.venue.getCapacity())
                        .append(" (Required: ").append(result.event.getRequiredCapacity())
                        .append(", Diff: ").append(result.candidate.capacityDiff).append(")\n");
                if (!result.unmetCriteria.isEmpty()) {
                    recommendationText.append("  Unmet Criteria: ").append(String.join(", ", result.unmetCriteria)).append("\n");
                } else {
                    recommendationText.append("  All criteria met.\n");
                }
                recommendationText.append("\n");
            }
        }

        // Create a wide alert with the recommendation text.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Auto-Match Recommendations");
        alert.setHeaderText("Review and Bulk Book All Recommendations");
        alert.getDialogPane().setMinWidth(800);

        TextArea textArea = new TextArea(recommendationText.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(780);
        textArea.setPrefHeight(400);
        alert.getDialogPane().setContent(textArea);

        ButtonType bookAllButton = new ButtonType("Book All", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(bookAllButton, cancelButton);

        Optional<ButtonType> userResponse = alert.showAndWait();
        if (userResponse.isPresent() && userResponse.get() == bookAllButton) {
            // Use the service to bulk book recommendations.
            Map<Event, Boolean> bookingResults = matchingService.bulkBookRecommendations(recommendations);
            StringBuilder bookingResultText = new StringBuilder();
            bookingResults.forEach((event, success) -> {
                if (success) {
                    bookingResultText.append("Booked '").append(event.getEventName()).append("'.\n");
                } else {
                    bookingResultText.append("Failed to book '").append(event.getEventName()).append("'.\n");
                }
            });

            Alert bookingAlert = new Alert(Alert.AlertType.INFORMATION);
            bookingAlert.setTitle("Bulk Booking Results");
            bookingAlert.setHeaderText("Results of Bulk Booking");
            bookingAlert.getDialogPane().setMinWidth(600);
            bookingAlert.setContentText(bookingResultText.toString());
            bookingAlert.showAndWait();

            // Refresh events if needed.
            loadEventData();
        }
    }

    /**
     * Displays detailed venue comparison information.
     * <p>
     * This method opens a new window that compares the selected venue and event. It loads the comparison
     * view, sets the appropriate data, and displays the window.
     * </p>
     */
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

            // Pass selected venue and event data to the comparison controller.
            ComparisonVenueEventController controller = loader.getController();
            controller.setVenueAndEvent(selectedVenue, selectedEvent, BookingDAO.checkAvailability(
                    selectedVenue.getVenueId(), selectedEvent.getEventDate(), selectedEvent.getEventTime(), selectedEvent.getDuration()
            ));

            // Set the stage for the venue and event comparison
            Stage stage = new Stage();
            stage.setTitle("Venue & Event Comparison");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Failed to load venue details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Books the selected venue for the selected event.
     * <p>
     * This method verifies that both an event and a venue have been selected. It then performs
     * additional compatibility checks (capacity, category, and type) and, if necessary, warns the user
     * about unmet criteria. If the user proceeds, the venue is booked and the event list is refreshed.
     * </p>
     */
    @FXML
    private void bookVenue() {
        if (selectedEvent == null || selectedVenue == null) {
            AlertUtils.showAlert("Booking Error", "Please select both an event and a venue before booking.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Step 1: Check venue availability.
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

            // Step 2: Build a list of unmet criteria.
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

            // Step 3: Prompt user if there are unmet criteria.
            if (!unmetCriteria.isEmpty()) {
                boolean proceed = AlertUtils.showConfirmation("Compatibility Warning",
                        "The selected venue does not meet the following criteria:\n\n" + unmetCriteria +
                                "\nDo you still want to proceed?");
                if (!proceed) {
                    return;
                }
            }

            // Step 4: Book the venue.
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

            // Step 5: Notify user and refresh event list.
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

    /**
     * Navigates to the settings view based on the current user's role.
     * <p>
     * If the user is a manager, the manager settings view is shown; otherwise, the admin view is displayed.
     * </p>
     */
    @FXML
    private void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {
            SceneManager.switchScene("admin-view.fxml");
        }
    }

    /**
     * Displays detailed information about the selected event.
     * <p>
     * This method loads the event details view in a modal window and passes the selected event data to the controller.
     * </p>
     */
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

            // Pass event details to the controller.
            EventDetailsController controller = loader.getController();
            controller.setEventDetails(selectedEvent);

            // Create a modal popup window.
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

    /**
     * Logs out the current user and switches to the main view.
     */
    @FXML private void logout() {
        SceneManager.switchScene("main-view.fxml");
    }
}
