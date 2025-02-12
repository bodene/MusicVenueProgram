package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Venue;
import model.VenueType;

/**
 * Data Access Object (DAO) class for managing venue-related database operations.
 * <p>
 * This class provides methods to add venues (and associated venue types) to the database,
 * retrieve venues (including for search and backup purposes), clear venues, and restore venues.
 * All database interactions are performed using JDBC.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class VenueDAO {

    private VenueDAO() {}

    /**
     * Adds a venue and its associated venue types to the database.
     * <p>
     * This method inserts a new venue into the <em>venues</em> table, retrieves the generated venue ID,
     * and then calls {@code VenueTypeDAO.saveVenueTypes(...)} to save the associated venue types using the same connection.
     * A transaction is used to ensure that both operations succeed together.
     * </p>
     *
     * @param venue      the {@code Venue} object containing the venue details
     * @param venueTypes a {@code List<String>} of venue type names associated with the venue
     * @return {@code true} if the venue (and its venue types) are successfully added; {@code false} otherwise
     */
    public static boolean addVenue(Venue venue, List<String> venueTypes) {
        String insertVenueSQL = """
            INSERT INTO venues (venue_name, venue_category, venue_capacity, hire_price)
            VALUES (?, ?, ?, ?)
        """;

        // Start a transaction.
        try (Connection connection = DatabaseHandler.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement venueStmt = connection.prepareStatement(insertVenueSQL, Statement.RETURN_GENERATED_KEYS)) {
                venueStmt.setString(1, venue.getName());
                venueStmt.setString(2, venue.getCategory().name());
                venueStmt.setInt(3, venue.getCapacity());
                venueStmt.setDouble(4, venue.getHirePricePerHour());

                venueStmt.executeUpdate();

                // Retrieve the generated venue_id.
                ResultSet rs = venueStmt.getGeneratedKeys();
                if (rs.next()) {
                    int venueId = rs.getInt(1);

                    // Save associated venue types using the generated venueId.
                    VenueTypeDAO.saveVenueTypes(venueId, venueTypes, connection);
                }
            }
            // Commit the transaction.
            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error saving venue: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all venues from the database.
     * <p>
     * This method executes a query to fetch venue details and uses a GROUP_CONCAT function to combine
     * multiple venue types into a comma-separated string for each venue.
     * </p>
     *
     * @return an {@code ObservableList<Venue>} containing all venues from the database
     */
    public static ObservableList<Venue> getAllVenues() {
        ObservableList<Venue> venueList = FXCollections.observableArrayList();
        String sql = """
            SELECT v.venue_id, v.venue_name, v.venue_category, v.venue_capacity, v.hire_price,
                   GROUP_CONCAT(vt.venue_type, ', ') AS venue_types
            FROM venues v
            LEFT JOIN venue_types_venues vtv ON v.venue_id = vtv.venue_id
            LEFT JOIN venue_types vt ON vtv.venue_type_id = vt.venue_type_id
            GROUP BY v.venue_id, v.venue_name, v.venue_category, v.venue_capacity, v.hire_price
        """;

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

                // Construct a Venue object with all retrieved fields.
                Venue venue = new Venue(venueId, venueName, venueCategory, venueCapacity, hirePricePerHour, venueTypes);
                venueList.add(venue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching venues from database.");
        }
        return venueList;
    }

    /**
     * Searches for venues by name and category.
     * <p>
     * This method builds a dynamic SQL query based on the provided search text and list of categories.
     * It returns an {@code ObservableList<Venue>} matching the criteria.
     * </p>
     *
     * @param searchText the search text for the venue name
     * @param categories a {@code List<String>} of venue categories to filter by
     * @return an {@code ObservableList<Venue>} containing the search results
     * @throws SQLException if a database access error occurs
     */
    public static ObservableList<Venue> searchVenuesByNameAndCategory(String searchText, List<String> categories) throws SQLException {
        ObservableList<Venue> venueList = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder("""
            SELECT v.venue_id, v.venue_name, v.venue_category, v.venue_capacity, v.hire_price,
                   GROUP_CONCAT(vt.venue_type) AS venue_types
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
            if (searchText != null && !searchText.trim().isEmpty()) {
                pstmt.setString(index++, "%" + searchText + "%");
                pstmt.setString(index++, searchText);
            } else {
                pstmt.setString(index++, "%");
                pstmt.setNull(index++, Types.VARCHAR);
            }

            if (categories != null) {
                for (String category : categories) {
                    pstmt.setString(index++, category);
                }
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int venueId = rs.getInt("venue_id");
                String venueName = rs.getString("venue_name");
                String venueCategory = rs.getString("venue_category");
                int venueCapacity = rs.getInt("venue_capacity");
                String hirePricePerHour = rs.getString("hire_price");
                String venueTypes = rs.getString("venue_types");

                Venue venue = new Venue(venueId, venueName, venueCategory, venueCapacity, hirePricePerHour, venueTypes);
                venueList.add(venue);
            }
        }
        return venueList;
    }

    /**
     * Deletes a venue and its associated venue type associations from the database.
     * <p>
     * This method first deletes related records from the <em>venue_types_venues</em> table,
     * then deletes the venue record from the <em>venues</em> table.
     * A transaction is used to ensure that both deletions succeed together.
     * </p>
     *
     * @param venueId the ID of the venue to delete
     * @return {@code true} if the venue was successfully deleted; {@code false} otherwise
     */
    public static boolean deleteVenue(int venueId) {
        String deleteVenueTypesSQL = "DELETE FROM venue_types_venues WHERE venue_id = ?";
        String deleteVenueSQL = "DELETE FROM venues WHERE venue_id = ?";

        try (Connection connection = DatabaseHandler.getConnection()) {
            connection.setAutoCommit(false);

            // Delete venue-type associations.
            try (PreparedStatement venueTypeStmt = connection.prepareStatement(deleteVenueTypesSQL)) {
                venueTypeStmt.setInt(1, venueId);
                venueTypeStmt.executeUpdate();
            }

            // Delete the venue record.
            try (PreparedStatement venueStmt = connection.prepareStatement(deleteVenueSQL)) {
                venueStmt.setInt(1, venueId);
                int rowsAffected = venueStmt.executeUpdate();

                if (rowsAffected > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting venue: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all venues from the database for backup purposes.
     * <p>
     * This method fetches all records from the <em>venues</em> table and returns them as a {@code List<Venue>}.
     * </p>
     *
     * @return a {@code List<Venue>} containing all venues for backup
     */
    public static List<Venue> getAllVenuesBU() {
        List<Venue> venueList = new ArrayList<>();
        String sql = "SELECT * FROM venues";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int venueId = rs.getInt("venue_id");
                String venueName = rs.getString("venue_name");
                String venueCategory = rs.getString("venue_category");
                int venueCapacity = rs.getInt("venue_capacity");
                String hirePricePerHour = rs.getString("hire_price");

                Venue venue = new Venue(venueId, venueName, venueCategory, venueCapacity, hirePricePerHour);
                venueList.add(venue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching venues from database.");
        }
        return venueList;
    }

    /**
     * Clears all venue records from the database.
     * <p>
     * This method executes a DELETE statement to remove all records from the <em>venues</em> table.
     * </p>
     */
    public static void clearAllVenues() {
        String sql = "DELETE FROM venues";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restores a venue from backup by inserting a venue record into the database.
     * <p>
     * This method inserts the venue into the <em>venues</em> table and then restores associated venue type
     * associations by calling {@code insertVenueTypeAssociation(...)} for each venue type.
     * </p>
     *
     * @param venue the {@code Venue} object to insert into the database
     */
    public static void insertVenue(Venue venue) {
        String sql = "INSERT INTO venues (venue_id, venue_name, venue_category, venue_capacity, hire_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, venue.getVenueId());
            stmt.setString(2, venue.getName());
            stmt.setString(3, venue.getCategory().name());
            stmt.setInt(4, venue.getCapacity());
            stmt.setDouble(5, venue.getHirePricePerHour());

            stmt.executeUpdate();

            // Handle associations with venue types.
            for (VenueType type : venue.getVenueTypes()) {
                insertVenueTypeAssociation(venue.getVenueId(), type.getVenueTypeId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts an association between a venue and a venue type into the database.
     * <p>
     * This method inserts a record into the <em>venue_types_venues</em> table for the given venue ID and venue type ID.
     * </p>
     *
     * @param venueId the ID of the venue
     * @param typeId  the ID of the venue type
     */
    private static void insertVenueTypeAssociation(int venueId, int typeId) {
        String sql = "INSERT INTO venue_types_venues (venue_id, venue_type_id) VALUES (?, ?)";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            stmt.setInt(2, typeId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}