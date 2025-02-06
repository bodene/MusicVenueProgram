package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventDAO {

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
                LocalDate eventDate = rs.getString("event_date") != null ? LocalDate.parse(rs.getString("event_date")) : null;
                LocalTime eventTime = rs.getString("event_time") != null ? LocalTime.parse(rs.getString("event_time")) : null;

                int eventDuration = rs.getInt("event_duration");
                int eventCapacity = rs.getInt("required_capacity");
                String eventType = rs.getString("event_type");
                String eventCategory = rs.getString("event_category");
                String clientName = rs.getString("client_name");

                // Create and add Event object
                Event event = new Event(eventId, eventName, eventArtist, eventDate, eventTime, eventDuration, eventCapacity, eventType, eventCategory, clientName);
                eventList.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Error fetching events from database: " + e.getMessage());
        } finally {
            // ✅ Close resources properly
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (connection != null) connection.close(); // ✅ Ensures new connection each time
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("❌ Error closing resources: " + e.getMessage());
            }
        }

        // ✅ Print the results **after** the try-with-resources block
        if (!eventList.isEmpty()) {
            eventList.forEach(event -> System.out.println("✅ Loaded event: " + event.getEventName()));
        } else {
            System.out.println("❌ No events found in the database.");
        }

        return eventList;
    }

}
