package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventDAO {

    // Add events to the database
    public static void saveEvents(List<Event> events) throws SQLException {
        String insertEventSQL = """
            INSERT INTO events (event_id, event_name, event_artist, event_date, event_time, event_duration, 
                                event_end_time, required_capacity, event_type, event_category, client_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement eventStmt = connection.prepareStatement(insertEventSQL)) {

            connection.setAutoCommit(false); // Disable auto-commit to batch inserts

            for (Event event : events) {
                try {
                    int eventId = generateUniqueEventId(connection); // Generate unique event ID
                    int clientId = ClientDAO.findOrCreateClientId(event.getClient().getClientName(), connection); // Ensure client exists

                    // Handle invalid client ID
                    if (clientId <= 0) {
                        continue;
                    }
                    // Calculate event end time
                    Time eventEndTime = Time.valueOf(event.getEventTime().plusHours(event.getDuration()));

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

                    eventStmt.executeUpdate();

                } catch (SQLException e) {
                    System.err.println("SQL Error inserting event: " + event.getEventName() + " | " + e.getMessage());
                    e.printStackTrace();
                }
            }

            connection.commit(); // Commit the batch insertions

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            throw new RuntimeException("Error inserting events", e);
        }
    }

    // Helper Method - Generate Unique event ID
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

                // Handle null dates safely
                long epochDays = rs.getLong("event_date");
                LocalDate eventDate = LocalDate.ofEpochDay(epochDays);
                LocalTime eventTime = LocalTime.parse(rs.getString("event_time"));
                int eventDuration = rs.getInt("event_duration");
                int eventCapacity = rs.getInt("required_capacity");
                String eventType = rs.getString("event_type");
                String eventCategory = rs.getString("event_category");
                String clientName = rs.getString("client_name");

                // Create and add Event object
                Client client = ClientDAO.findOrCreateClient(clientName);
                Event event = new Event(eventId, eventName, eventArtist, eventDate, eventTime, eventDuration,
                        eventCapacity, eventType, eventCategory, client);
                eventList.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching events from database: " + e.getMessage());
        } finally {
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

    public static boolean updateEvent(Event event) throws SQLException {
        String sql = """
        UPDATE events
        SET event_date = ?, event_time = ?, event_artist = ?
        WHERE event_id = ?
    """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(4, String.valueOf(event.getEventDate().toEpochDay()));
            pstmt.setString(2, event.getEventTime().toString());
            pstmt.setString(3, event.getArtist());
            pstmt.setInt(4, event.getEventId());
            return pstmt.executeUpdate() > 0;
        }
    }
}
