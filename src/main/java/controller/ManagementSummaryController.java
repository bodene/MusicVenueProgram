package controller;

import dao.BookingDAO;
import dao.ClientDAO;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Map;


public class ManagementSummaryController {

    @FXML private PieChart venueUtilisationChart;
    @FXML private BarChart<String, Number> incomeCommissionChart;

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

    @FXML private Button logoutButton;
    @FXML private Button settingsButton;

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
//        Map<String, Double> venueIncomeData = BookingDAO.getVenueIncome();
//        Map<String, Double> venueCommissionData = BookingDAO.getVenueCommission();
//
//        CategoryAxis xAxis = (CategoryAxis) incomeCommissionChart.getXAxis();
//        NumberAxis yAxis = (NumberAxis) incomeCommissionChart.getYAxis();
//        xAxis.setLabel("Venue");
//        yAxis.setLabel("Amount ($)");
//
//        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
//        incomeSeries.setName("Income");
//
//        XYChart.Series<String, Number> commissionSeries = new XYChart.Series<>();
//        commissionSeries.setName("Commission");
//
//        for (String venue : venueIncomeData.keySet()) {
//            incomeSeries.getData().add(new XYChart.Data<>(venue, venueIncomeData.get(venue)));
//            commissionSeries.getData().add(new XYChart.Data<>(venue, venueCommissionData.getOrDefault(venue, 0.0)));
//        }
//
//        incomeCommissionChart.getData().addAll(incomeSeries, commissionSeries);
    }
    private void setupTables() {
            // BOOKING ORDER SUMMARY TABLE SETUP
            bookingNoColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
            eventNameColumn.setCellValueFactory(cellData -> cellData.getValue().getEvent().eventNameProperty());
            venueNameColumn.setCellValueFactory(cellData -> cellData.getValue().getVenue().venueNameProperty());
            bookingCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getBookingEventCommissionProperty());
            bookedByUserColumn.setCellValueFactory(cellData -> cellData.getValue().getBookedByProperty());

            // Get all bookings and filter to keep only confirmed ones
            List<Booking> bookingList = BookingDAO.getBookingOrderSummary();
            List<Booking> confirmedBookings = bookingList.stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)  // Replace BookingStatus.CONFIRMED with your actual status check logic
                    .toList();

            ObservableList<Booking> bookingObservableList = FXCollections.observableArrayList(confirmedBookings);
            managementEventCommissionTable.setItems(bookingObservableList);

            // CLIENT COMMISSION TABLE SETUP
            clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
            clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
            noOfJobsColumn.setCellValueFactory(cellData -> cellData.getValue().confirmedJobCountProperty().asObject());
            totalCommissionColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalCommissionProperty());
            totalClientCostColumn.setCellValueFactory(cellData -> cellData.getValue().getClientBookingTotalProperty());

            List<Client> clientList = ClientDAO.getAllClientSummaries();
            ObservableList<Client> observableClientList = FXCollections.observableArrayList(clientList);
            clientCommissionTable.setItems(observableClientList);
        }

//    private ObservableList<Booking> getManagementEventCommissionData() {
//        return FXCollections.observableArrayList(BookingDAO.getBookingOrderSummary());
//    }
//
//    private ObservableList<Client> getClientCommissionData() {
//        return FXCollections.observableArrayList(ClientDAO.getAllClientSummaries());
//    }



    @FXML public void goToSettings() {SceneManager.switchScene("manager-view.fxml");}
    @FXML private void logout() {SceneManager.switchScene("main-view.fxml");}

}


