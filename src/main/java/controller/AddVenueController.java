package controller;

import model.Venue;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.VenueService;
import util.AlertUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddVenueController implements Initializable {

    @FXML private TextField venueNameField, venueCapacityField, pricePerHourField;
    @FXML private HBox venueTypeContainer;
    @FXML private VBox venueTypeColumn1, venueTypeColumn2;
    @FXML private ToggleGroup categoryGroup;
    @FXML private RadioButton indoorCategory, outdoorCategory, convertibleCategory;

    private final String[] eventTypes = {"Gig", "Disco", "Live Concert", "Festival", "Large Live Concert"};
    private final List<CheckBox> eventTypeCheckBoxes = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseEventTypes();
        initialiseCategoryGroup();
    }

    // Assign a ToggleGroup to the RadioButtons
    private void initialiseCategoryGroup() {
        categoryGroup = new ToggleGroup();
        indoorCategory.setToggleGroup(categoryGroup);
        outdoorCategory.setToggleGroup(categoryGroup);
        convertibleCategory.setToggleGroup(categoryGroup);

        // Increase font size for radio buttons
        indoorCategory.setStyle("-fx-font-size: 16px;");
        outdoorCategory.setStyle("-fx-font-size: 16px;");
        convertibleCategory.setStyle("-fx-font-size: 16px;");
    }

    // Populate the VBox with CheckBoxes for multiple selections
    private void initialiseEventTypes() {
        // Clear previous checkboxes
        venueTypeColumn1.getChildren().clear();
        venueTypeColumn2.getChildren().clear();
        eventTypeCheckBoxes.clear();

        for (int i = 0; i < eventTypes.length; i++) {
            CheckBox checkBox = new CheckBox(eventTypes[i]);
            checkBox.setStyle("-fx-font-size: 16px;");
            eventTypeCheckBoxes.add(checkBox);

            // Distribute checkboxes into two columns
            if (i % 2 == 0) {
                venueTypeColumn1.getChildren().add(checkBox);
            } else {
                venueTypeColumn2.getChildren().add(checkBox);
            }
        }
    }

    // Add new Venue
    @FXML
    private void addVenue() {
        // Get the venue name
        String venueName = venueNameField.getText().trim();
        if (venueName.isEmpty()) {
            AlertUtils.showAlert("Error", "Venue can not be empty", Alert.AlertType.ERROR);
            return;
        }
        // Get Venue capacity and validate
        int venueCapacity = validateIntegerInput(venueCapacityField.getText().trim(), "Invalid Venue Capacity, must be a positive number.");
        if (venueCapacity == -1) return;

        // Get price per hour
        double pricePerHour = validateDoubleInput(pricePerHourField.getText().trim(), "Invalid price, must be a positive number.");
        if (pricePerHour == -1) return;

        // Get selected category
        RadioButton selectedCategory = (RadioButton) categoryGroup.getSelectedToggle();
        if (selectedCategory == null) {
            AlertUtils.showAlert("Error", "Please select a venue category!", Alert.AlertType.ERROR);
            return;
        }
        String category = selectedCategory.getText().toUpperCase();

        // Retrieve selected checkboxes from both columns
        List<String> selectedVenueTypes = getSelectedVenueTypes();
        if (selectedVenueTypes.isEmpty()) {
            AlertUtils.showAlert("Error", "Select at least one venue type!", Alert.AlertType.ERROR);
            return;
        }
        Venue venue = new Venue(venueName, category, venueCapacity, pricePerHour);
        boolean success = VenueService.addVenue(venue, selectedVenueTypes);

        if (success) {
            AlertUtils.showAlert("Success", "Venue added successfully!", Alert.AlertType.INFORMATION);
            SceneManager.switchScene("view-venue-details.fxml");
        } else {
            AlertUtils.showAlert("Error", "Failed to add venue. Please try again.", Alert.AlertType.ERROR);
        }
    }

    // Helper Method - validate integer input
    private int validateIntegerInput(String input, String errorMessage) {
        try {
            int value = Integer.parseInt(input);
            if (value <= 0) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException e) {
            AlertUtils.showAlert("Error", errorMessage, Alert.AlertType.ERROR);
            return -1;
        }
    }

    // Helper Method - validate double input
    private double validateDoubleInput(String input, String errorMessage) {
        try {
            double value = Double.parseDouble(input);
            if (value <= 0.0) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException e) {
            AlertUtils.showAlert("Error", errorMessage, Alert.AlertType.ERROR);
            return -1;
        }
    }

    // Helper Method - get selected Venue types
    private List<String> getSelectedVenueTypes() {
        List<String> selectedVenueTypes = new ArrayList<>();
        for (CheckBox checkBox : eventTypeCheckBoxes) {
            if (checkBox.isSelected()) {
                selectedVenueTypes.add(checkBox.getText());
            }
        }
        return selectedVenueTypes;
    }

    @FXML
    private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }
}