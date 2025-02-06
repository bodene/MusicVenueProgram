package controller;

import dao.VenueDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.sql.SQLException;
import java.text.NumberFormat;
import javafx.util.StringConverter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import service.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

import java.util.List;
import java.util.stream.Collectors;

public class VenuesController {

    @FXML private Button logoutButton;
    @FXML private Button settingsButton;
    @FXML private Button addVenueButton;
    @FXML private Button deleteVenueButton;
    @FXML private Button returnToDashboardButton;
    @FXML private Button searchVenuesButton;
    @FXML private RadioButton indoorVenueRadio, outdoorVenueRadio, convertibleVenueRadio;
    private ToggleGroup categoryGroup;
    @FXML private TextField searchVenueNameField;

    @FXML private TableView<Venue> searchVenueTable;
    @FXML private TableColumn<Venue, Integer> venueIdColumn;
    @FXML private TableColumn<Venue, String> venueNameColumn;
    @FXML private TableColumn<Venue, Integer> venueCapacityColumn;
    @FXML private TableColumn<Venue, String> venueTypesColumn;
    @FXML private TableColumn<Venue, String> venueCategoryColumn;
    @FXML private TableColumn<Venue, Double> pricePerHourColumn;
    private ObservableList<Venue> venueList = FXCollections.observableArrayList();

    // Initialise Search Venue Tables
    @FXML
    public void initialize() {
        setupTableColumns();
        Platform.runLater(this::loadData);

        // Create ToggleGroup for category selection
        categoryGroup = new ToggleGroup();
        indoorVenueRadio.setToggleGroup(categoryGroup);
        outdoorVenueRadio.setToggleGroup(categoryGroup);
        convertibleVenueRadio.setToggleGroup(categoryGroup);

        // Allow deselection (by setting all to false if clicked again)
        categoryGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) { // If the user clicks again on the selected button, deselect all
                categoryGroup.getSelectedToggle().setSelected(false);
            }
        });

        searchVenuesButton.setOnAction(event -> {
            try {
                searchVenues();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Set up columns to match Venue object properties.
     */
    private void setupTableColumns() {
        // Set up Venue Table
        venueIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getVenueId()));
        venueNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));

        // Adds a thousand separator to capacity
        venueCapacityColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCapacity()));
        venueCapacityColumn.setCellFactory(column -> new TextFieldTableCell<>(new StringConverter<Integer>() {
            private final NumberFormat format = NumberFormat.getInstance(Locale.US);
            @Override
            public String toString(Integer value) {
                return value == null ? "" : format.format(value);
            }
            @Override
            public Integer fromString(String string) {
                try {
                    return format.parse(string).intValue();
                } catch (Exception e) {
                    return 0;
                }
            }
        }));

        // Format Venue Types (Remove Brackets)
        venueTypesColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getVenueTypes()).asString());
        venueTypesColumn.setCellValueFactory(cellData -> {
            List<VenueType> venueTypes = cellData.getValue().getVenueTypes();
            String formattedTypes = venueTypes.stream()
                    .map(VenueType::getVenueType)
                    .collect(Collectors.joining(", ")); // Joins types with commas
            return new SimpleStringProperty(formattedTypes);
        });

        venueCategoryColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCategory()).asString());

       // Adds thousands separator and adds dollar sign
        pricePerHourColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getHirePricePerHour()));
        pricePerHourColumn.setCellFactory(column -> new TextFieldTableCell<>(new StringConverter<Double>() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            @Override
            public String toString(Double value) {
                return value == null ? "" : currencyFormat.format(value);
            }
            @Override
            public Double fromString(String string) {
                try {
                    return currencyFormat.parse(string).doubleValue();
                } catch (Exception e) {
                    return 0.0;
                }
            }
        }));
    }

    private void loadData() {
        System.out.println("🔄 Loading venues from database...");
        List<Venue> venueResults = VenueDAO.getAllVenues();

        if (venueResults == null || venueResults.isEmpty()) {
            System.out.println("❌ No venues found in the database!");
        } else {
            for (Venue v : venueResults) {
                System.out.println("✅ Loaded Venue: " + v.getName());
            }
        }
        venueList.setAll(venueResults);
        searchVenueTable.setItems(venueList);
    }

    @FXML
    private void searchVenues() throws SQLException {
        String searchText = searchVenueNameField.getText().trim();
        List<String> categories = new ArrayList<>();

        // Determine category selected
        if (categoryGroup.getSelectedToggle() != null) {
            RadioButton selectedButton = (RadioButton) categoryGroup.getSelectedToggle();
            String selectedCategory = selectedButton.getText().toUpperCase();

            // If indoors include convertible, if outdoors include convertible
            if ("INDOOR".equals(selectedCategory)) {
                categories.add("INDOOR");
                categories.add("CONVERTIBLE");
            } else if ("OUTDOOR".equals(selectedCategory)) {
                categories.add("OUTDOOR");
                categories.add("CONVERTIBLE");
            } else if ("CONVERTIBLE".equals(selectedCategory)) {
                categories.add("CONVERTIBLE");
            }
        }
        // Get Venues with search logic
        ObservableList<Venue> filteredVenues = VenueDAO.searchVenues(searchText, categories);
        searchVenueTable.setItems(filteredVenues);
    }

    // Add Venue
    @FXML
    private void addVenue() {
        SceneManager.switchScene("add-venue-view.fxml");
    }

    // Delete Venue
    @FXML
    private void deleteVenue() {
        // Get the selected venue from Table
        Venue selectedVenue = searchVenueTable.getSelectionModel().getSelectedItem();
        if (selectedVenue == null) {
            showAlert("No Selection", "Select a venue to delete");
            return;
        }
        // Confirm deletion
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete this venue?");
        confirmationAlert.setContentText("This action cannot be undone.\nVenue: " + selectedVenue.getName());

        if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Delete Venue
            boolean success = VenueDAO.deleteVenue(selectedVenue.getVenueId());

            if (success) {
                venueList.remove(selectedVenue);
                showAlert("Success", "Venue deleted successfully");
            } else {
                showAlert("Error", "Failed to delete the venue");
            }
        }
    }





    @FXML
    private void returnToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }

    // go to either manager or admin settings page
    @FXML
    private void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {
            SceneManager.switchScene("admin-view.fxml");
        }
    }

    @FXML
    private void logout() {
        //TODO implement logout
        SceneManager.switchScene("main-view.fxml");
    }
}
