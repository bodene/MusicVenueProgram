package dao;


import model.Booking;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Data Access Object (DAO) class for managing booking-related database operations.
 * <p>
 * This class provides methods for checking venue availability, booking venues, retrieving venue utilisation data,
 * canceling and updating bookings, and handling backup and restore operations for bookings.
 * </p>
 *
 * <p>
 * The class follows a static-method-only pattern and cannot be instantiated.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class BookingDAO {

    /**
     * Private constructor to prevent instantiation.
     */
    private BookingDAO() {}

    /**
     * Checks for conflicting bookings for a given venue at a specific date and time.
     * <p>
     * This method determines whether a venue is available by checking for any confirmed bookings
     * that conflict with the specified event time and duration.
     * </p>
     *
     * @param venueId   the ID of the venue to check
     * @param eventDate the date of the event
     * @param eventTime the start time of the event
     * @param duration  the duration of the event in hours
     * @return {@code true} if the venue is available (i.e., no conflicting confirmed bookings exist), {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean checkAvailability(int venueId, LocalDate eventDate, LocalTime eventTime, int duration) throws SQLException {
        // Convert event start and end time to String.
        String startTimeStr = eventTime.toString();
        String endTimeStr = eventTime.plusHours(duration).toString();

        // SQL query to count conflicting confirmed bookings.
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

            // Return true if count is zero (i.e., no conflicting bookings)
            return rs.next() && rs.getInt("count") == 0;
        }
    }

    /**
     * Books an event venue by inserting a new booking record into the database.
     * <p>
     * This method inserts a new booking into the bookings table using the provided details.
     * </p>
     *
     * @param bookingDate  the date the booking is made
     * @param bookingStatus the status of the booking (e.g., "CONFIRMED")
     * @param eventId      the ID of the event
     * @param venueId      the ID of the venue
     * @param clientId     the ID of the client
     * @param bookedBy     the username of the person who made the booking
     * @return {@code true} if the booking was inserted successfully, {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Retrieves venue utilisation data.
     * <p>
     * This method returns a map where the key is the venue name and the value is the number of times
     * the venue has been booked (i.e., utilisation count) for confirmed bookings.
     * </p>
     *
     * @return a {@code Map<String, Integer>} mapping venue names to their utilisation counts
     */
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

    /**
     * Cancels a booking by updating its status to "CANCELLED".
     * <p>
     * This method updates the booking status in the database to indicate that the booking has been cancelled.
     * </p>
     *
     * @param bookingId the ID of the booking to cancel
     * @return {@code true} if the booking was successfully cancelled, {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Updates a booking record in the database.
     * <p>
     * This method updates the booking's date, event ID, venue ID, and client ID for the specified booking.
     * The booking date is updated to the current date.
     * </p>
     *
     * @param booking the {@code Booking} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Retrieves all bookings for backup purposes.
     * <p>
     * This method fetches all bookings from the database and constructs a list of {@code Booking} objects.
     * The booking date is converted from epoch days to {@code LocalDate}.
     * </p>
     *
     * @return a {@code List<Booking>} containing all bookings
     */
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

    /**
     * Inserts a booking record into the database (used for restoring from backup).
     * <p>
     * This method inserts a booking into the bookings table using the provided {@code Booking} object's details.
     * </p>
     *
     * @param booking the {@code Booking} object to be inserted into the database
     */
    public static void insertBooking(Booking booking) {
        String sql = "INSERT INTO bookings (booking_id, booking_date, booking_status, event_id, venue_id, client_id, booked_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, booking.getBookingId());
            stmt.setString(2, String.valueOf(booking.getBookingDate().toEpochDay()));
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