package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import service.SceneManager;
import service.VenueService;
import util.AlertUtils;
import util.InputValidationUtils;


/**
 * Controller class for adding a new venue.
 * <p>
 * This class is part of the Model-View-Controller (MVC) architecture and is responsible for handling
 * user interactions on the "Add Venue" view. It manages the retrieval and validation of user input,
 * dynamically creates event type checkboxes, and delegates the venue creation process to the
 * {@link VenueService}. Successful addition of a venue results in navigation to the venue details view
 * via the {@link SceneManager}.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class AddVenueController implements Initializable {

    @FXML private TextField venueNameField;
    @FXML private TextField venueCapacityField;
    @FXML private TextField pricePerHourField;
    @FXML private VBox venueTypeColumn1;
    @FXML private VBox venueTypeColumn2;
    @FXML private ToggleGroup categoryGroup;
    @FXML private RadioButton indoorCategory;
    @FXML private RadioButton outdoorCategory;
    @FXML private RadioButton convertibleCategory;

    /** Array of event types to be displayed as checkboxes. */
    private final String[] eventTypes = {"Gig", "Disco", "Live Concert", "Festival", "Large Live Concert"};

    /** List to hold dynamically created event type checkboxes. */
    private final List<CheckBox> eventTypeCheckBoxes = new ArrayList<>();

    /**
     * Initialises the controller after its root element has been completely processed.
     * <p>
     * This method is automatically called by the JavaFX framework when the FXML file is loaded.
     * It initialises the event type checkboxes and assigns the venue category radio buttons to a toggle group.
     * </p>
     *
     * @param location  The location used to resolve relative paths for the root object, or {@code null} if unknown.
     * @param resources The resources used to localise the root object, or {@code null} if not localised.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseEventTypes();
        initialiseCategoryGroup();
    }

    /**
     * Initialises the toggle group for the venue category radio buttons.
     * <p>
     * This method creates a new {@link ToggleGroup} and assigns the indoor, outdoor, and convertible
     * radio buttons to it, ensuring that only one category can be selected at a time.
     * </p>
     */
    private void initialiseCategoryGroup() {
        categoryGroup = new ToggleGroup();
        indoorCategory.setToggleGroup(categoryGroup);
        outdoorCategory.setToggleGroup(categoryGroup);
        convertibleCategory.setToggleGroup(categoryGroup);
    }

    /**
     * Populates the venue type columns with checkboxes.
     * <p>
     * This method dynamically creates checkboxes for each event type defined in the {@code eventTypes} array.
     * It first clears any existing checkboxes from both columns and then distributes the new checkboxes evenly
     * between two VBox containers.
     * </p>
     */
    private void initialiseEventTypes() {
        // Clear any existing children from the VBox containers and the checkboxes list.
        venueTypeColumn1.getChildren().clear();
        venueTypeColumn2.getChildren().clear();
        eventTypeCheckBoxes.clear();

        // Create a checkbox for each event type and distribute them into two columns.
        for (int i = 0; i < eventTypes.length; i++) {
            CheckBox checkBox = new CheckBox(eventTypes[i]);
            checkBox.setStyle("-fx-font-size: 16px;");      // Set font size for the checkboxes.
            eventTypeCheckBoxes.add(checkBox);

            // Evenly distribute checkboxes into two columns.
            if (i % 2 == 0) {
                venueTypeColumn1.getChildren().add(checkBox);
            } else {
                venueTypeColumn2.getChildren().add(checkBox);
            }
        }
    }

    /**
     * Handles the action of adding a new venue.
     * <p>
     * This method retrieves and validates user inputs from the UI components. It checks that:
     * <ul>
     *     <li>The venue name is not empty.</li>
     *     <li>The venue capacity is a valid integer.</li>
     *     <li>The price per hour is a valid double.</li>
     *     <li>A venue category is selected.</li>
     *     <li>At least one event type is selected.</li>
     * </ul>
     * If any validation fails, an error alert is displayed using {@link AlertUtils}. Upon successful validation,
     * the method calls {@link VenueService#addVenue(String, String, int, double, List)} to add the venue.
     * If the venue is added successfully, a success alert is shown and the scene is switched to the venue details view.
     * </p>
     */
    @FXML
    private void addVenue() {

        // Retrieve and validate the venue name.
        String venueName = venueNameField.getText().trim();
        if (venueName.isEmpty()) {
            AlertUtils.showAlert("Error", "Venue can not be empty", Alert.AlertType.ERROR);
            return;
        }

        // Validate and parse the venue capacity.
        int venueCapacity = InputValidationUtils.validateInteger(venueCapacityField.getText().trim(), "Invalid Venue Capacity");
        if (venueCapacity == -1) return;

        // Validate and parse the price per hour.
        double pricePerHour = InputValidationUtils.validateDouble(pricePerHourField.getText().trim(), "Invalid Price");
        if (pricePerHour == -1) return;

        // Ensure that a venue category is selected.
        RadioButton selectedCategory = (RadioButton) categoryGroup.getSelectedToggle();
        if (selectedCategory == null) {
            AlertUtils.showAlert("Error", "Please select a venue category!", Alert.AlertType.ERROR);
            return;
        }

        // Retrieve the selected venue types from the checkboxes.
        List<String> selectedVenueTypes = getSelectedVenueTypes();
        if (selectedVenueTypes.isEmpty()) {
            AlertUtils.showAlert("Error", "Select at least one venue type!", Alert.AlertType.ERROR);
            return;
        }

        // Attempt to add the venue using the VenueService.
        boolean success = VenueService.addVenue(
                venueName,
                selectedCategory.getText().toUpperCase(),
                venueCapacity,
                pricePerHour,
                selectedVenueTypes
        );

        // Provide feedback based on the outcome of the venue addition.
        if (success) {
            AlertUtils.showAlert("Success", "Venue added successfully!", Alert.AlertType.INFORMATION);
            SceneManager.switchScene("view-venue-details.fxml");
        } else {
            AlertUtils.showAlert("Error", "Failed to add venue. Please try again.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Retrieves the list of selected event types.
     * <p>
     * This helper method iterates over the list of event type checkboxes and collects the text of those
     * that are selected.
     * </p>
     *
     * @return a list of strings representing the selected event types.
     */
    private List<String> getSelectedVenueTypes() {
        List<String> selectedVenueTypes = new ArrayList<>();

        // Iterate through each checkbox and add the text of selected ones to the list.
        for (CheckBox checkBox : eventTypeCheckBoxes) {
            if (checkBox.isSelected()) {
                selectedVenueTypes.add(checkBox.getText());
            }
        }
        return selectedVenueTypes;
    }

    /**
     * Navigates back to the venue details view.
     * <p>
     * This method is triggered when the user opts to cancel or go back from the add venue operation.
     * It uses the {@link SceneManager} to switch the current scene.
     * </p>
     */
    @FXML private void goBack() {
        SceneManager.switchScene("view-venue-details.fxml");
    }
}