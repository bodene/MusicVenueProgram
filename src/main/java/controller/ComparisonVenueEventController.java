package controller;

import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Event;
import model.Venue;
import model.VenueType;
import util.NumberUtils;


/**
 * Controller class for comparing details between a venue and an event.
 * <p>
 * This class is responsible for displaying a side-by-side comparison of an event and a venue.
 * It updates UI labels with formatted details, highlights mismatches in key attributes (such as category,
 * capacity, and event type), and provides a mechanism to close the comparison window.
 * </p>
 *
 * <p>
 * The controller leverages helper methods to safely format strings, generate a comma-separated list
 * of venue types, and verify if the event type matches any of the venue's types.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class ComparisonVenueEventController {

    @FXML private Label eventNameLabel;
    @FXML private Label venueNameLabel;
    @FXML private Label eventCategoryLabel;
    @FXML private Label venueCategoryLabel;
    @FXML private Label eventCapacityLabel;
    @FXML private Label venueCapacityLabel;
    @FXML private Label eventTypeLabel;
    @FXML private Label venueTypesLabel;
    @FXML private Label eventDateTimeLabel;
    @FXML private Label venueAvailabilityLabel;
    @FXML private Label venuePriceLabel;

    /** The selected event and Venue to be compared. */
    private Event selectedEvent;
    private Venue selectedVenue;

    /** Flag indicating whether the venue is available for the event. */
    private boolean isAvailable;

    /**
     * Sets the selected venue, event, and availability status, then updates the UI.
     * <p>
     * This method is used to pass the details from the dashboard to the comparison view.
     * Once the values are set, it calls {@link #updateUI()} to refresh the displayed data.
     * </p>
     *
     * @param venue     the selected venue
     * @param event     the selected event
     * @param available {@code true} if the venue is available for the event, {@code false} otherwise
     */
    public void setVenueAndEvent(Venue venue, Event event, boolean available) {
        this.selectedEvent = event;
        this.selectedVenue = venue;
        this.isAvailable = available;
        updateUI();
    }

    /**
     * Updates the UI labels with details from the selected event and venue.
     * <p>
     * This method populates the various labels with formatted information retrieved from the event
     * and venue objects. It also highlights mismatches in event category, capacity, and event type by
     * applying a specific style if discrepancies are detected.
     * </p>
     */
    private void updateUI() {
        if (selectedEvent == null || selectedVenue == null) {
            return;
        }

        // Set event and venue names using safe string conversion.
        eventNameLabel.setText(safeString(selectedEvent.getEventName()));
        venueNameLabel.setText(safeString(selectedVenue.getName()));

        // Set event and venue categories.
        eventCategoryLabel.setText(safeString(selectedEvent.getCategory().toString()));
        venueCategoryLabel.setText(safeString(selectedVenue.getCategory().toString()));

        // Set capacities with formatted numbers.
        eventCapacityLabel.setText(NumberUtils.formatNumber(selectedEvent.getRequiredCapacity()));
        venueCapacityLabel.setText(NumberUtils.formatNumber(selectedVenue.getCapacity()));

        // Set event type and formatted venue types.
        eventTypeLabel.setText(safeString(selectedEvent.getEventType()));
        venueTypesLabel.setText(formatVenueTypes(selectedVenue.getVenueTypes()));

        // Set event date and time, and display availability.
        eventDateTimeLabel.setText(selectedEvent.getEventDate() + " " + selectedEvent.getEventTime());
        venueAvailabilityLabel.setText(isAvailable ? "✅ Available" : "❌ Not Available");

        // Set the venue price formatted as currency.
        venuePriceLabel.setText(NumberUtils.formatCurrency(selectedVenue.getHirePricePerHour()));

        // Highlight mismatches in key fields.
        highlightMismatch(eventCategoryLabel, venueCategoryLabel,
                !selectedEvent.getCategory().equals(selectedVenue.getCategory()));
        highlightMismatch(eventCapacityLabel, venueCapacityLabel,
                selectedVenue.getCapacity() < selectedEvent.getRequiredCapacity());
        highlightMismatch(eventTypeLabel, venueTypesLabel,
                !hasMatchingVenueType(selectedEvent.getEventType(), selectedVenue.getVenueTypes()));
    }

    /**
     * Highlights label mismatches by applying a CSS style if a discrepancy is detected.
     * <p>
     * If a mismatch is identified between the event and venue attributes, this method adds the
     * "highlight-mismatch" style class to both labels.
     * </p>
     *
     * @param eventLabel the label displaying the event attribute
     * @param venueLabel the label displaying the venue attribute
     * @param mismatch   {@code true} if there is a discrepancy, {@code false} otherwise
     */
    private void highlightMismatch(Label eventLabel, Label venueLabel, boolean mismatch) {
        if (mismatch) {
            eventLabel.getStyleClass().add("highlight-mismatch");
            venueLabel.getStyleClass().add("highlight-mismatch");
        }
    }

    /**
     * Checks if the event type matches any of the venue types.
     * <p>
     * This method compares the event type (case-insensitive) with the list of venue types. It returns
     * {@code true} if a match is found.
     * </p>
     *
     * @param eventType  the type of the event
     * @param venueTypes the list of venue types associated with the venue
     * @return {@code true} if the event type matches any of the venue types, {@code false} otherwise
     */
    private boolean hasMatchingVenueType(String eventType, List<VenueType> venueTypes) {
        List<String> venueTypeNames = venueTypes.stream()
                .map(VenueType::getVenueType)
                .map(String::toLowerCase)
                .toList();
        return venueTypeNames.contains(eventType.toLowerCase());
    }

    /**
     * Formats a list of venue types into a comma-separated string.
     * <p>
     * If the list is null or empty, it returns "N/A". Otherwise, it concatenates the venue type names with commas.
     * </p>
     *
     * @param venueTypes the list of venue types
     * @return a comma-separated string of venue types or "N/A" if the list is empty
     */
    private String formatVenueTypes(List<VenueType> venueTypes) {
        if (venueTypes == null || venueTypes.isEmpty()) {
            return "N/A";
        }
        return venueTypes.stream()
                .map(VenueType::getVenueType)
                .collect(Collectors.joining(", "));
    }

    /**
     * Safely converts a string to a non-null value.
     * <p>
     * This method returns the provided string if it is not null; otherwise, it returns "N/A".
     * </p>
     *
     * @param value the string to be checked
     * @return the original string if not null, or "N/A" if null
     */
    private String safeString(String value) {
        return value != null ? value : "N/A";
    }

    /**
     * Closes the current window.
     * <p>
     * This method retrieves the current stage from the {@code eventNameLabel}'s scene and closes it.
     * </p>
     */
    @FXML private void closeWindow() {
        ((Stage) eventNameLabel.getScene().getWindow()).close();
    }
}