package controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Event;
import service.EventService;
import util.AlertUtils;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

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

    // Shows Full event details in a popup screen
    public void setEventDetails(Event event) {
        if (event == null) {
            AlertUtils.showAlert("Error", "No event data available!", javafx.scene.control.Alert.AlertType.ERROR);
            return;
        }

        eventNameLabel.setText(event.getEventName());
        eventArtistLabel.setText(event.getArtist());
        eventDateLabel.setText(EventService.formatDate(Optional.ofNullable(event.getEventDate().toString())));
        eventTimeLabel.setText(EventService.formatTime(Optional.ofNullable(event.getEventTime().toString())));
        eventDurationLabel.setText(event.getDuration() + " hours");
        eventCapacityLabel.setText(String.valueOf(event.getRequiredCapacity()));
        eventTypeLabel.setText(event.getEventType());
        eventCategoryLabel.setText(event.getCategory().toString());
        eventClientLabel.setText(event.getClient().getClientName());
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
