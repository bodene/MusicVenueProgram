package controller;
//DONE
import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Event;
import model.Venue;
import model.VenueType;
import util.NumberUtils;

public class ComparisonVenueEventController {

    @FXML private Label eventNameLabel, venueNameLabel;
    @FXML private Label eventCategoryLabel, venueCategoryLabel;
    @FXML private Label eventCapacityLabel, venueCapacityLabel;
    @FXML private Label eventTypeLabel, venueTypesLabel;
    @FXML private Label eventDateTimeLabel, venueAvailabilityLabel;
    @FXML private Label venuePriceLabel;

    private Event selectedEvent;
    private Venue selectedVenue;
    private boolean isAvailable;

    // HELPER METHOD - PASSES DETAILS FROM DASHBOARD
    public void setVenueAndEvent(Venue venue, Event event, boolean available) {
        this.selectedEvent = event;
        this.selectedVenue = venue;
        this.isAvailable = available;
        updateUI();
    }

    // POPULATE LABELS FOR FORMATTED DATA
    private void updateUI() {
        if (selectedEvent == null || selectedVenue == null) return;

        eventNameLabel.setText(safeString(selectedEvent.getEventName()));
        venueNameLabel.setText(safeString(selectedVenue.getName()));

        eventCategoryLabel.setText(safeString(selectedEvent.getCategory().toString()));
        venueCategoryLabel.setText(safeString(selectedVenue.getCategory().toString()));

        eventCapacityLabel.setText(NumberUtils.formatNumber(selectedEvent.getRequiredCapacity()));
        venueCapacityLabel.setText(NumberUtils.formatNumber(selectedVenue.getCapacity()));

        eventTypeLabel.setText(safeString(selectedEvent.getEventType()));
        venueTypesLabel.setText(formatVenueTypes(selectedVenue.getVenueTypes()));

        eventDateTimeLabel.setText(selectedEvent.getEventDate() + " " + selectedEvent.getEventTime());
        venueAvailabilityLabel.setText(isAvailable ? "✅ Available" : "❌ Not Available");

        venuePriceLabel.setText(NumberUtils.formatCurrency(selectedVenue.getHirePricePerHour()));

        highlightMismatch(eventCategoryLabel, venueCategoryLabel, !selectedEvent.getCategory().equals(selectedVenue.getCategory()));
        highlightMismatch(eventCapacityLabel, venueCapacityLabel, selectedVenue.getCapacity() < selectedEvent.getRequiredCapacity());
        highlightMismatch(eventTypeLabel, venueTypesLabel, !hasMatchingVenueType(selectedEvent.getEventType(), selectedVenue.getVenueTypes()));
    }

    // HELPER METHOD - HIGHLIGHT MISMATCHES
    private void highlightMismatch(Label eventLabel, Label venueLabel, boolean mismatch) {
        if (mismatch) {
            eventLabel.getStyleClass().add("highlight-mismatch");
            venueLabel.getStyleClass().add("highlight-mismatch");
        }
    }

    private boolean hasMatchingVenueType(String eventType, List<VenueType> venueTypes) {
        List<String> venueTypeNames = venueTypes.stream()
                .map(VenueType::getVenueType)
                .map(String::toLowerCase) // Convert to lowercase for case-insensitive comparison
                .collect(Collectors.toList());

        return venueTypeNames.contains(eventType.toLowerCase());
    }

    private String formatVenueTypes(List<VenueType> venueTypes) {
        if (venueTypes == null || venueTypes.isEmpty()) return "N/A";
        return venueTypes.stream()
                .map(VenueType::getVenueType)
                .collect(Collectors.joining(", "));
    }

    private String safeString(String value) {
        return value != null ? value : "N/A";
    }

    @FXML private void closeWindow() {
        ((Stage) eventNameLabel.getScene().getWindow()).close();
    }
}