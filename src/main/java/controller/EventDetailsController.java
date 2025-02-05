package controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Event;

import java.net.URL;
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

    public void setEventDetails(Event event) {
        if (event == null) return;

        eventNameLabel.setText(event.getEventName());
        eventArtistLabel.setText(event.getArtist());
        eventDateLabel.setText(event.getEventDate().toString());
        eventTimeLabel.setText(event.getEventTime().toString());
        eventDurationLabel.setText(event.getDuration() + " hours");
        eventCapacityLabel.setText(String.valueOf(event.getRequiredCapacity()));
        eventTypeLabel.setText(event.getEventType());
        eventCategoryLabel.setText(event.getCategory().toString());
        eventClientLabel.setText(event.getClientName());
    }

    public void initialise(URL location, ResourceBundle resources) {
        eventNameLabel.getScene().getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
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
