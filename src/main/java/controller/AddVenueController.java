package controller;

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
    private TextField venueName, capacity, pricePerHour;

    @FXML
    private HBox suitableForContainer;

    @FXML
    private VBox suitableForColumn1, suitableForColumn2;

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
        suitableForColumn1.getChildren().clear();
        suitableForColumn2.getChildren().clear();

        for (int i = 0; i < eventTypes.length; i++) {
            CheckBox checkBox = new CheckBox(eventTypes[i]);

            // Increase font size
            checkBox.setStyle("-fx-font-size: 16px;");

            // Distribute checkboxes into two columns
            if (i % 2 == 0) {
                suitableForColumn1.getChildren().add(checkBox);
            } else {
                suitableForColumn2.getChildren().add(checkBox);
            }
        }
    }


    @FXML
    private void addVenue() {
        // Get selected category
        RadioButton selectedCategory = (RadioButton) categoryGroup.getSelectedToggle();
        String category = (selectedCategory != null) ? selectedCategory.getText() : "None";

        // Retrieve selected checkboxes from both columns
        List<String> selectedEventTypes = new ArrayList<>();

        for (Node node : suitableForColumn1.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                selectedEventTypes.add(checkBox.getText());
            }
        }

        for (Node node : suitableForColumn2.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                selectedEventTypes.add(checkBox.getText());
            }
        }

        // Print venue details
        System.out.println("Adding venue: " + venueName.getText());
        System.out.println("Capacity: " + capacity.getText());
        System.out.println("Price per hour: " + pricePerHour.getText());
        System.out.println("Venue Category: " + category);
        System.out.println("Suitable for: " + selectedEventTypes);
    }

    @FXML
    private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }
}