package dao;
//DONE
import model.VenueType;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenueTypeDAO {

    // SAVE VENUE TYPES FOR A GIVEN VENUE
    public static void saveVenueTypes(int venueId, List<String> venueTypes, Connection conn) throws SQLException {
        String sql = "INSERT INTO venue_types_venues (venue_id, venue_type_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String type : venueTypes) {
                int typeId = findOrCreateVenueTypeId(type, conn);
                stmt.setInt(1, venueId);
                stmt.setInt(2, typeId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    // GET ALL VENUE TYPES FOR A VENUE
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

    // CHECK IF A VENUE IS SUITABLE FOR A GIVEN EVENT TYPE
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

    // FIND OR CREATE VENUE TYPE
    public static int findOrCreateVenueTypeId(String description, Connection conn) throws SQLException {
        int venueTypeId = findVenueTypeId(description, conn);
        if (venueTypeId == -1) {
            venueTypeId = createVenueType(description, conn);
        }
        return venueTypeId;
    }

    // GET VENUE TYPE ID
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

    // CREATE VENUE TYPE
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

    // DATABASE BACKUP: Fetch all venue types with their IDs
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



    // DATABASE BACKUP: Fetch all venue-type associations
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