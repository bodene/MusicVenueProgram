package controller;

import java.util.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import model.Venue;
import model.VenueType;
import service.SceneManager;
import service.SessionManager;
import service.VenueService;
import util.AlertUtils;


public class VenuesController {

    @FXML private Button logoutButton, settingsButton, addVenueButton, deleteVenueButton, returnToDashboardButton, searchVenuesButton;
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
            if (newVal == null) {
                categoryGroup.getSelectedToggle().setSelected(false);
            }
        });

        searchVenuesButton.setOnAction(event -> {
            try {
                searchVenues();
            } catch (SQLException e) {
                e.printStackTrace();
                AlertUtils.showAlert("Error", "Failed to search venues.", Alert.AlertType.ERROR);
            }
        });
    }

    // Set Up Venue Table Columns
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
        searchVenueTable.setItems(VenueService.getAllVenues());
    }

    // Search Venues form
    @FXML
    private void searchVenues() throws SQLException {
        String searchText = searchVenueNameField.getText().trim();
        List<String> categories = new ArrayList<>();

        // Determine category selected. If indoors include convertible, if outdoors include convertible
        if (categoryGroup.getSelectedToggle() != null) {
            String selectedCategory = ((RadioButton) categoryGroup.getSelectedToggle()).getText().toUpperCase();
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
        searchVenueTable.setItems(VenueService.searchVenues(searchText, categories));
    }

    @FXML
    private void addVenue() {SceneManager.switchScene("add-venue-view.fxml");}

    // Delete Selected Venue
    @FXML
    private void deleteVenue() {
        Venue selectedVenue = searchVenueTable.getSelectionModel().getSelectedItem();
        if (selectedVenue == null) {
            AlertUtils.showAlert("No Selection", "Select a venue to delete", Alert.AlertType.WARNING);
            return;
        }
        // Confirm deletion
        if (AlertUtils.showConfirmation("Confirm Deletion", "Are you sure you want to delete this venue?\nVenue: " + selectedVenue.getName())) {
            if (VenueService.deleteVenue(selectedVenue.getVenueId())) {
                venueList.remove(selectedVenue);
                AlertUtils.showAlert("Success", "Venue deleted successfully", Alert.AlertType.INFORMATION);
            } else {
                AlertUtils.showAlert("Error", "Failed to delete the venue", Alert.AlertType.ERROR);
            }
        }
    }

    // go to either manager or admin settings page
    @FXML
    private void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {SceneManager.switchScene("admin-view.fxml");}
    }

    @FXML private void returnToDashboard() {SceneManager.switchScene("dashboard.fxml");}
    @FXML private void logout() { SceneManager.switchScene("main-view.fxml"); }
}
