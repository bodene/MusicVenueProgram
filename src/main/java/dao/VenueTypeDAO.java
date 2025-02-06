package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueTypeDAO {

    // Add view type to venue
    public void addViewType(int venueId, int venueTypeId) {
        String sql = "INSERT INTO venue_types_venues (venue_id, venue_type_id) VALUES (?, ?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            stmt.setInt(2, venueTypeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all venue types for a venue
    public List<Integer> getAllVenueTypes(int venueId) {
        List<Integer> venueTypeIds = new ArrayList<>();
        String sql = "SELECT venue_type_id FROM venue_types_venues WHERE venue_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                venueTypeIds.add(rs.getInt("venue_type_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venueTypeIds;
    }

    // Check if a venue is suitable for a given event type
    public boolean isVenueSuitable(int venueId, int venueTypeId) {
        String sql = "SELECT COUNT(*) FROM venue_types_venues WHERE venue_id = ? AND venue_type_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            stmt.setInt(2, venueTypeId);

            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Finds or creates a venueType entry in the database.
     * @param description Venue type name.
     * @param conn Active database connection.
     * @return venueTypeId
     * @throws SQLException
     */
    public static int findOrCreateVenueTypeId(String description, Connection conn) throws SQLException {
        String findSQL = "SELECT venue_type_id FROM venue_types WHERE venue_type = ?";
        String insertSQL = "INSERT INTO venue_types (venue_type) VALUES (?)";

        try (PreparedStatement findStmt = conn.prepareStatement(findSQL)) {
            findStmt.setString(1, description);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("venue_type_id"); // Found existing ID
            }
        }

        // If not found, insert and return new ID
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, description);
            insertStmt.executeUpdate();

            ResultSet rs = insertStmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return newly inserted ID
            }
        }

        throw new SQLException("Error: Could not insert or find Venue Type for: " + description);
    }
}
