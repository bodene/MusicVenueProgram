package controller;

import dao.BookingDAO;
import dao.VenueDAO;
import dao.ClientDAO;
import dao.EventDAO;
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
import java.time.LocalTime;


/**
 * Controller class for updating booking details.
 * <p>
 * This class manages the update of booking and event details within the booking process. It populates the UI fields
 * with the current booking details, provides functionality to check venue availability based on updated information,
 * and saves changes to both the event and booking in the database.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class UpdateBookingDetailsController {

    @FXML private DatePicker eventDatePicker;
    @FXML private TextField eventTimeField;
    @FXML private TextField eventArtistField;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private ComboBox<Venue> venueComboBox;

    /** The booking object being updated. */
    private Booking booking;

    /**
     * Sets the booking to be updated and populates the UI fields.
     * <p>
     * This method is called by the previous view to pass the booking that needs to be updated.
     * </p>
     *
     * @param booking the booking object to update
     */
    public void setBooking(Booking booking) {
        this.booking = booking;
        populateFields();
    }

    /**
     * Populates the UI fields with the current details from the booking.
     * <p>
     * The method extracts the event details from the booking and sets the values for the event date,
     * event time, artist, client, and venue. It also loads available clients and venues from the database.
     * </p>
     */
    private void populateFields() {
        Event event = booking.getEvent();

        // Set event details in UI components.
        eventDatePicker.setValue(event.getEventDate());
        eventTimeField.setText(event.getEventTime().toString());
        eventArtistField.setText(event.getArtist());

        // Populate client and venue combo boxes with data from the database.
        clientComboBox.setItems(ClientDAO.getAllClients());
        venueComboBox.setItems(VenueDAO.getAllVenues());

        // Set the current client and venue values.
        clientComboBox.setValue(booking.getClient());
        venueComboBox.setValue(booking.getVenue());
    }

    /**
     * Checks the availability of the selected venue at the specified date and time.
     * <p>
     * This method retrieves the selected venue, event date, and event time, and checks availability using
     * {@link BookingDAO#checkAvailability(int, java.time.LocalDate, java.time.LocalTime, int)}. It then displays
     * an alert indicating whether the venue is available or not.
     * </p>
     */
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
            // Display an error alert if input is invalid or availability check fails.
            AlertUtils.showAlert("Error", "Invalid input or failed to check availability: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Navigates to the settings view.
     * <p>
     * Depending on the current user's role, this method switches the scene to either the manager or admin settings view.
     * </p>
     */
    public void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {
            SceneManager.switchScene("admin-view.fxml");
        }
    }

    /**
     * Saves changes made to the booking and event details.
     * <p>
     * This method updates the event object with the new date, time, and artist information and updates the booking
     * object with the selected client and venue. It then saves the changes to the database using the respective DAO methods.
     * Upon successful update, an information alert is shown and the window is closed; otherwise, an error alert is displayed.
     * </p>
     */
    @FXML
    public void saveBookingChanges() {

        try {
            Event event = booking.getEvent();

            // Update the event object with new details.
            event.setEventDate(eventDatePicker.getValue());
            event.setEventTime(LocalTime.parse(eventTimeField.getText()));
            event.setArtist(eventArtistField.getText());

            // Update the booking object with selected client and venue.
            booking.setClient(clientComboBox.getValue());
            booking.setVenue(venueComboBox.getValue());

            // Update the event and booking in the database.
            boolean eventUpdated = EventDAO.updateEvent(event);
            boolean bookingUpdated = BookingDAO.updateBooking(booking);

            if (eventUpdated && bookingUpdated) {
                AlertUtils.showAlert("Success", "Booking and event details updated successfully!", Alert.AlertType.INFORMATION);
                closeWindow();
            } else {
                AlertUtils.showAlert("Error", "Failed to update booking and event details.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Failed to save changes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Closes the update booking window.
     * <p>
     * This method retrieves the current stage from one of the UI components and closes it.
     * </p>
     */
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) eventDatePicker.getScene().getWindow();
        stage.close();
    }
}