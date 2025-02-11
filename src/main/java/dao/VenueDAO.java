package dao;
//DONE
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Venue;
import model.VenueType;

public class VenueDAO {

    // ADD VENUE TO DATABASE
    public static boolean addVenue(Venue venue, List<String> venueTypes) {
        String insertVenueSQL = """
            INSERT INTO venues (venue_name, venue_category, venue_capacity, hire_price)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseHandler.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement venueStmt = connection.prepareStatement(insertVenueSQL, Statement.RETURN_GENERATED_KEYS)) {
                venueStmt.setString(1, venue.getName());
                venueStmt.setString(2, venue.getCategory().name());
                venueStmt.setInt(3, venue.getCapacity());
                venueStmt.setDouble(4, venue.getHirePricePerHour());

                venueStmt.executeUpdate();

                // RETRIEVE GENERATED VENUE_ID
                ResultSet rs = venueStmt.getGeneratedKeys();
                if (rs.next()) {
                    int venueId = rs.getInt(1);

                    // ADD VENUE TYPES
                    VenueTypeDAO.saveVenueTypes(venueId, venueTypes, connection);
                }
            }
            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error saving venue: " + e.getMessage());
            return false;
        }
    }

    // RETRIEVE ALL VENUES
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

                Venue venue = new Venue(venueId, venueName, venueCategory, venueCapacity, hirePricePerHour, venueTypes);
                venueList.add(venue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching venues from database.");
        }
        return venueList;
    }

    // SEARCH VENUES BY NAME & CATEGORY
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

    // DELETE VENUE
    public static boolean deleteVenue(int venueId) {
        String deleteVenueTypesSQL = "DELETE FROM venue_types_venues WHERE venue_id = ?";
        String deleteVenueSQL = "DELETE FROM venues WHERE venue_id = ?";

        try (Connection connection = DatabaseHandler.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement venueTypeStmt = connection.prepareStatement(deleteVenueTypesSQL)) {
                venueTypeStmt.setInt(1, venueId);
                venueTypeStmt.executeUpdate();
            }

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

    // RETRIEVE ALL VENUES FOR BACKUP
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

    // CLEAR ALL VENUES
    public static void clearAllVenues() {
        String sql = "DELETE FROM venues";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // RESTORE VENUES FROM BACKUP
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

            // Handle VenueType associations
            for (VenueType type : venue.getVenueTypes()) {
                insertVenueTypeAssociation(venue.getVenueId(), type.getVenueTypeId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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