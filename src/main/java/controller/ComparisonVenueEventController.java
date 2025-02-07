package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Event;
import model.Venue;

import java.text.NumberFormat;
import java.util.Locale;

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

    // Helper Method - to pass data from DashboardController
    public void setVenueAndEvent(Venue venue, Event event, boolean available) {
        this.selectedEvent = event;
        this.selectedVenue = venue;
        this.isAvailable = available;

        updateUI();
    }

    @FXML
    public void initialize() {
        // Ensure UI is populated when loaded
        if (selectedEvent != null && selectedVenue != null) {
            updateUI();
        }
    }

    private void updateUI() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

        // Populate labels
        eventNameLabel.setText(selectedEvent.getEventName());
        venueNameLabel.setText(selectedVenue.getName());

        eventCategoryLabel.setText(selectedEvent.getCategory().toString());
        venueCategoryLabel.setText(selectedVenue.getCategory().toString());

        eventCapacityLabel.setText(numberFormat.format(selectedEvent.getRequiredCapacity()));
        venueCapacityLabel.setText(numberFormat.format(selectedVenue.getCapacity()));

        eventTypeLabel.setText(selectedEvent.getEventType());
        venueTypesLabel.setText(selectedVenue.getVenueTypes().toString());

        eventDateTimeLabel.setText(selectedEvent.getEventDate() + " " + selectedEvent.getEventTime());
        venueAvailabilityLabel.setText(isAvailable ? "✅ Available" : "❌ Not Available");

        venuePriceLabel.setText(currencyFormat.format(selectedVenue.getHirePricePerHour()));

        // Highlight mismatches
        highlightMismatch(eventCategoryLabel, venueCategoryLabel, !selectedEvent.getCategory().equals(selectedVenue.getCategory()));
        highlightMismatch(eventCapacityLabel, venueCapacityLabel, selectedVenue.getCapacity() < selectedEvent.getRequiredCapacity());
        highlightMismatch(eventTypeLabel, venueTypesLabel, !selectedVenue.getVenueTypes().contains(selectedEvent.getEventType()));
    }

    private void highlightMismatch(Label eventLabel, Label venueLabel, boolean mismatch) {
        if (mismatch) {
            eventLabel.getStyleClass().add("highlight-mismatch");
            venueLabel.getStyleClass().add("highlight-mismatch");
        }
    }

    @FXML
    private void closeWindow() {
        ((Stage) eventNameLabel.getScene().getWindow()).close();
    }
}
