package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuitabilityDAO {

    // Add suitable event type to venue
    public void addSuitability(int venueId, int eventTypeId) {
        String sql = "INSERT INTO venue_suitability (venue_id, event_type_id) VALUES (?, ?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            stmt.setInt(2, eventTypeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all suitable event types for a venue
    public List<Integer> getSuitableEventTypes(int venueId) {
        List<Integer> eventTypeIds = new ArrayList<>();
        String sql = "SELECT event_type_id FROM venue_suitability WHERE venue_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                eventTypeIds.add(rs.getInt("event_type_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventTypeIds;
    }

    // Check if a venue is suitable for a given event type
    public boolean isVenueSuitable(int venueId, int eventTypeId) {
        String sql = "SELECT COUNT(*) FROM venue_suitability WHERE venue_id = ? AND event_type_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, venueId);
            stmt.setInt(2, eventTypeId);

            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Finds or creates a suitability entry in the database.
     * @param description Suitability type (e.g., "Concert", "Conference").
     * @param conn Active database connection.
     * @return suitabilityId
     * @throws SQLException
     */
    public static int findOrCreateSuitabilityId(String description, Connection conn) throws SQLException {
        String findSQL = "SELECT suitability_id FROM suitabilities WHERE suitable_description = ?";
        String insertSQL = "INSERT INTO suitabilities (suitable_description) VALUES (?)";

        try (PreparedStatement findStmt = conn.prepareStatement(findSQL)) {
            findStmt.setString(1, description);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("suitability_id"); // Found existing ID
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

        throw new SQLException("Error: Could not insert or find Suitability for: " + description);
    }
}
