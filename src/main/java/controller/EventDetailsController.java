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

    // SHOW FULL EVENT DETAILS ON POPUP SCREEN
    public void setEventDetails(Event event) {
        if (event == null) {
            AlertUtils.showAlert("Error", "No event data available!", Alert.AlertType.ERROR);
            return;
        }

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

    private void updateLabel(Label label, String value, String defaultValue) {
        label.setText(value != null ? value : defaultValue);
    }

    public void initialize(URL location, ResourceBundle resources) {
        // Ensure styles are applied correctly
        if (eventNameLabel.getScene() != null) {
            eventNameLabel.getScene().getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) eventNameLabel.getScene().getWindow();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> stage.close());
        fadeOut.play();
    }
}
