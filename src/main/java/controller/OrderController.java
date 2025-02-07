package controller;

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


public class OrderController {

    @FXML
    private PieChart venueUtilisationChart;

    @FXML
    private BarChart<String, Number> incomeCommissionChart;

    @FXML
    private TableView<Order> eventCommissionTable;

    @FXML
    private TableColumn<Order, Integer> requestNoColumn;

    @FXML
    private TableColumn<Order, String> eventNameColumn;

    @FXML
    private TableColumn<Order, String> clientColumn;

    @FXML
    private TableColumn<Order, Double> commissionColumn;

    @FXML
    private TableView<Order.ClientCommission> clientCommissionTable;

    @FXML
    private TableColumn<Order.ClientCommission, Integer> clientIdColumn;

    @FXML
    private TableColumn<Order.ClientCommission, String> clientNameColumn;

    @FXML
    private TableColumn<Order.ClientCommission, Double> totalCommissionColumn;

    @FXML
    private Button logoutButton;

    @FXML
    private Button adminButton;

    @FXML
    private void initialize() {
        setupPieChart();
        setupBarChart();
        setupTables();
    }

    // Set up Pie Chart with dummy data
    private void setupPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Esplanade Hotel (The Espy)", 20),
                new PieChart.Data("Corner Hotel", 15),
                new PieChart.Data("170 Russell", 10),
                new PieChart.Data("The Forum", 18),
                new PieChart.Data("The Night Cat", 12),
                new PieChart.Data("The Tote", 8),
                new PieChart.Data("Rod Laver Arena", 9),
                new PieChart.Data("Docklands Stadium", 8)
        );
        venueUtilisationChart.setData(pieChartData);
    }

    // Setup Bar Chart with Dummy Data
    private void setupBarChart() {
        CategoryAxis xAxis = (CategoryAxis) incomeCommissionChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) incomeCommissionChart.getYAxis();
        xAxis.setLabel("Venue");
        yAxis.setLabel("Amount ($)");

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        incomeSeries.getData().add(new XYChart.Data<>("Esplanade Hotel", 5000));
        incomeSeries.getData().add(new XYChart.Data<>("Corner Hotel", 3500));
        incomeSeries.getData().add(new XYChart.Data<>("170 Russell", 4000));
        incomeSeries.getData().add(new XYChart.Data<>("The Forum", 7000));

        XYChart.Series<String, Number> commissionSeries = new XYChart.Series<>();
        commissionSeries.setName("Commission");
        commissionSeries.getData().add(new XYChart.Data<>("Esplanade Hotel", 1000));
        commissionSeries.getData().add(new XYChart.Data<>("Corner Hotel", 700));
        commissionSeries.getData().add(new XYChart.Data<>("170 Russell", 800));
        commissionSeries.getData().add(new XYChart.Data<>("The Forum", 1200));

        incomeCommissionChart.getData().addAll(incomeSeries, commissionSeries);
    }

    private void setupTables() {
        requestNoColumn.setCellValueFactory(new PropertyValueFactory<>("requestNo"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        commissionColumn.setCellValueFactory(new PropertyValueFactory<>("commission"));

        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        totalCommissionColumn.setCellValueFactory(new PropertyValueFactory<>("totalCommission"));

        eventCommissionTable.setItems(getEventData());
       // clientCommissionTable.setItems(getClientCommissionData());
    }

    private ObservableList<Order> getEventData() {
        return FXCollections.observableArrayList(
                new Order(101, "Rock Concert", "John Doe", 250.00),
                new Order(102, "Jazz Night", "Jane Smith", 180.00),
                new Order(103, "Corporate Meetup", "XYZ Corp", 400.00),
                new Order(104, "Wedding Event", "Mike Johnson", 320.00)
        );
    }

    private ObservableList<Order.ClientCommission> getClientCommissionData() {
        return FXCollections.observableArrayList(
                new Order.ClientCommission(201, "John Doe", 500.00),
                new Order.ClientCommission(202, "Jane Smith", 350.00),
                new Order.ClientCommission(203, "XYZ Corp", 600.00),
                new Order.ClientCommission(204, "Mike Johnson", 450.00)
        );
    }


    // Admin Settings
    @FXML
    private void admin() {

        SceneManager.switchScene("manager-view.fxml");
    }

    @FXML
    private void logout() {

        SceneManager.switchScene("main-view.fxml");
    }
}

// Order Class
class Order {
    private final Integer requestNo;
    private final String eventName;
    private final String client;
    private final Double commission;

    public Order(Integer requestNo, String eventName, String client, Double commission) {
        this.requestNo = requestNo;
        this.eventName = eventName;
        this.client = client;
        this.commission = commission;
    }

    public Integer getRequestNo() {
        return requestNo;
    }

    public String getEventName() {
        return eventName;
    }



    public Double getCommission() {
        return commission;
    }


// ClientCommission Class
static class ClientCommission {
    private final Integer clientId;
    private final String clientName;
    private final Double totalCommission;

    public ClientCommission(Integer clientId, String clientName, Double totalCommission) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.totalCommission = totalCommission;
    }





    public Double getTotalCommission() {
        return totalCommission;
    }
}
}


