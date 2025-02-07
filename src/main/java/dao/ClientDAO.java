package dao;

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
}