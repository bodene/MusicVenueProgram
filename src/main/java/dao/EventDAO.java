package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing Event-related database operations.
 * <p>
 * This class provides methods to save events to the database, retrieve events,
 * update event details, clear events, and restore events from backup.
 * It uses JDBC to connect to an SQLite database and follows a static-method-only pattern.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class EventDAO {

    /**
     * Private constructor to prevent instantiation.
     */
    private EventDAO() {}


    /**
     * Saves a list of events to the database.
     * <p>
     * For each event in the provided list, a unique event ID is generated and the associated
     * client is ensured to exist in the database. The event end time is calculated from the start time
     * and duration. The events are inserted using a prepared statement within a transaction.
     * </p>
     *
     * @param events the list of {@code Event} objects to save
     * @throws SQLException if a database access error occurs
     */
    public static void saveEvents(List<Event> events) throws SQLException {
        String insertEventSQL = """
            INSERT INTO events (event_id, event_name, event_artist, event_date, event_time, event_duration,
                                event_end_time, required_capacity, event_type, event_category, client_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement eventStmt = connection.prepareStatement(insertEventSQL)) {

            // Disable auto-commit to execute batch inserts within a transaction.
            connection.setAutoCommit(false); // Disable auto-commit to batch inserts

            for (Event event : events) {
                try {
                    // Generate a unique event ID for each event.
                    int eventId = generateUniqueEventId(connection);

                    // Ensure the client exists in the database and retrieve its ID.
                    int clientId = ClientDAO.findOrCreateClientId(event.getClient().getClientName(), connection); // Ensure client exists

                    // If client ID is invalid, skip this event.
                    if (clientId <= 0) {
                        continue;
                    }
                    // Calculate event end time by adding the duration (in hours) to the start time.
                    Time eventEndTime = Time.valueOf(event.getEventTime().plusHours(event.getDuration()));

                    // Set parameters for the prepared statement.
                    eventStmt.setInt(1, eventId);
                    eventStmt.setString(2, event.getEventName());
                    eventStmt.setString(3, event.getArtist());
                    eventStmt.setString(4, String.valueOf(event.getEventDate().toEpochDay()));
                    eventStmt.setString(5, event.getEventTime().toString());
                    eventStmt.setInt(6, event.getDuration());
                    eventStmt.setString(7, eventEndTime.toString());
                    eventStmt.setInt(8, event.getRequiredCapacity());
                    eventStmt.setString(9, event.getEventType());
                    eventStmt.setString(10, event.getCategory().name());
                    eventStmt.setInt(11, clientId);

                    // Execute the insertion.
                    eventStmt.executeUpdate();

                } catch (SQLException e) {
                    // Log error for the specific event and continue with the next.
                    System.err.println("SQL Error inserting event: " + event.getEventName() + " | " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Commit all insertions.
            connection.commit();

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            throw new RuntimeException("Error inserting events", e);
        }
    }

    /**
     * Generates a unique event ID by retrieving the current maximum event_id from the database and adding 1.
     *
     * @param connection the {@code Connection} to the database
     * @return the next available event ID as an integer
     * @throws SQLException if a database access error occurs
     */
    private static int generateUniqueEventId(Connection connection) throws SQLException {
        String query = "SELECT COALESCE(MAX(event_id), 0) + 1 FROM events";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1); // Return the next available event_id
            }
        }
        throw new SQLException("Error generating event ID");
    }

    /**
     * Retrieves all events from the database.
     * <p>
     * This method performs a JOIN query between events and clients to retrieve event details along with the
     * associated client name. Each event is constructed as an {@code Event} object.
     * </p>
     *
     * @return an {@code ObservableList<Event>} containing all events
     */
    public static ObservableList<Event> getAllEvents() {
        ObservableList<Event> eventList = FXCollections.observableArrayList();

        String sql = """
        SELECT e.event_id, e.event_name, e.event_artist, e.event_date, e.event_time,
               e.event_duration, e.required_capacity, e.event_type, e.event_category,
               c.client_name
        FROM events e
        JOIN clients c ON e.client_id = c.client_id""";

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseHandler.getConnection();
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventName = rs.getString("event_name");
                String eventArtist = rs.getString("event_artist");

                // Convert the stored epoch days into a LocalDate.
                long epochDays = rs.getLong("event_date");
                LocalDate eventDate = LocalDate.ofEpochDay(epochDays);

                // Parse the event time from the stored string.
                LocalTime eventTime = LocalTime.parse(rs.getString("event_time"));
                int eventDuration = rs.getInt("event_duration");
                int eventCapacity = rs.getInt("required_capacity");
                String eventType = rs.getString("event_type");
                String eventCategory = rs.getString("event_category");
                String clientName = rs.getString("client_name");

                // Retrieve the client; if not present, create a new one.
                Client client = ClientDAO.findOrCreateClient(clientName);

                // Construct an Event object.
                Event event = new Event(eventId, eventName, eventArtist, eventDate, eventTime, eventDuration,
                        eventCapacity, eventType, eventCategory, client);
                eventList.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching events from database: " + e.getMessage());
        } finally {
            // Close resources in reverse order of their opening.
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return eventList;
    }

    /**
     * Updates the details of an event in the database.
     * <p>
     * Only the event date, event time, and event artist are updated. The update is based on the event ID.
     * </p>
     *
     * @param event the {@code Event} object containing updated details
     * @return {@code true} if the update was successful, {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean updateEvent(Event event) throws SQLException {
        String sql = """
        UPDATE events
        SET event_date = ?, event_time = ?, event_artist = ?
        WHERE event_id = ?
    """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(event.getEventDate().toEpochDay()));
            pstmt.setString(2, event.getEventTime().toString());
            pstmt.setString(3, event.getArtist());
            pstmt.setInt(4, event.getEventId());
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all events from the database for backup purposes.
     * <p>
     * This method fetches all records from the events table and constructs a list of {@code Event} objects.
     * The event date is converted from epoch days and the event time is parsed from its string representation.
     * </p>
     *
     * @return a {@code List<Event>} containing all events
     */
    public static List<Event> getAllEventsBU() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM events";
        try (Connection conn = DatabaseHandler.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventName = rs.getString("event_name");
                String eventArtist = rs.getString("event_artist");
                long epochDays = rs.getLong("event_date");
                LocalDate eventDate = LocalDate.ofEpochDay(epochDays);
                LocalTime eventTime = LocalTime.parse(rs.getString("event_time"));
                LocalTime eventEndTime = LocalTime.parse(rs.getString("event_end_time"));
                int eventDuration = rs.getInt("event_duration");
                int eventCapacity = rs.getInt("required_capacity");
                String eventType = rs.getString("event_type");
                String eventCategory = rs.getString("event_category");
                int clientId = rs.getInt("client_id");

                // Construct an Event object with a constructor that takes clientId.
                Event event = new Event(eventId, eventName, eventArtist, eventDate, eventTime, eventDuration, eventCapacity, eventType, eventCategory, clientId);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Clears all events from the database.
     * <p>
     * This method executes a DELETE statement to remove all records from the events table.
     * </p>
     */
    public static void clearAllEvents() {
        String sql = "DELETE FROM events";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts an event into the database (used for restoring from backup).
     * <p>
     * This method inserts a record into the events table with all relevant fields.
     * </p>
     *
     * @param event the {@code Event} object to insert
     */
    public static void insertEvent(Event event) {
        String sql = "INSERT INTO events (event_id, event_name, event_artist, event_date, event_time, event_duration, required_capacity, event_type, event_category, client_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, event.getEventId());
            stmt.setString(2, event.getEventName());
            stmt.setString(3, event.getArtist());
            stmt.setString(4, String.valueOf(event.getEventDate().toEpochDay()));
            stmt.setString(5, event.getEventTime().toString());
            stmt.setInt(6, event.getDuration());
            stmt.setInt(7, event.getRequiredCapacity());
            stmt.setString(8, event.getEventType());
            stmt.setString(9, event.getCategory().toString());
            stmt.setInt(10, event.getClient().getClientId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}