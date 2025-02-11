package controller;
//DONE
import java.util.*;
import java.sql.SQLException;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Venue;
import model.VenueType;
import service.SceneManager;
import service.SessionManager;
import service.VenueService;
import util.AlertUtils;


public class VenuesController {

    @FXML private Button searchVenuesButton;
    @FXML private RadioButton indoorVenueRadio;
    @FXML private RadioButton outdoorVenueRadio;
    @FXML private RadioButton convertibleVenueRadio;
    private ToggleGroup categoryGroup;

    @FXML private TextField searchVenueNameField;
    @FXML private TableView<Venue> searchVenueTable;
    @FXML private TableColumn<Venue, Integer> venueIdColumn;
    @FXML private TableColumn<Venue, String> venueNameColumn;
    @FXML private TableColumn<Venue, String> venueCapacityColumn;
    @FXML private TableColumn<Venue, String> venueTypesColumn;
    @FXML private TableColumn<Venue, String> venueCategoryColumn;
    @FXML private TableColumn<Venue, String> pricePerHourColumn;
    private ObservableList<Venue> venueList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        Platform.runLater(this::loadData);

        // CREATE TOGGLE-GROUP
        categoryGroup = new ToggleGroup();
        indoorVenueRadio.setToggleGroup(categoryGroup);
        outdoorVenueRadio.setToggleGroup(categoryGroup);
        convertibleVenueRadio.setToggleGroup(categoryGroup);

        // ALLOW DE-SELECTION OF TOGGLES
        categoryGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                categoryGroup.getSelectedToggle().setSelected(false);
            }
        });

        // SEARCH VENUES BUTTON
        searchVenuesButton.setOnAction(event -> {
            try {
                searchVenues();
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", "Failed to search venues.", Alert.AlertType.ERROR);
            }
        });
    }

    // SET UP VENUE TABLE
    private void setupTableColumns() {
        venueIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getVenueId()));
        venueNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
        venueCapacityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedCapacity()));
        venueCategoryColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCategory()).asString());
        pricePerHourColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedPrice()));

        // FORMAT VENUE TYPES (REMOVE BRACKETS)
        venueTypesColumn.setCellValueFactory(cellData -> {
            List<VenueType> venueTypes = cellData.getValue().getVenueTypes();
            String formattedTypes = venueTypes.stream()
                    .map(venueType -> venueType.getVenueType().trim())
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(formattedTypes);
        });
    }

    // LOAD TABLE DATA
    private void loadData() {

        searchVenueTable.setItems(VenueService.getAllVenues());
    }

    // SEARCH VENUES FORM
    @FXML
    private void searchVenues() throws SQLException {
        String searchText = searchVenueNameField.getText().trim();
        String selectedCategory = categoryGroup.getSelectedToggle() != null ?
                ((RadioButton) categoryGroup.getSelectedToggle()).getText().toUpperCase() : null;

        searchVenueTable.setItems(VenueService.searchVenues(searchText, selectedCategory));
    }

    // DELETE VENUE
    @FXML
    private void deleteVenue() {
        Venue selectedVenue = searchVenueTable.getSelectionModel().getSelectedItem();

        if (selectedVenue == null) {
            AlertUtils.showAlert("No Selection", "Select a venue to delete", Alert.AlertType.WARNING);
            return;
        }

        // CONFIRM DELETION
        if (AlertUtils.showConfirmation("Confirm Deletion", "Are you sure you want to delete this venue?\nVenue: " + selectedVenue.getName())) {
            if (VenueService.deleteVenue(selectedVenue.getVenueId())) {
                AlertUtils.showAlert("Success", "Venue deleted successfully", Alert.AlertType.INFORMATION);
                searchVenueTable.setItems(VenueService.getAllVenues());
            } else {
                AlertUtils.showAlert("Error", "Failed to delete the venue", Alert.AlertType.ERROR);
            }
        }
    }

    // GO TO SETTINGS (MANAGER OR STAFF)
    @FXML
    private void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {SceneManager.switchScene("admin-view.fxml");}
    }

    @FXML private void addVenue() {SceneManager.switchScene("add-venue-view.fxml");}
    @FXML private void returnToDashboard() {SceneManager.switchScene("dashboard.fxml");}
    @FXML private void logout() { SceneManager.switchScene("main-view.fxml"); }
}