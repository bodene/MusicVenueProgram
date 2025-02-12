package dao;

import model.VenueType;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) class for managing venue type-related database operations.
 * <p>
 * This class provides methods for saving venue types associated with venues, retrieving venue types,
 * checking if a venue is suitable for a given event type, and fetching venue type information for backup purposes.
 * </p>
 * <p>
 * All methods in this class use JDBC to interact with the database.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class VenueTypeDAO {

    private VenueTypeDAO() {}

    /**
     * Saves venue types for a given venue.
     * <p>
     * This method iterates over the provided list of venue type descriptions, retrieves or creates a venue type ID
     * for each description, and then saves the association between the venue and the venue type in the
     * <em>venue_types_venues</em> table using batch processing.
     * </p>
     *
     * @param venueId    the ID of the venue
     * @param venueTypes a {@code List<String>} containing venue type descriptions
     * @param conn       an active {@code Connection} to the database
     * @throws SQLException if a database access error occurs
     */
    public static void saveVenueTypes(int venueId, List<String> venueTypes, Connection conn) throws SQLException {
        String sql = "INSERT INTO venue_types_venues (venue_id, venue_type_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String type : venueTypes) {

                // Retrieve the venue type ID, creating the venue type if it doesn't exist.
                int typeId = findOrCreateVenueTypeId(type, conn);
                stmt.setInt(1, venueId);
                stmt.setInt(2, typeId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Retrieves all venue types for a specific venue.
     * <p>
     * This method fetches the venue type details for the specified venue ID by joining the
     * <em>venue_types_venues</em> and <em>venue_types</em> tables.
     * </p>
     *
     * @param venueId the ID of the venue
     * @return a {@code List<VenueType>} containing the venue types associated with the venue
     * @throws RuntimeException if a database access error occurs
     */
    public static List<VenueType> getAllVenueTypes(int venueId) {
        List<VenueType> venueTypes = new ArrayList<>();
        String sql = """
            SELECT vt.venue_type_id, vt.venue_type
            FROM venue_types_venues vtv
            JOIN venue_types vt ON vtv.venue_type_id = vt.venue_type_id
            WHERE vtv.venue_id = ?
        """;

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                venueTypes.add(new VenueType(rs.getInt("venue_type_id"), rs.getString("venue_type")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving venue types for venue " + venueId, e);
        }
        return venueTypes;
    }

    /**
     * Checks if a venue is suitable for a given venue type.
     * <p>
     * This method checks whether there is an association between the specified venue and venue type in the
     * <em>venue_types_venues</em> table.
     * </p>
     *
     * @param venueId    the ID of the venue
     * @param venueTypeId the ID of the venue type
     * @return {@code true} if the association exists, {@code false} otherwise
     * @throws RuntimeException if a database access error occurs
     */
    public static boolean isVenueSuitable(int venueId, int venueTypeId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM venue_types_venues WHERE venue_id = ? AND venue_type_id = ?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, venueId);
            stmt.setInt(2, venueTypeId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking suitability for venue " + venueId, e);
        }
    }


    /**
     * Finds or creates a venue type ID for a given description.
     * <p>
     * This method attempts to find the ID of a venue type by its description (case-insensitive). If not found,
     * it creates a new venue type and returns the generated ID.
     * </p>
     *
     * @param description the description of the venue type
     * @param conn        an active {@code Connection} to the database
     * @return the venue type ID
     * @throws SQLException if a database access error occurs
     */
    public static int findOrCreateVenueTypeId(String description, Connection conn) throws SQLException {
        int venueTypeId = findVenueTypeId(description, conn);
        if (venueTypeId == -1) {
            venueTypeId = createVenueType(description, conn);
        }
        return venueTypeId;
    }

    /**
     * Retrieves the venue type ID for a given description.
     * <p>
     * This method searches the <em>venue_types</em> table for a record matching the description (case-insensitive).
     * </p>
     *
     * @param description the description of the venue type
     * @param conn        an active {@code Connection} to the database
     * @return the venue type ID if found; -1 otherwise
     * @throws SQLException if a database access error occurs
     */
    public static int findVenueTypeId(String description, Connection conn) throws SQLException {
        String sql = "SELECT venue_type_id FROM venue_types WHERE LOWER(venue_type) = LOWER(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, description);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("venue_type_id");
            }
        }
        return -1;
    }

    /**
     * Creates a new venue type in the database.
     * <p>
     * This method inserts a new record into the <em>venue_types</em> table with the provided description
     * and returns the generated venue type ID.
     * </p>
     *
     * @param description the description of the venue type
     * @param conn        an active {@code Connection} to the database
     * @return the generated venue type ID
     * @throws SQLException if a database access error occurs or if the insert fails
     */
    public static int createVenueType(String description, Connection conn) throws SQLException {
        String sql = "INSERT INTO venue_types (venue_type) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, description);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Error inserting venue type: " + description);
    }

    /**
     * Retrieves all venue types from the database for backup purposes.
     * <p>
     * This method fetches all records from the <em>venue_types</em> table and constructs a list of {@code VenueType} objects.
     * </p>
     *
     * @return a {@code List<VenueType>} containing all venue types
     */
    public static List<VenueType> getAllVenueTypesBU() {
        List<VenueType> venueTypes = new ArrayList<>();
        String sql = "SELECT * FROM venue_types";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int typeId = rs.getInt("venue_type_id");
                String typeName = rs.getString("venue_type");
                VenueType venueType = new VenueType(typeId, typeName);
                venueTypes.add(venueType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venueTypes;
    }

    /**
     * Retrieves all venue-type associations from the database for backup purposes.
     * <p>
     * This method fetches all records from the <em>venue_types_venues</em> table and constructs a mapping
     * between venue IDs and lists of associated venue type IDs.
     * </p>
     *
     * @return a {@code Map<Integer, List<Integer>>} where each key is a venue ID and each value is a list of venue type IDs
     */
    public static Map<Integer, List<Integer>> getAllVenueTypesVenuesBU() {
        Map<Integer, List<Integer>> venueTypeMap = new HashMap<>();
        String sql = "SELECT venue_id, venue_type_id FROM venue_types_venues";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int venueId = rs.getInt("venue_id");
                int typeId = rs.getInt("venue_type_id");

                venueTypeMap.computeIfAbsent(venueId, k -> new ArrayList<>()).add(typeId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venueTypeMap;
    }
}