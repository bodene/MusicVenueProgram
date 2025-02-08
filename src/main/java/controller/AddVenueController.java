package controller;
//DONE
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.SceneManager;
import service.VenueService;
import util.AlertUtils;
import util.InputValidationUtils;

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

    // ASSIGN A TOGGLE-GROUP TO RADIOBUTTONS
    private void initialiseCategoryGroup() {
        categoryGroup = new ToggleGroup();
        indoorCategory.setToggleGroup(categoryGroup);
        outdoorCategory.setToggleGroup(categoryGroup);
        convertibleCategory.setToggleGroup(categoryGroup);

        // INCREASE FONT SIZE FOR RADIO BUTTONS
        indoorCategory.setStyle("-fx-font-size: 16px;");
        outdoorCategory.setStyle("-fx-font-size: 16px;");
        convertibleCategory.setStyle("-fx-font-size: 16px;");
    }

    // POPULATE THE VBOX WITH CHECKBOXES
    private void initialiseEventTypes() {
        // CLEAR PREVIOUS CHECKBOXES
        venueTypeColumn1.getChildren().clear();
        venueTypeColumn2.getChildren().clear();
        eventTypeCheckBoxes.clear();

        for (int i = 0; i < eventTypes.length; i++) {
            CheckBox checkBox = new CheckBox(eventTypes[i]);
            checkBox.setStyle("-fx-font-size: 16px;");
            eventTypeCheckBoxes.add(checkBox);

            // DISTRIBUTE CHECKBOXES INTO TWO COLUMNS
            if (i % 2 == 0) {
                venueTypeColumn1.getChildren().add(checkBox);
            } else {
                venueTypeColumn2.getChildren().add(checkBox);
            }
        }
    }

    // ADD NEW VENUE
    @FXML
    private void addVenue() {
        // GET VENUE NAME
        String venueName = venueNameField.getText().trim();
        if (venueName.isEmpty()) {
            AlertUtils.showAlert("Error", "Venue can not be empty", Alert.AlertType.ERROR);
            return;
        }
        // GET VENUE CAPACITY AND VALIDATE
        int venueCapacity = InputValidationUtils.validateInteger(venueCapacityField.getText().trim(), "Invalid Venue Capacity");
        if (venueCapacity == -1) return;

        // GET PRICE PER HOUR
        double pricePerHour = InputValidationUtils.validateDouble(pricePerHourField.getText().trim(), "Invalid Price");
        if (pricePerHour == -1) return;

        // GET SELECTED CATEGORY
        RadioButton selectedCategory = (RadioButton) categoryGroup.getSelectedToggle();
        if (selectedCategory == null) {
            AlertUtils.showAlert("Error", "Please select a venue category!", Alert.AlertType.ERROR);
            return;
        }

        // RETRIEVE SELECTED CHECKBOXES FROM BOTH COLUMNS
        List<String> selectedVenueTypes = getSelectedVenueTypes();
        if (selectedVenueTypes.isEmpty()) {
            AlertUtils.showAlert("Error", "Select at least one venue type!", Alert.AlertType.ERROR);
            return;
        }

        boolean success = VenueService.addVenue(venueName, selectedCategory.getText().toUpperCase(), venueCapacity, pricePerHour, selectedVenueTypes);

        if (success) {
            AlertUtils.showAlert("Success", "Venue added successfully!", Alert.AlertType.INFORMATION);
            SceneManager.switchScene("view-venue-details.fxml");
        } else {
            AlertUtils.showAlert("Error", "Failed to add venue. Please try again.", Alert.AlertType.ERROR);
        }
    }

    // HELPER METHOD - GET SELECTED VENUE TYPES
    private List<String> getSelectedVenueTypes() {
        List<String> selectedVenueTypes = new ArrayList<>();
        for (CheckBox checkBox : eventTypeCheckBoxes) {
            if (checkBox.isSelected()) {
                selectedVenueTypes.add(checkBox.getText());
            }
        }
        return selectedVenueTypes;
    }

    @FXML private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }
}


//    // Helper Method - validate integer input
//    private int validateIntegerInput(String input, String errorMessage) {
//        try {
//            int value = Integer.parseInt(input);
//            if (value <= 0) throw new NumberFormatException();
//            return value;
//        } catch (NumberFormatException e) {
//            AlertUtils.showAlert("Error", errorMessage, Alert.AlertType.ERROR);
//            return -1;
//        }
//    }

//    // Helper Method - validate double input
//    private double validateDoubleInput(String input, String errorMessage) {
//        try {
//            double value = Double.parseDouble(input);
//            if (value <= 0.0) throw new NumberFormatException();
//            return value;
//        } catch (NumberFormatException e) {
//            AlertUtils.showAlert("Error", errorMessage, Alert.AlertType.ERROR);
//            return -1;
//        }
//    }