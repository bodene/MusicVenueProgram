package controller;

import dao.VenueDAO;
import model.Venue;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddVenueController implements Initializable {

    @FXML
    private TextField venueNameField, venueCapacityField, pricePerHourField;

    @FXML
    private HBox venueTypeContainer;

    @FXML
    private VBox venueTypeColumn1, venueTypeColumn2;

    private final String[] eventTypes = {
            "Gig", "Disco", "Live Concert", "Festival", "Large Live Concert"
    };

    @FXML
    private ToggleGroup categoryGroup;

    @FXML
    private RadioButton indoorCategory, outdoorCategory, convertibleCategory;

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

        for (int i = 0; i < eventTypes.length; i++) {
            CheckBox checkBox = new CheckBox(eventTypes[i]);

            // Increase font size
            checkBox.setStyle("-fx-font-size: 16px;");

            // Distribute checkboxes into two columns
            if (i % 2 == 0) {
                venueTypeColumn1.getChildren().add(checkBox);
            } else {
                venueTypeColumn2.getChildren().add(checkBox);
            }
        }
    }


    @FXML
    private void addVenue() {
        // Get the venue name
        String venueName = venueNameField.getText().trim();
        if (venueName.isEmpty()) {
            showAlert("Error", "Venue can not be empty");
            return;
        }
        // Get Venue capacity
        int venueCapacity = 0;
        try {
            venueCapacity = Integer.parseInt(venueCapacityField.getText().trim());
            if (venueCapacity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Venue Capacity, Must be a positive number.");
            return;
        }
        // Get price per hour
        double pricePerHour = 0.0;
        try {
            pricePerHour = Double.parseDouble(pricePerHourField.getText().trim());
            if (pricePerHour <= 0.0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid price, Must be a positive number.");
            return;
        }
        // Get selected category
        RadioButton selectedCategory = (RadioButton) categoryGroup.getSelectedToggle();
        if (selectedCategory == null) {
            showAlert("Error", "Please select a venue category!");
            return;
        }
        String category = selectedCategory.getText().toUpperCase();

        // Retrieve selected checkboxes from both columns
        List<String> selectedEventTypes = new ArrayList<>();

        for (Node node : venueTypeColumn1.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                selectedEventTypes.add(checkBox.getText());
            }
        }
        for (Node node : venueTypeColumn2.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                selectedEventTypes.add(checkBox.getText());
            }
        }
        if (selectedEventTypes.isEmpty()) {
            showAlert("Error", "Select at least one venue type!");
            return;
        }
        // ✅ Create Venue object
        Venue venue = new Venue(venueName, category, venueCapacity, pricePerHour);

        // ✅ Save venue to database via VenueDAO
        boolean success = VenueDAO.addVenue(venue, selectedEventTypes);

        if (success) {
            showAlert("Success", "Venue added successfully!");
            SceneManager.switchScene("view-venue-details.fxml");
        } else {
            showAlert("Error", "Failed to add venue. Please try again.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        venueNameField.clear();
        venueCapacityField.clear();
        pricePerHourField.clear();
        categoryGroup.selectToggle(null); // Deselect category
        venueTypeColumn1.getChildren().forEach(node -> {
            if (node instanceof CheckBox checkBox) checkBox.setSelected(false);
        });
        venueTypeColumn2.getChildren().forEach(node -> {
            if (node instanceof CheckBox checkBox) checkBox.setSelected(false);
        });
    }

    @FXML
    private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }

}