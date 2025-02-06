package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;


public class VenueDAO {

    // To get Venues for table
    public static ObservableList<Venue> getAllVenues() {
        ObservableList<Venue> venueList = FXCollections.observableArrayList();

        String sql = """
                SELECT v.venue_id, v.venue_name, v.venue_category, v.venue_capacity, v.hire_price,
                               GROUP_CONCAT(vt.venue_type, ', ') AS 'venue_types'
                        FROM venues v
                        JOIN venue_types_venues vtv ON v.venue_id = vtv.venue_id
                        JOIN venue_types vt ON vtv.venue_type_id = vt.venue_type_id
                        GROUP BY v.venue_id, v.venue_name, v.venue_category, v.venue_capacity, v.hire_price;""";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int venueId = rs.getInt("venue_id");
                String venueName = rs.getString("venue_name");
                String venueCategory = rs.getString("venue_category");
                int venueCapacity = rs.getInt("venue_capacity");
                String hirePricePerHour = rs.getString("hire_price");
                String venueTypes = rs.getString("venue_types");

                // Create and add Venue object
                Venue venue = new Venue(venueId, venueName, venueCategory, venueCapacity, hirePricePerHour, venueTypes);
                venueList.add(venue);
                System.out.println("Loaded venue: " + venueName);
            }

            System.out.println("🔎 Found " + venueList.size() + " venues in database.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("❌ Error fetching venues from database.");
        }

        return venueList;
    }

    public static ObservableList<Venue> searchVenues(String searchText, List<String> categories) throws SQLException {
        ObservableList<Venue> venueList = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder("""
        SELECT v.venue_id, v.venue_name, v.venue_category, v.venue_capacity, v.hire_price,
               GROUP_CONCAT(vt.venue_type, ', ') AS venue_types
        FROM venues v
        LEFT JOIN venue_types_venues vtv ON v.venue_id = vtv.venue_id
        LEFT JOIN venue_types vt ON vtv.venue_type_id = vt.venue_type_id
        WHERE (v.venue_name LIKE ? OR ? IS NULL)
    """);

    if (categories != null && !categories.isEmpty()) {
        String categoryPlaceholders = categories.stream()
                .map(c -> "?")
                .collect(Collectors.joining(", "));
        sql.append(" AND v.venue_category IN (").append(categoryPlaceholders).append(")");
    }

    sql.append(" GROUP BY v.venue_id, v.venue_name, v.venue_category, v.venue_capacity, v.hire_price;");

    try (Connection connection = DatabaseHandler.getConnection();
    PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {

        int index = 1;

        // ✅ Set Venue Name Parameter, using '%' wildcard for LIKE search
        if (searchText != null && !searchText.trim().isEmpty()) {
            pstmt.setString(index++, "%" + searchText + "%");
            pstmt.setString(index++, searchText); // Pass second value for NULL check
        } else {
            pstmt.setString(index++, "%"); // Match everything if empty
            pstmt.setNull(index++, Types.VARCHAR); // Force NULL comparison
        }

        // ✅ Set Category Parameters correctly
        if (categories != null) {
            for (String category : categories) {
                pstmt.setString(index++, category);
            }
        }

        // **🔍 Debugging Output**
        System.out.println("Executing SQL Query: " + sql);
        System.out.println("Parameters:");
        System.out.println(" - Venue Name: " + searchText);
        System.out.println(" - Categories: " + categories);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
                int venueId = rs.getInt("venue_id");
                String venueNameResult = rs.getString("venue_name");
                String venueCategory = rs.getString("venue_category");
                int venueCapacity = rs.getInt("venue_capacity");
                String hirePricePerHour = rs.getString("hire_price");
                String venueTypes = rs.getString("venue_types");

                Venue venue = new Venue(venueId, venueNameResult, venueCategory, venueCapacity, hirePricePerHour, venueTypes);
                venueList.add(venue);
            }

            System.out.println("✅ Found " + venueList.size() + " venues matching search criteria.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Error searching venues: " + e.getMessage());
        }

        return venueList;

    }

    // Add Venue to the database
    public static boolean addVenue(Venue venue, List<String> venueTypes) {
        String insertVenueSQL = """
        INSERT INTO venues (venue_name, venue_category, venue_capacity, hire_price)
        VALUES (?, ?, ?, ?)
    """;
        String insertVenueTypeSQL = "INSERT INTO venue_types_venues (venue_id, venue_type_id) VALUES (?, ?)";

        try (Connection connection = DatabaseHandler.getConnection()) {
            connection.setAutoCommit(false); // 🔹 Start transaction

            // Insert Venue
            try (PreparedStatement venueStmt = connection.prepareStatement(insertVenueSQL, Statement.RETURN_GENERATED_KEYS)) {
                venueStmt.setString(1, venue.getName());
                venueStmt.setString(2, venue.getCategory().name());
                venueStmt.setInt(3, venue.getCapacity());
                venueStmt.setDouble(4, venue.getHirePricePerHour());

                venueStmt.executeUpdate();

                // Retrieve the generated venue_id
                ResultSet rs = venueStmt.getGeneratedKeys();
                if (rs.next()) {
                    int venueId = rs.getInt(1);

                    // Insert venue types
                    try (PreparedStatement venueTypeStmt = connection.prepareStatement(insertVenueTypeSQL)) {
                        for (String venueType : venueTypes) {
                            int venueTypeId = VenueTypeDAO.findOrCreateVenueTypeId(venueType, connection);
                            venueTypeStmt.setInt(1, venueId);
                            venueTypeStmt.setInt(2, venueTypeId);
                            venueTypeStmt.executeUpdate();
                        }
                    }
                }
            }

            connection.commit(); // 🔹 Commit transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Error saving venue: " + e.getMessage());
            return false;
        }
    }

    // Delete Venue from Database
    public static boolean deleteVenue(int venueId) {
        String deleteVenueTypesSQL = "DELETE FROM venue_types_venues WHERE venue_id = ?";
        String deleteVenueSQL = "DELETE FROM venues WHERE venue_id = ?";

        try (Connection connection = DatabaseHandler.getConnection()) {
            connection.setAutoCommit(false); // 🔹 Start transaction

            // First, delete the links in venue_types_venues
            try (PreparedStatement venueTypeStmt = connection.prepareStatement(deleteVenueTypesSQL)) {
                venueTypeStmt.setInt(1, venueId);
                venueTypeStmt.executeUpdate();
            }

            // Then, delete the venue
            try (PreparedStatement venueStmt = connection.prepareStatement(deleteVenueSQL)) {
                venueStmt.setInt(1, venueId);
                int rowsAffected = venueStmt.executeUpdate();

                if (rowsAffected > 0) {
                    connection.commit(); // 🔹 Commit transaction if successful
                    return true;
                } else {
                    connection.rollback(); // ❌ Rollback if venue deletion fails
                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Error deleting venue: " + e.getMessage());
            return false;
        }
    }




}
