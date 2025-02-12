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


/**
 * Data Access Object (DAO) class for managing Client-related database operations.
 * <p>
 * This class provides methods to find or create clients, retrieve client details,
 * fetch client summaries (including associated bookings, events, and venues), and obtain client lists
 * for backup and other purposes.
 * </p>
 * <p>
 * This class uses a static collection to cache client objects, which is used by the {@code findOrCreateClient} method.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class ClientDAO {

    /**
     * Private constructor to prevent instantiation.
     */
    private ClientDAO() {}

    /**
     * A static list to cache Client objects.
     */
    private static List<Client> clients = new ArrayList<>();


    /**
     * Finds a client by name (case-insensitive) from the cached list, or creates a new client if not found.
     * <p>
     * If the client is not found in the cache, the method attempts to create the client in the database.
     * </p>
     *
     * @param clientName the name of the client
     * @return the existing or newly created {@code Client} object; {@code null} if a database error occurs
     */
    public static Client findOrCreateClient(String clientName) {
        // Search for the client in the cached list.
        for (Client client : clients) {
            if (client.getClientName().equalsIgnoreCase(clientName)) {
                return client;
            }
        }

        // If not found, attempt to create a new client in the database.
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

    /**
     * Finds an existing client ID by client name or creates a new client record and returns its generated ID.
     * <p>
     * This method first searches the database for a client with the specified name. If found, it returns the existing client ID.
     * Otherwise, it inserts a new client record and returns the generated client ID.
     * </p>
     *
     * @param clientName the name of the client
     * @param connection the database connection to use
     * @return the client ID for the existing or newly created client
     * @throws SQLException if an error occurs during the database operation
     */
    public static int findOrCreateClientId(String clientName, Connection connection) throws SQLException {
        String findSQL = "SELECT client_id FROM clients WHERE client_name = ?";
        String insertSQL = "INSERT INTO clients (client_name) VALUES (?)";

        // Try to find the client first.
        try (PreparedStatement findStmt = connection.prepareStatement(findSQL)) {
            findStmt.setString(1, clientName);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("client_id"); // Return existing client ID
            }
        }
        // If not found, insert a new client.
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

    /**
     * Retrieves a {@code Client} object from the database by its ID.
     *
     * @param clientId the ID of the client to retrieve
     * @return the {@code Client} object if found; otherwise, {@code null}
     */
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

    /**
     * Retrieves all client summaries, including associated bookings, events, and venues.
     * <p>
     * This method executes a multi-table LEFT JOIN query to obtain client data along with related booking,
     * event, and venue details. Each client is constructed only once, and if there is an associated booking,
     * an {@code Booking} object is created and added to the client.
     * </p>
     *
     * @return a {@code List<Client>} containing client summaries with their bookings
     */
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

                // Process booking details if a booking exists.
                int bookingId = rs.getInt("booking_id");
                if (bookingId > 0) {

                    // Convert booking date from epoch days to LocalDate.
                    LocalDate bookingDate = LocalDate.ofEpochDay(rs.getLong("booking_date"));

                    // Create an Event object using the event details.
                    Event event = new Event(rs.getInt("event_id"), rs.getString("event_name"),
                            LocalDate.ofEpochDay(rs.getLong("event_date")),
                            LocalTime.parse(rs.getString("event_time"), DateTimeFormatter.ofPattern("HH:mm")),
                            rs.getInt("event_duration"), rs.getString("event_artist"));

                    // Create a Venue object using the venue details.
                    Venue venue = new Venue(rs.getInt("venue_id"), rs.getString("venue_name"), rs.getDouble("hire_price"));

                    // Create a Booking object with the retrieved details.
                    Booking booking = new Booking(bookingId, rs.getString("booking_status"), event, venue, client, bookingDate, rs.getString("booked_by"));

                    // Add the booking to the client's list of bookings.
                    client.addBooking(booking);
                }
            }
            // Add all unique clients to the list.
            clientList.addAll(clientMap.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientList;
    }

    /**
     * Retrieves all clients from the database as an {@code ObservableList}.
     * <p>
     * This method is typically used to populate UI components such as ComboBoxes or TableViews.
     * </p>
     *
     * @return an {@code ObservableList<Client>} containing all clients from the database
     */
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

    /**
     * Retrieves all clients from the database for backup purposes.
     * <p>
     * This method fetches all client records from the database and returns them as a {@code List<Client>}.
     * </p>
     *
     * @return a {@code List<Client>} containing all client records
     */
    public static List<Client> getAllClientsBU() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM clients";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int clientId = rs.getInt("client_id");
                String clientName = rs.getString("client_name");
                String contactInfo = rs.getString("contact_info");
                Client client = new Client(clientId, clientName, contactInfo);
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
}