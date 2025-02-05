package dao;
import model.Booking;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // Checks for conflicting bookings for a venue
    public List<Booking> getConflictingBookings(int venueId, LocalDate requestedDate, LocalTime requestedTime, LocalTime endTime) {
        List<Booking> bookings = new ArrayList<>();
//        String sql = "SELECT * FROM bookings WHERE venue_id = ? AND booking_date = ? " +
//                "AND ((start_time < ? AND end_time > ?) " +
//                "OR (start_time >= ? AND start_time < ?))";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement stmt = connection.prepareStatement(sql)) {
//
//            stmt.setInt(1, venueId);
//            stmt.setObject(2, requestedDate);
//            stmt.setObject(3, endTime);
//            stmt.setObject(4, requestedTime);
//            stmt.setObject(5, requestedTime);
//            stmt.setObject(6, endTime);
//
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                bookings.add(new Booking(
//                        rs.getInt("id"),
//                        rs.getInt("venue_id"),
//                        rs.getObject("booking_date", LocalDate.class),
//                        rs.getObject("start_time", LocalTime.class),
//                        rs.getObject("end_time", LocalTime.class),
//                        rs.getInt("client_id")
//                ));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        return bookings;
    }

    // Books an event into the database
    public boolean bookEvent(int venueId, int clientId, LocalDate eventDate, LocalTime startTime, int durationHours) {
//        // Calculate end time
//        LocalTime endTime = startTime.plusHours(durationHours);
//
//        String sql = "INSERT INTO bookings (venue_id, client_id, booking_date, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, venueId);
//            stmt.setInt(2, clientId);
//            stmt.setObject(3, eventDate);
//            stmt.setObject(4, startTime);
//            stmt.setObject(5, endTime);
//
//            return stmt.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
            return false;
//        }
    }
}

