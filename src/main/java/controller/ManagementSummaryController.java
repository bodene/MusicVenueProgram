package controller;

import dao.BookingDAO;
import dao.ClientDAO;
import model.Booking;
import model.BookingStatus;
import model.Client;
import service.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.Map;


/**
 * Controller class for managing the summary view in the management dashboard.
 * <p>
 * This class is responsible for displaying various management summary statistics including:
 * <ul>
 *   <li>A pie chart representing venue utilisation.</li>
 *   <li>A bar chart showing income and commission data per event.</li>
 *   <li>Tables listing details of confirmed bookings and client commission summaries.</li>
 * </ul>
 * It fetches data using DAO classes and uses JavaFX charts and tables to present the data.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class ManagementSummaryController {

    /* CHART COMPONENTS */
    @FXML private PieChart venueUtilisationChart;
    @FXML private BarChart<Number, String> incomeCommissionChart;
    @FXML private CategoryAxis yAxis;
    @FXML private NumberAxis xAxis;

    /* BOOKING TABLE COMPONENTS */
    @FXML private TableView<Booking> managementEventCommissionTable;
    @FXML private TableColumn<Booking, Integer> bookingNoColumn;
    @FXML private TableColumn<Booking, String> eventNameColumn;
    @FXML private TableColumn<Booking, String> venueNameColumn;
    @FXML private TableColumn<Booking, String> bookingCommissionColumn;
    @FXML private TableColumn<Booking, String> bookedByUserColumn;

    /* CLIENT TABLE COMPONENT */
    @FXML private TableView<Client> clientCommissionTable;
    @FXML private TableColumn<Client, Integer> clientIdColumn;
    @FXML private TableColumn<Client, String> clientNameColumn;
    @FXML private TableColumn<Client, Integer> noOfJobsColumn;
    @FXML private TableColumn<Client, String> totalCommissionColumn;
    @FXML private TableColumn<Client, String> totalClientCostColumn;


    /**
     * Initialises the management summary view.
     * <p>
     * This method is automatically called after the FXML file is loaded. It initialises the pie chart,
     * bar chart, and tables by calling dedicated setup methods.
     * </p>
     */
    @FXML
    private void initialize() {
        setupPieChart();
        setupBarChart();
        setupTables();
    }

    /**
     * Sets up the pie chart for venue utilisation.
     * <p>
     * This method retrieves venue utilisation data from {@link BookingDAO} and populates the
     * {@code venueUtilisationChart} with data entries.
     * </p>
     */
    private void setupPieChart() {

        // Retrieve venue utilisation data where the key is the venue name and the value is the usage count.
        Map<String, Integer> venueUtilisationData = BookingDAO.getVenueUtilisation();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // Convert each entry of the map into a PieChart.Data object.
        for (Map.Entry<String, Integer> entry : venueUtilisationData.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Set the data for the pie chart.
        venueUtilisationChart.setData(pieChartData);
    }

    /**
     * Sets up the bar chart for income and commission.
     * <p>
     * This method configures the chart axes, creates two data series (one for income and one for commission),
     * retrieves confirmed bookings from all clients, and adds data points to the series.
     * </p>
     */
    private void setupBarChart() {

        // Configure the X-Axis and Y-Axis labels.
        xAxis.setLabel("Amount ($)");
        yAxis.setLabel("Event");

        // Create two data series for income and commission.
        XYChart.Series<Number, String> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income $");

        XYChart.Series<Number, String> commissionSeries = new XYChart.Series<>();
        commissionSeries.setName("Commission $");

        // Retrieve all client summaries.
        List<Client> allClientSummaries = ClientDAO.getAllClientSummaries();

        // Extract all confirmed bookings from all clients.
        List<Booking> bookings = allClientSummaries.stream()
                .flatMap(client -> client.getBookings().stream())
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)  // Only confirmed bookings.
                .toList();

        // Process each booking to extract chart data.
        for (Booking booking : bookings) {
            // Get event name and format it to ensure it doesn't exceed a certain length.
            String eventName = booking.getEvent().getEventName();
            if (eventName.length() > 20) {
                // Break the event name into two lines if it's too long.
                int splitIndex = eventName.lastIndexOf(" ", 20);
                if (splitIndex > 0) {
                    eventName = eventName.substring(0, splitIndex) + "\n" + eventName.substring(splitIndex + 1);
                }
            }
            // Add data points for income and commission.
            incomeSeries.getData().add(new XYChart.Data<>(booking.getBookingHirePrice(), eventName));
            commissionSeries.getData().add(new XYChart.Data<>(booking.getBookingEventCommission(), eventName));
        }
        // Hide the legend for the bar chart.
        incomeCommissionChart.setLegendVisible(false);

        // Add both series to the bar chart.
        incomeCommissionChart.getData().addAll(incomeSeries, commissionSeries);
    }

    /**
     * Sets up the tables for displaying booking and client commission details.
     * <p>
     * This method configures the columns of the booking and client tables and populates them with data
     * retrieved from the DAO classes.
     * </p>
     */
    private void setupTables() {
        // Retrieve client summaries from the database.
        List<Client> clientList = ClientDAO.getAllClientSummaries();

        // Extract confirmed bookings from the client list using flatMap.
        List<Booking> confirmedBookings = clientList.stream()
                .flatMap(client -> client.getBookings().stream())
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .toList();

        // Configure the booking table columns.
        bookingNoColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        eventNameColumn.setCellValueFactory(cellData -> cellData.getValue().getEvent().eventNameProperty());
        venueNameColumn.setCellValueFactory(cellData -> cellData.getValue().getVenue().venueNameProperty());
        bookingCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingEventCommissionProperty());
        bookedByUserColumn.setCellValueFactory(cellData -> cellData.getValue().getBookedByProperty());

        // Populate the booking table with confirmed bookings.
        ObservableList<Booking> bookingObservableList = FXCollections.observableArrayList(confirmedBookings);
        managementEventCommissionTable.setItems(bookingObservableList);

        // Configure the client table columns.
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        noOfJobsColumn.setCellValueFactory(cellData -> cellData.getValue().confirmedJobCountProperty().asObject());
        totalCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalCommissionProperty());
        totalClientCostColumn.setCellValueFactory(cellData -> cellData.getValue().getClientBookingTotalProperty());

        // Populate the client table with client summaries.
        ObservableList<Client> observableClientList = FXCollections.observableArrayList(clientList);
        clientCommissionTable.setItems(observableClientList);
    }

    /**
     * Navigates to the settings view.
     * <p>
     * This method is invoked when the user chooses to access the management settings.
     * It switches the scene to the manager view.
     * </p>
     */
    @FXML public void goToSettings() {
        SceneManager.switchScene("manager-view.fxml");
    }

    /**
     * Logs out the current user and navigates back to the main view.
     * <p>
     * This method switches the scene to "main-view.fxml" to log the user out.
     * </p>
     */
    @FXML private void logout() {
        SceneManager.switchScene("main-view.fxml");
    }
}