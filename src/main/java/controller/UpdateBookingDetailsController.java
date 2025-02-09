package controller;

import dao.BookingDAO;
import dao.VenueDAO;
import dao.ClientDAO;
import dao.EventDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Booking;
import model.Client;
import model.Event;
import service.SceneManager;
import service.SessionManager;

import model.Venue;
import util.AlertUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class UpdateBookingDetailsController {
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField eventTimeField;
    @FXML private TextField eventArtistField;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private ComboBox<Venue> venueComboBox;

    private Booking booking;

    public void setBooking(Booking booking) {
        this.booking = booking;
        populateFields();
    }

    private void populateFields() {
        Event event = booking.getEvent();

        eventDatePicker.setValue(event.getEventDate());
        eventTimeField.setText(event.getEventTime().toString());
        eventArtistField.setText(event.getArtist());

        clientComboBox.setItems(ClientDAO.getAllClients());
        venueComboBox.setItems(VenueDAO.getAllVenues());

        clientComboBox.setValue(booking.getClient());
        venueComboBox.setValue(booking.getVenue());
    }

    @FXML
    private void checkAvailability() {
        try {
            boolean isAvailable = BookingDAO.checkAvailability(
                    venueComboBox.getValue().getVenueId(),
                    eventDatePicker.getValue(),
                    LocalTime.parse(eventTimeField.getText()),
                    booking.getEvent().getDuration()
            );

            if (isAvailable) {
                AlertUtils.showAlert("Available", "The selected venue is available for the chosen date and time.", Alert.AlertType.INFORMATION);
            } else {
                AlertUtils.showAlert("Unavailable", "The selected venue is not available for the chosen date and time.", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Invalid input or failed to check availability: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }



    public void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {
            SceneManager.switchScene("admin-view.fxml");
        }
    }


    @FXML
    public void saveBookingChanges() {
        try {
            Event event = booking.getEvent();

            // Update Event object
            event.setEventDate(eventDatePicker.getValue());
            event.setEventTime(LocalTime.parse(eventTimeField.getText()));
            event.setArtist(eventArtistField.getText());

            // Update Booking object
            booking.setClient(clientComboBox.getValue());
            booking.setVenue(venueComboBox.getValue());

            // Update the Event and Booking in the database
            boolean eventUpdated = EventDAO.updateEvent(event);
            boolean bookingUpdated = BookingDAO.updateBooking(booking);

            if (eventUpdated && bookingUpdated) {
                AlertUtils.showAlert("Success", "Booking and event details updated successfully!", Alert.AlertType.INFORMATION);
                closeWindow();
            } else {
                AlertUtils.showAlert("Error", "Failed to update booking and event details.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Failed to save changes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) eventDatePicker.getScene().getWindow();
        stage.close();
    }
}
