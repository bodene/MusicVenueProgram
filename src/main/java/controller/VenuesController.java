package controller;

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


/**
 * Controller class for managing venues.
 * <p>
 * This class handles the display and management of venues. It provides functionalities to:
 * <ul>
 *   <li>Display all venues in a table view.</li>
 *   <li>Search venues by name and/or category.</li>
 *   <li>Delete a selected venue.</li>
 *   <li>Navigate to other views such as add venue, settings, dashboard, and logout.</li>
 * </ul>
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class VenuesController {

    /* Search Features */
    @FXML private Button searchVenuesButton;
    @FXML private RadioButton indoorVenueRadio;
    @FXML private RadioButton outdoorVenueRadio;
    @FXML private RadioButton convertibleVenueRadio;
    private ToggleGroup categoryGroup;
    @FXML private TextField searchVenueNameField;

    /* SEARCH VENUES RESULTS FIELDS */
    @FXML private TableView<Venue> searchVenueTable;
    @FXML private TableColumn<Venue, Integer> venueIdColumn;
    @FXML private TableColumn<Venue, String> venueNameColumn;
    @FXML private TableColumn<Venue, String> venueCapacityColumn;
    @FXML private TableColumn<Venue, String> venueTypesColumn;
    @FXML private TableColumn<Venue, String> venueCategoryColumn;
    @FXML private TableColumn<Venue, String> pricePerHourColumn;
    private ObservableList<Venue> venueList = FXCollections.observableArrayList();


    /**
     * Initialises the VenuesController after the FXML elements have been loaded.
     * <p>
     * This method sets up the table columns, loads the venue data, creates the toggle group for
     * category filtering, and sets up the action for the search button.
     * </p>
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        // Load data on the JavaFX Application Thread after initialisation.
        Platform.runLater(this::loadData);

        // Create and assign a ToggleGroup to the category RadioButtons.
        categoryGroup = new ToggleGroup();
        indoorVenueRadio.setToggleGroup(categoryGroup);
        outdoorVenueRadio.setToggleGroup(categoryGroup);
        convertibleVenueRadio.setToggleGroup(categoryGroup);

        // Allow de-selection of toggles by adding a listener.
        categoryGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                // This block prevents NPE by ensuring no toggle is selected if newVal is null.
                if (oldVal != null) {
                    ((RadioButton) oldVal).setSelected(false);
                }
            }
        });

        // Set up the search venues button to call the searchVenues() method.
        searchVenuesButton.setOnAction(event -> {
            try {
                searchVenues();
            } catch (SQLException e) {
                AlertUtils.showAlert("Error", "Failed to search venues.", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Configures the TableView columns to display venue properties.
     * <p>
     * This method maps the venue ID, name, capacity, category, price per hour, and venue types to their corresponding table columns.
     * The venue types are formatted to remove brackets and display as a comma-separated list.
     * </p>
     */
    private void setupTableColumns() {
        venueIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getVenueId()));
        venueNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
        venueCapacityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedCapacity()));
        venueCategoryColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCategory()).asString());
        pricePerHourColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedPrice()));

        // Format venue types by joining their names with a comma.
        venueTypesColumn.setCellValueFactory(cellData -> {
            List<VenueType> venueTypes = cellData.getValue().getVenueTypes();
            String formattedTypes = venueTypes.stream()
                    .map(venueType -> venueType.getVenueType().trim())
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(formattedTypes);
        });
    }

    /**
     * Loads venue data into the TableView.
     * <p>
     * This method retrieves all venues via the {@link VenueService} and sets the resulting list into the table.
     * </p>
     */
    private void loadData() {
        searchVenueTable.setItems(VenueService.getAllVenues());
    }

    /**
     * Searches for venues based on the entered name and selected category.
     * <p>
     * This method retrieves the search text from the search field and the selected category (if any) from the toggle group.
     * It then uses {@link VenueService#searchVenues(String, String)} to retrieve matching venues and updates the table view.
     * </p>
     *
     * @throws SQLException if an error occurs during the search operation
     */
    @FXML
    private void searchVenues() throws SQLException {
        String searchText = searchVenueNameField.getText().trim();
        String selectedCategory = categoryGroup.getSelectedToggle() != null
                ? ((RadioButton) categoryGroup.getSelectedToggle()).getText().toUpperCase() : null;
        searchVenueTable.setItems(VenueService.searchVenues(searchText, selectedCategory));
    }

    /**
     * Deletes the selected venue.
     * <p>
     * This method retrieves the selected venue from the table. If a venue is selected, it confirms the deletion with the user.
     * If confirmed, the venue is deleted via the {@link VenueService}. After deletion, the table is refreshed.
     * </p>
     */
    @FXML
    private void deleteVenue() {
        Venue selectedVenue = searchVenueTable.getSelectionModel().getSelectedItem();

        if (selectedVenue == null) {
            AlertUtils.showAlert("No Selection", "Select a venue to delete", Alert.AlertType.WARNING);
            return;
        }

        // Confirm deletion with the user.
        if (AlertUtils.showConfirmation("Confirm Deletion", "Are you sure you want to delete this venue?\nVenue: " + selectedVenue.getName())) {
            if (VenueService.deleteVenue(selectedVenue.getVenueId())) {
                AlertUtils.showAlert("Success", "Venue deleted successfully", Alert.AlertType.INFORMATION);

                // Refresh the table data.
                searchVenueTable.setItems(VenueService.getAllVenues());
            } else {
                AlertUtils.showAlert("Error", "Failed to delete the venue", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Navigates to the settings view.
     * <p>
     * Depending on whether the current user is a manager or not, this method switches the scene to the corresponding settings view.
     * </p>
     */
    @FXML
    private void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {SceneManager.switchScene("admin-view.fxml");}
    }

    /**
     * Navigates to the add venue view.
     */
    @FXML private void addVenue() {
        SceneManager.switchScene("add-venue-view.fxml");
    }

    /**
     * Returns to the dashboard view.
     */
    @FXML private void returnToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }

    /**
     * Logs out the current user.
     */
    @FXML private void logout() {
        SceneManager.switchScene("main-view.fxml");
    }
}