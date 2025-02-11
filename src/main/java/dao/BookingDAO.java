package dao;


import model.Booking;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookingDAO {

    private BookingDAO() {}

    // CHECKS FOR CONFLICTING BOOKING
    public static boolean checkAvailability(int venueId, LocalDate eventDate, LocalTime eventTime, int duration) throws SQLException {
        // CONVERTS EVENT START & END DATE TO TEXT
        String startTimeStr = eventTime.toString();
        String endTimeStr = eventTime.plusHours(duration).toString();

        String sql = """
            SELECT COUNT(*) AS count
            FROM bookings
            JOIN events ON bookings.event_id = events.event_id
            WHERE bookings.venue_id = ?
            AND events.event_date = ?
            AND bookings.booking_status = 'CONFIRMED'
            AND (
                (? >= events.event_time AND ? < events.event_end_time) OR
                (events.event_time >= ? AND events.event_time < ?)
            )
        """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, venueId);
            pstmt.setString(2, String.valueOf(eventDate.toEpochDay()));
            pstmt.setString(3, startTimeStr);
            pstmt.setString(4, endTimeStr);
            pstmt.setString(5, startTimeStr);
            pstmt.setString(6, endTimeStr);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("count") == 0;

        }
    }

    // BOOKS EVENT INTO DB
    public static boolean bookVenue(LocalDate bookingDate, String bookingStatus, int eventId, int venueId, int clientId, String bookedBy) throws SQLException {
        String sql = """
                INSERT INTO bookings (booking_date, booking_status, event_id, venue_id, client_id, booked_by)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(bookingDate.toEpochDay()));
            pstmt.setString(2, bookingStatus);
            pstmt.setInt(3, eventId);
            pstmt.setInt(4, venueId);
            pstmt.setInt(5, clientId);
            pstmt.setString(6, bookedBy);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Get Venue Utilisation (Number of times each venue has been booked)
    public static Map<String, Integer> getVenueUtilisation() {
        String sql = """
                        SELECT v.venue_name,
                        COUNT(b.booking_id) AS utilisation_count
                        FROM bookings b
                        JOIN venues v ON b.venue_id = v.venue_id
                        WHERE b.booking_status = 'CONFIRMED'
                        GROUP BY v.venue_name
                        """;
        Map<String, Integer> utilisationData = new HashMap<>();

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String venueName = rs.getString("venue_name");
                int count = rs.getInt("utilisation_count");
                utilisationData.put(venueName, count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisationData;
    }

    // CANCEL BOOKING
    public static boolean cancelBooking(int bookingId) throws SQLException {
        String sql = """
                        UPDATE bookings
                        SET booking_status = 'CANCELLED'
                        WHERE booking_id = ?
                    """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, bookingId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Error canceling booking with Booking ID: " + bookingId, e);
        }
    }

    // UPDATE BOOKING
    public static boolean updateBooking(Booking booking) throws SQLException {
            String sql = """
                            UPDATE bookings
                            SET booking_date = ?,
                                event_id = ?,
                                venue_id = ?,
                                client_id = ?
                            WHERE booking_id = ?
                        """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Set the booking_date to the current date
            LocalDate currentDate = LocalDate.now();
            pstmt.setString(1, String.valueOf(currentDate.toEpochDay()));
            pstmt.setInt(2, booking.getEvent().getEventId());
            pstmt.setInt(3, booking.getVenue().getVenueId());
            pstmt.setInt(4, booking.getClient().getClientId());
            pstmt.setInt(5, booking.getBookingId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating booking with ID " + booking.getBookingId(), e);
        }
    }

    // BACKUP DATABASE
    public static List<Booking> getAllBookingsBU() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                long bookingDateEpochDays = rs.getLong("booking_date");
                LocalDate bookingDate = LocalDate.ofEpochDay(bookingDateEpochDays);
                String bookingStatus = rs.getString("booking_status");
                int eventId = rs.getInt("event_id");
                int venueId = rs.getInt("venue_id");
                int clientId = rs.getInt("client_id");
                String bookedBy = rs.getString("booked_by");

                Booking booking = new Booking(bookingId, bookingDate, bookingStatus, eventId, venueId, clientId, bookedBy);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // RESTORE FROM BACKUP
    public static void insertBooking(Booking booking) {
        String sql = "INSERT INTO bookings (booking_id, booking_date, booking_status, event_id, venue_id, client_id, booked_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, booking.getBookingId());
            stmt.setString(1, String.valueOf(booking.getBookingDate().toEpochDay()));
            stmt.setString(3, booking.getStatus().toString());
            stmt.setInt(4, booking.getEvent().getEventId());
            stmt.setInt(5, booking.getVenue().getVenueId());
            stmt.setInt(6, booking.getClient().getClientId());
            stmt.setString(7, booking.getBookedBy());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}