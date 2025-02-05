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
                return client; // ✅ Found existing client
            }
        }
        // ✅ If client not found, create a new one and add to the list
        Client newClient = new Client(clientName, "unknown@contact.com");
        clients.add(newClient);
        return newClient;
    }

    // For database importing
    public static int findOrCreateClientId(String clientName, Connection connection) throws SQLException {
        String findSQL = "SELECT client_id FROM clients WHERE client_name = ?";
        String insertSQL = "INSERT INTO clients (client_name) VALUES (?)";

        try (PreparedStatement findStmt = connection.prepareStatement(findSQL)) {
            findStmt.setString(1, clientName);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("client_id");
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

        throw new SQLException("Error: Could not insert or find client: " + clientName);
    }

}