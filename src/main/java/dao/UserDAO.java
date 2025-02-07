package dao;
//done
import model.Staff;
import model.Manager;
import model.User;
import model.UserRole;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    // User Retrieval
    public static Optional<User> findUserByUsername(String username) {
        String sql = "SELECT user_id, user_first_name, user_last_name, user_name, user_password, user_role FROM users WHERE user_name = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapUserFromResultSet(rs)); // Include password
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // User Authentication
    public static Optional<User> authenticateUser(String username, String inputPassword) {
        String sql = "SELECT * FROM users WHERE user_name = ? AND user_password = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, inputPassword); // Compare plaintext password

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // Add User
    public static boolean addUser(String firstName, String lastName, String username, String password, UserRole role) {
        String sql = "INSERT INTO users (user_first_name, user_last_name, user_name, user_password, user_role) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, username);
            pstmt.setString(4, password); // Storing plaintext password (as per your request)
            pstmt.setString(5, role.name().toLowerCase());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Check if User Exists
    public static boolean userExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Update User Details
    public static boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET user_first_name = ?, user_last_name = ?, user_name = ?, user_role = ? WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getRole().name().toLowerCase());
            pstmt.setInt(5, user.getUserId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // Update Password
    public static boolean updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET user_password = ? WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword); // Plaintext password update
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // Delete User
    public static boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Promote to Manager
    public static boolean updateUserRole(int userId, UserRole newRole) throws SQLException {
        String sql = "UPDATE users SET user_role = ? WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newRole.name().toLowerCase());
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Get All Users (Managers + Staff)
    public static List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                userList.add(mapUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    // Helper Method - Mapping Users
    private static User mapUserFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String firstName = rs.getString("user_first_name");
        String lastName = rs.getString("user_last_name");
        String username = rs.getString("user_name");
        String password = rs.getString("user_password"); // Keep as plaintext
        UserRole role = UserRole.valueOf(rs.getString("user_role").toUpperCase());

        return role == UserRole.MANAGER
                ? new Manager(userId, firstName, lastName, username, password)
                : new Staff(userId, firstName, lastName, username, password);
    }

        public static List<User> searchUsers(String query) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE user_first_name LIKE ? OR user_last_name LIKE ? OR user_name LIKE ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Use wildcards to match any part of the name
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("user_first_name");
                String lastName = rs.getString("user_last_name");
                String username = rs.getString("user_name");
                String password = rs.getString("user_password");
                String userRoleStr = rs.getString("user_role");

                UserRole role = UserRole.valueOf(userRoleStr.toUpperCase());

                User user;
                if (role == UserRole.MANAGER) {
                    user = new Manager(userId, firstName, lastName, username, password);
                } else {
                    user = new Staff(userId, firstName, lastName, username, password);
                }

                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }
}