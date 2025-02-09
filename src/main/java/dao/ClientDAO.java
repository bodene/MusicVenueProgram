package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    private static List<Client> clients = new ArrayList<>();

    public static Client findOrCreateClient(String clientName) {
        for (Client client : clients) {
            if (client.getClientName().equalsIgnoreCase(clientName)) {
                return client;
            }
        }

        try (Connection connection = DatabaseHandler.getConnection()) {
            int newClientId = findOrCreateClientId(clientName, connection);
            Client newClient = new Client(newClientId, clientName, "unknown@contact.com");
            clients.add(newClient);
            return newClient;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // For database importing
    public static int findOrCreateClientId(String clientName, Connection connection) throws SQLException {
        String findSQL = "SELECT client_id FROM clients WHERE client_name = ?";
        String insertSQL = "INSERT INTO clients (client_name) VALUES (?)";

        try (PreparedStatement findStmt = connection.prepareStatement(findSQL)) {
            findStmt.setString(1, clientName);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("client_id"); // Return existing client ID
            }
        }

        try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, clientName);
            insertStmt.executeUpdate();
            ResultSet rs = insertStmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Could not insert or find client: " + clientName);
    }

    public static Client getClientById(int clientId) throws SQLException {
        String sql = "SELECT * FROM clients WHERE client_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Client(
                        rs.getInt("client_id"),
                        rs.getString("client_name"),
                        rs.getString("contact_info")
                );
            }
        }
        return null;
    }

    // GET ALL CLIENT SUMMARIES FOR CLIENT COMMISSIONS
    public static List<Client> getAllClientSummaries() {
        String sql = """
                SELECT c.client_id, c.client_name, c.total_jobs, c.total_commission, c.total_amount_spent 
                FROM clients c
            """;

        List<Client> clientList = new ArrayList<>();

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int clientId = rs.getInt("client_id");
                String clientName = rs.getString("client_name");
                int totalJobs = rs.getInt("total_jobs");
                double totalCommission = rs.getDouble("total_commission");
                double totalAmountSpent = rs.getDouble("total_amount_spent");

                Client client = new Client(clientId, clientName, totalJobs, totalCommission, totalAmountSpent);
                clientList.add(client);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientList;
    }

    public static ObservableList<Client> getAllClients() {
        ObservableList<Client> clientList = FXCollections.observableArrayList();
        String sql = "SELECT client_id, client_name, contact_info FROM clients";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int clientId = rs.getInt("client_id");
                String clientName = rs.getString("client_name");
                String contactInfo = rs.getString("contact_info");

                Client client = new Client(clientId, clientName, contactInfo);
                clientList.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching clients: " + e.getMessage());
        }

        return clientList;
    }
}