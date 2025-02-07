package dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


public class BookingDAO {

    // Checks for conflicting bookings for a venue
    public static boolean checkAvailability(int venueId, LocalDate eventDate, LocalTime eventTime, int duration) throws SQLException {
        // Convert event start and end times to text format
        String startTimeStr = eventTime.toString(); // Format: "HH:MM:SS"
        String endTimeStr = eventTime.plusHours(duration).toString(); // Calculate event end time

        String sql = """
            SELECT COUNT(*) AS count
            FROM bookings 
            JOIN events ON bookings.event_id = events.event_id
            WHERE bookings.venue_id = ?
            AND events.event_date = ?
            AND (
                (? >= events.event_time AND ? < events.event_end_time) OR 
                (events.event_time >= ? AND events.event_time < ?)
            )
        """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, venueId);
            pstmt.setString(2, eventDate.toString());
            pstmt.setString(3, startTimeStr);
            pstmt.setString(4, endTimeStr);
            pstmt.setString(5, startTimeStr);
            pstmt.setString(6, endTimeStr);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("count") == 0; // Returns true if no conflicts

        }
    }

    // Books an event into the database
    public static boolean bookVenue(LocalDate bookingDate, double hirePrice, String bookingStatus,
                                    int eventId, int venueId, int clientId, String bookedBy) throws SQLException {
        String sql = """
        INSERT INTO bookings (booking_date, booking_hire_price, booking_status, event_id, venue_id, client_id, booked_by)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, bookingDate.toString());
            pstmt.setDouble(2, hirePrice);
            pstmt.setString(3, bookingStatus);
            pstmt.setInt(4, eventId);
            pstmt.setInt(5, venueId);
            pstmt.setInt(6, clientId);
            pstmt.setString(7, bookedBy);

            return pstmt.executeUpdate() > 0; // Returns true if insert was successful
        }
    }
}