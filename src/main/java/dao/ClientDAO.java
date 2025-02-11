package dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Booking;
import model.Client;
import model.Event;
import model.Venue;

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

    public static Client getClientById(int clientId) {
        String sql = "SELECT client_id, client_name, contact_info FROM clients WHERE client_id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Client(rs.getInt("client_id"),
                        rs.getString("client_name"),
                        rs.getString("contact_info"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // GET ALL CLIENT SUMMARIES FOR CLIENT COMMISSIONS
    public static List<Client> getAllClientSummaries() {
        String sql = """
                        SELECT c.client_id, c.client_name, c.contact_info, 
                                b.booking_id, b.booking_date, b.booking_status, b.booked_by,
                                        e.event_id, e.event_name, e.event_date, e.event_time, e.event_duration, e.event_artist,
                                        v.venue_id, v.venue_name, v.hire_price
                                FROM clients c
                                LEFT JOIN bookings b ON c.client_id = b.client_id
                                LEFT JOIN events e ON b.event_id = e.event_id
                                LEFT JOIN venues v ON b.venue_id = v.venue_id
                    """;
        List<Client> clientList = new ArrayList<>();

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Map<Integer, Client> clientMap = new HashMap<>();

            while (rs.next()) {
                int clientId = rs.getInt("client_id");
                Client client = clientMap.get(clientId);

                if (client == null) {
                    client = new Client(clientId, rs.getString("client_name"), rs.getString("contact_info"));
                    clientMap.put(clientId, client);
                }
                // If there is a confirmed booking
                int bookingId = rs.getInt("booking_id");
                if (bookingId > 0) {
                    LocalDate bookingDate = LocalDate.ofEpochDay(rs.getLong("booking_date"));
                    Event event = new Event(rs.getInt("event_id"), rs.getString("event_name"),
                            LocalDate.ofEpochDay(rs.getLong("event_date")),
                            LocalTime.parse(rs.getString("event_time"), DateTimeFormatter.ofPattern("HH:mm")),
                            rs.getInt("event_duration"), rs.getString("event_artist"));
                    Venue venue = new Venue(rs.getInt("venue_id"), rs.getString("venue_name"), rs.getDouble("hire_price"));
                    Booking booking = new Booking(bookingId, rs.getString("booking_status"), event, venue, client, bookingDate, rs.getString("booked_by"));

                    client.addBooking(booking);  // Ensure your Client class has an `addBooking` method
                }
            }

                // Attach dynamically calculated values
                clientList.addAll(clientMap.values());

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