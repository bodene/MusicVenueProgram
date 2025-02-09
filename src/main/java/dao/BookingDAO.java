package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Booking;
import model.Client;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookingDAO {

    // CHECKS FOR CONFLICTING BOOKING
    public static boolean checkAvailability(int venueId, LocalDate eventDate, LocalTime eventTime, int duration) throws SQLException {
        // CONVERTS EVENT START & END DATE TO TEXT
        String startTimeStr = eventTime.toString(); // Format: "HH:MM:SS"
        String endTimeStr = eventTime.plusHours(duration).toString(); // CALCULATE EVENT END DATE

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
    public static boolean bookVenue(LocalDate bookingDate, double hirePrice, String bookingStatus,
                                    int eventId, int venueId, int clientId, String bookedBy) throws SQLException {
        String sql = """
        INSERT INTO bookings (booking_date, booking_hire_price, booking_status, event_id, venue_id, client_id, booked_by)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(bookingDate.toEpochDay()));
            pstmt.setDouble(2, hirePrice);
            pstmt.setString(3, bookingStatus);
            pstmt.setInt(4, eventId);
            pstmt.setInt(5, venueId);
            pstmt.setInt(6, clientId);
            pstmt.setString(7, bookedBy);

            return pstmt.executeUpdate() > 0;
        }
    }

    // GET ALL COMMISSION SUMMARIES FOR CLIENT BOOKINGS
    public static ObservableList<Client> getAllCommissionSummaries() {
        ObservableList<Client> clientList = FXCollections.observableArrayList();

        String sql = """
        SELECT c.client_id, c.client_name, c.contact_info,
               COUNT(b.booking_id) AS total_jobs,
               SUM(b.booking_hire_price) AS total_event_spend,
               CASE 
                   WHEN COUNT(b.booking_id) > 1 THEN SUM(b.booking_hire_price) * 0.09
                   ELSE SUM(b.booking_hire_price) * 0.10 
               END AS client_commission,
               SUM(b.booking_hire_price) AS total_client_spend
        FROM clients c
        LEFT JOIN bookings b ON c.client_id = b.client_id AND b.booking_status = 'CONFIRMED'
        GROUP BY c.client_id, c.client_name, c.contact_info
    """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int clientId = rs.getInt("client_id");
                String clientName = rs.getString("client_name");
                String contactInfo = rs.getString("contact_info");
                int totalJobs = rs.getInt("total_jobs");
                double totalEventSpend = rs.getDouble("total_event_spend");
                double clientCommission = rs.getDouble("client_commission");
                double totalClientSpend = rs.getDouble("total_client_spend");

                Client client = new Client(clientId, clientName, contactInfo);
                client.setTotalJobs(totalJobs);
                client.setTotalAmountSpent(totalEventSpend);
                client.setTotalCommission(clientCommission);
                clientList.add(client);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching client commission summaries: " + e.getMessage());
        }

        return clientList;
    }

    public static ObservableList<Booking> getBookingOrderSummary() {
        ObservableList<Booking> bookingList = FXCollections.observableArrayList();

        String sql = """
                        SELECT b.booking_id, e.event_name, e.event_date, v.venue_name, 
                               b.booking_hire_price, 
                               CASE 
                                   WHEN c.total_jobs > 1 THEN b.booking_hire_price * 0.09 
                                   ELSE b.booking_hire_price * 0.10 
                               END AS event_commission,
                               b.booking_hire_price AS booking_total,
                               b.booking_status
                        FROM bookings b
                        JOIN events e ON b.event_id = e.event_id
                        JOIN venues v ON b.venue_id = v.venue_id
                        JOIN clients c ON b.client_id = c.client_id
                    """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                String eventName = rs.getString("event_name");
                long epochDays = rs.getLong("event_date");
                LocalDate eventDate = LocalDate.ofEpochDay(epochDays);
                String venueName = rs.getString("venue_name");
                double eventCost = rs.getDouble("booking_hire_price");
                double eventCommission = rs.getDouble("event_commission");
                double bookingTotal = rs.getDouble("booking_total");
                String bookingStatus = rs.getString("booking_status");

                Booking booking = new Booking(bookingId, eventName, eventDate, venueName, eventCost, eventCommission, bookingTotal, bookingStatus);
                bookingList.add(booking);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching booking summary: " + e.getMessage());
        }

        return bookingList;
    }

    // Get Venue Utilisation (Number of times each venue has been booked)
    public static Map<String, Integer> getVenueUtilisation() {
        String sql = """
                    SELECT v.venue_name, COUNT(b.booking_id) AS utilisation_count
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

    // GET VENUE INCOME
    public static Map<String, Double> getVenueIncome() {
        String sql = """
                        SELECT v.venue_name, SUM(b.booking_hire_price) AS total_income
                        FROM bookings b
                        JOIN venues v ON b.venue_id = v.venue_id
                        WHERE b.booking_status = 'CONFIRMED'
                        GROUP BY v.venue_name
                    """;
        Map<String, Double> incomeData = new HashMap<>();

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String venueName = rs.getString("venue_name");
                double totalIncome = rs.getDouble("total_income");
                incomeData.put(venueName, totalIncome);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return incomeData;
    }

    // GET VENUE COMMISSION
    public static Map<String, Double> getVenueCommission() {
        String sql = """
                        SELECT v.venue_name, SUM(b.booking_commission) AS total_commission
                        FROM bookings b
                        JOIN venues v ON b.venue_id = v.venue_id
                        WHERE b.booking_status = 'CONFIRMED'
                        GROUP BY v.venue_name
                    """;
        Map<String, Double> commissionData = new HashMap<>();

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String venueName = rs.getString("venue_name");
                double totalCommission = rs.getDouble("total_commission");
                commissionData.put(venueName, totalCommission);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commissionData;
    }

    // GET ALL BOOKING SUMMARIES FOR MANAGEMENT EVENT COMMISSION TABLE
    public static List<Booking> getAllBookingSummaries() {
        String sql = """
                        SELECT b.booking_id, e.event_name, v.venue_name, b.booking_commission, b.booked_by 
                        FROM bookings b 
                        JOIN events e ON b.event_id = e.event_id 
                        JOIN venues v ON b.venue_id = v.venue_id
                        WHERE b.booking_status = 'CONFIRMED'
                    """;

        List<Booking> bookingList = new ArrayList<>();

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int bookingId = rs.getInt("booking_id");
                String eventName = rs.getString("event_name");
                String venueName = rs.getString("venue_name");
                double bookingCommission = rs.getDouble("booking_commission");
                String bookedBy = rs.getString("booked_by");

                Booking booking = new Booking(bookingId, eventName, venueName, bookingCommission, bookedBy);
                bookingList.add(booking);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookingList;
    }

    // CANCEL BOOKING & UPDATE TOTALS TO ZERO
    public static boolean cancelBooking(int bookingId) throws SQLException {
        String sql = """
        UPDATE bookings
        SET booking_status = 'CANCELLED',
            booking_hire_price = 0.00,
            booking_commission = 0.00
        WHERE booking_id = ?
    """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error canceling booking with ID " + bookingId, e);
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

            pstmt.setString(1, String.valueOf(booking.getBookingDate().toEpochDay()));// Update booking date
            pstmt.setInt(2, booking.getEvent().getEventId());        // Update event ID
            pstmt.setInt(3, booking.getVenue().getVenueId());        // Update venue ID
            pstmt.setInt(4, booking.getClient().getClientId());      // Update client ID
            pstmt.setInt(5, booking.getBookingId());                 // WHERE condition to match booking ID

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating booking with ID " + booking.getBookingId(), e);
        }
    }



}