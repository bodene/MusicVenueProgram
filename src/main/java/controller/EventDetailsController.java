package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Event;
import util.AlertUtils;
import util.DateUtils;


/**
 * Controller class for displaying event details in a popup window.
 * <p>
 * This controller is responsible for populating the UI with full event details from an {@code Event} object.
 * It updates the UI labels with formatted event information and
 * handles the closing of the popup window with a fade-out transition effect.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class EventDetailsController {

    @FXML private Label eventNameLabel;
    @FXML private Label eventArtistLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label eventTimeLabel;
    @FXML private Label eventDurationLabel;
    @FXML private Label eventCapacityLabel;
    @FXML private Label eventTypeLabel;
    @FXML private Label eventCategoryLabel;
    @FXML private Label eventClientLabel;


    /**
     * Populates the UI with details of the specified event.
     * <p>
     * This method updates each label with event information including name, artist, date, time, duration, capacity,
     * type, category, and client details. If the provided event is {@code null}, an error alert is displayed.
     * </p>
     *
     * @param event the {@code Event} object containing event details
     */
    public void setEventDetails(Event event) {
        if (event == null) {
            AlertUtils.showAlert("Error", "No event data available!", Alert.AlertType.ERROR);
            return;
        }

        // Update each label with the event information using the helper method.
        updateLabel(eventNameLabel, event.getEventName(), "N/A");
        updateLabel(eventArtistLabel, event.getArtist(), "N/A");
        updateLabel(eventDateLabel, DateUtils.formatDate(event.getEventDate()), "N/A");
        updateLabel(eventTimeLabel, DateUtils.formatTime(event.getEventTime()), "N/A");
        updateLabel(eventDurationLabel, event.getDuration() + " hours", "N/A");
        updateLabel(eventCapacityLabel, String.valueOf(event.getRequiredCapacity()), "N/A");
        updateLabel(eventTypeLabel, event.getEventType(), "N/A");
        updateLabel(eventCategoryLabel, event.getCategory().toString(), "N/A");
        updateLabel(eventClientLabel, event.getClient() != null ? event.getClient().getClientName() : "N/A", "N/A");
    }

    /**
     * Updates the text of a label with the given value, or a default value if the provided value is {@code null}.
     *
     * @param label the {@code Label} to be updated
     * @param value the new text value for the label
     * @param defaultValue the default text to display if {@code value} is {@code null}
     */
    private void updateLabel(Label label, String value, String defaultValue) {
        label.setText(value != null ? value : defaultValue);
    }

    /**
     * Closes the current window with a fade-out transition.
     * <p>
     * This method creates a fade transition that gradually decreases the opacity of the window's root node over 300 milliseconds.
     * Once the transition completes, the window is closed.
     * </p>
     */
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) eventNameLabel.getScene().getWindow();

        // Create a fade transition with a duration of 300 milliseconds.
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
        fadeOut.setFromValue(1);    // Start fully opaque.
        fadeOut.setToValue(0);      // End fully transparent.

        // When the fade-out finishes, close the stage.
        fadeOut.setOnFinished(event -> stage.close());
        fadeOut.play();
    }
}