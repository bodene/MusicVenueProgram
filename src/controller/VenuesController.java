package controller;

import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VenuesController {

    @FXML private Button logoutButton;
    @FXML private Button adminButton;
    @FXML private Button addVenueButton;
    @FXML private Button deleteVenueButton;
    @FXML private Button returnToDashboardButton;
    @FXML private Button searchVenuesButton;
    @FXML private TableView<Venue> venueTable;
    @FXML private TableColumn<Venue, Integer> venueIdColumn;
    @FXML private TableColumn<Venue, String> venueNameColumn;
    @FXML private TableColumn<Venue, Integer> capacityColumn;
    @FXML private TableColumn<Venue, String> suitableForColumn;
    @FXML private TableColumn<Venue, String> categoryColumn;
    @FXML private TableColumn<Venue, Double> pricePerHourColumn;

    private ObservableList<Venue> venueData = FXCollections.observableArrayList();

    // Show Request Details
    @FXML
    public void initialize() {

            if (venueTable == null) {
                System.out.println("venueTable is NULL! Check your FXML file.");
                return;
            }
        venueIdColumn.setCellValueFactory(new PropertyValueFactory<>("venueId"));
        venueNameColumn.setCellValueFactory(new PropertyValueFactory<>("venueName"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        suitableForColumn.setCellValueFactory(new PropertyValueFactory<>("suitableFor"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        pricePerHourColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerHour"));

        loadSampleData();
    }

    // Load Sample Data
    private void loadSampleData() {
        venueData.add(new Venue(1, "Grand Hall", 500, "Concerts", "Theater", 200.0));
        venueData.add(new Venue(2, "Open Grounds", 1000, "Festivals", "Outdoor", 150.0));
        venueTable.setItems(venueData);
    }

    // Admin Settings
    @FXML
    private void admin() {

        SceneManager.switchScene("admin-view.fxml");
    }

    @FXML
    private void logout() {

        SceneManager.switchScene("main-view.fxml");
    }

    // Add Venue
    @FXML
    private void addVenue() {
        System.out.println("Add Venue Clicked");
        // TODO Add Venue
    }

    // Delete Venue
    @FXML
    private void deleteVenue() {
        System.out.println("Delete Venue Clicked");
        // TODO Delete Venue
    }

    @FXML
    private void returnToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }

    @FXML
    private void searchVenues() {
        System.out.println("Searching venues...");
        // TODO Implement Search Venue
    }

    public static class Venue {
        private final int venueId;
        private final String venueName;
        private final int capacity;
        private final String suitableFor;
        private final String category;
        private final double pricePerHour;

        public Venue(int venueId, String venueName, int capacity, String suitableFor, String category, double pricePerHour) {
            this.venueId = venueId;
            this.venueName = venueName;
            this.capacity = capacity;
            this.suitableFor = suitableFor;
            this.category = category;
            this.pricePerHour = pricePerHour;
        }
    }





}
