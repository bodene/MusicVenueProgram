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


public class ManagementSummaryController {

    @FXML private PieChart venueUtilisationChart;
    @FXML private BarChart<Number, String> incomeCommissionChart;
    @FXML private CategoryAxis yAxis;
    @FXML private NumberAxis xAxis;

    @FXML private TableView<Booking> managementEventCommissionTable;
    @FXML private TableColumn<Booking, Integer> bookingNoColumn;
    @FXML private TableColumn<Booking, String> eventNameColumn;
    @FXML private TableColumn<Booking, String> venueNameColumn;
    @FXML private TableColumn<Booking, String> bookingCommissionColumn;
    @FXML private TableColumn<Booking, String> bookedByUserColumn;

    @FXML private TableView<Client> clientCommissionTable;
    @FXML private TableColumn<Client, Integer> clientIdColumn;
    @FXML private TableColumn<Client, String> clientNameColumn;
    @FXML private TableColumn<Client, Integer> noOfJobsColumn;
    @FXML private TableColumn<Client, String> totalCommissionColumn;
    @FXML private TableColumn<Client, String> totalClientCostColumn;


    @FXML
    private void initialize() {

        setupPieChart();
        setupBarChart();
        setupTables();
    }

    // SET UP PIE CHART
    private void setupPieChart() {

        Map<String, Integer> venueUtilisationData = BookingDAO.getVenueUtilisation();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : venueUtilisationData.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        venueUtilisationChart.setData(pieChartData);
    }

    // SETUP BAR-CHART
    private void setupBarChart() {

        xAxis.setLabel("Amount ($)");
        yAxis.setLabel("Event");

        // CREATE TWO SERIES FOR INCOME & COMMISSION
        XYChart.Series<Number, String> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income $");

        XYChart.Series<Number, String> commissionSeries = new XYChart.Series<>();
        commissionSeries.setName("Commission $");

        // FETCH ALL CLIENT SUMMARIES
        List<Client> allClientSummaries = ClientDAO.getAllClientSummaries();

        // EXTRACT BOOKINGS FROM ALL CLIENTS
        List<Booking> bookings = allClientSummaries.stream()
                .flatMap(client -> client.getBookings().stream())
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)  // Only confirmed bookings
                .toList();

        for (Booking booking : bookings) {
            String eventName = booking.getEvent().getEventName();
            if (eventName.length() > 20) {
                int splitIndex = eventName.lastIndexOf(" ", 20);
                if (splitIndex > 0) {
                    eventName = eventName.substring(0, splitIndex) + "\n" + eventName.substring(splitIndex + 1);
                }
            }

            incomeSeries.getData().add(new XYChart.Data<>(booking.getBookingHirePrice(), eventName));
            commissionSeries.getData().add(new XYChart.Data<>(booking.getBookingEventCommission(), eventName));
        }

        incomeCommissionChart.setLegendVisible(false);
        incomeCommissionChart.getData().addAll(incomeSeries, commissionSeries);
    }

    // SET-UP TABLES
    private void setupTables() {

        List<Client> clientList = ClientDAO.getAllClientSummaries();

        // USE FLAT MAP TO EXTRACT BOOKINGS
        List<Booking> confirmedBookings = clientList.stream()
                .flatMap(client -> client.getBookings().stream())
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .toList();

        // BOOKING ORDER SUMMARY TABLE SETUP
        bookingNoColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        eventNameColumn.setCellValueFactory(cellData -> cellData.getValue().getEvent().eventNameProperty());
        venueNameColumn.setCellValueFactory(cellData -> cellData.getValue().getVenue().venueNameProperty());
        bookingCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingEventCommissionProperty());
        bookedByUserColumn.setCellValueFactory(cellData -> cellData.getValue().getBookedByProperty());

        ObservableList<Booking> bookingObservableList = FXCollections.observableArrayList(confirmedBookings);
        managementEventCommissionTable.setItems(bookingObservableList);

        // CLIENT COMMISSION TABLE SETUP
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        noOfJobsColumn.setCellValueFactory(cellData -> cellData.getValue().confirmedJobCountProperty().asObject());
        totalCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalCommissionProperty());
        totalClientCostColumn.setCellValueFactory(cellData -> cellData.getValue().getClientBookingTotalProperty());

        ObservableList<Client> observableClientList = FXCollections.observableArrayList(clientList);
        clientCommissionTable.setItems(observableClientList);
    }

    @FXML public void goToSettings() {SceneManager.switchScene("manager-view.fxml");}
    @FXML private void logout() {SceneManager.switchScene("main-view.fxml");}

}


