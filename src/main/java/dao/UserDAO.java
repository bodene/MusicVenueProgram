package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Staff;
import model.Manager;
import model.UserRole;
import java.sql.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static Staff getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_name = ?";

        try (Connection connection = DatabaseHandler.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {

                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String firstName = rs.getString("user_first_name");
                    String lastName = rs.getString("user_last_name");
                    String password = rs.getString("user_password");
                    String userRoleStr = rs.getString("user_role");

                    // Convert role to enum
                    UserRole role = UserRole.valueOf(userRoleStr.toUpperCase());

                    // Return correct User role
                    if (role == UserRole.MANAGER) {
                        return new Manager(userId, firstName, lastName, username, password);
                    } else {
                        return new Staff(userId, firstName, lastName, username, password, role);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return null;
    }


    // Add User to database
    public static boolean addUser(String firstName, String lastName, String username, String password, UserRole role) {
        String sql = "INSERT INTO users (user_first_name, user_last_name, user_name, user_password, user_role) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection connection = DatabaseHandler.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, username);
            pstmt.setString(4, password);
            pstmt.setString(5, role.name().toLowerCase());

            return pstmt.executeUpdate() > 0;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean userExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ?";

        try(Connection connection = DatabaseHandler.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    public static UserAuthResult authenticateUser(String username, String password) {
        String sql = "SELECT user_id, user_role FROM users WHERE user_name = ? AND user_password = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String role = rs.getString("user_role");
                return new UserAuthResult(userId, role); // Return both values
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // User not found
    }

    // Update User Details
    public static boolean updateUser(Staff loggedInUser) throws SQLException {
        String sql = "UPDATE users SET user_first_name = ?, user_last_name = ?, user_name = ?, user_password = ? WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, loggedInUser.getFirstName());
            pstmt.setString(2, loggedInUser.getLastName());
            pstmt.setString(3, loggedInUser.getUsername());
            pstmt.setString(4, loggedInUser.getPassword());

            pstmt.setInt(5, loggedInUser.getUserId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static List<Staff> searchUsers(String query) {
        List<Staff> staffList = new ArrayList<>();
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

                Staff user;
                if (role == UserRole.MANAGER) {
                    user = new Manager(userId, firstName, lastName, username, password);
                } else {
                    user = new Staff(userId, firstName, lastName, username, password, role);
                }

                staffList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffList;
    }

    // Delete User
    public static boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Promote to Manager
    public static boolean updateUserRole(int userId, UserRole newRole) throws SQLException {
        String sql = "UPDATE users SET user_role = ? WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, newRole.name().toLowerCase()); // Convert Enum to String
            pstmt.setInt(2, userId);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    // Helper class to store user ID and role together
    public static class UserAuthResult {
        private final int userId;
        private final String role;

        public UserAuthResult(int userId, String role) {
            this.userId = userId;
            this.role = role;
        }

        public int getUserId() {
            return userId;
        }

        public String getRole() {
            return role;
        }
    }

    // Management Options
    public static ObservableList<Staff> getAllUsers() {
        ObservableList<Staff> staffList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("user_first_name");
                String lastName = rs.getString("user_last_name");
                String username = rs.getString("user_name");
                String password = rs.getString("user_password");
                String userRoleStr = rs.getString("user_role");

                UserRole role = UserRole.valueOf(userRoleStr.toUpperCase());

                Staff user;
                if (role == UserRole.MANAGER) {
                    user = new Manager(userId, firstName, lastName, username, password);
                } else {
                    user = new Staff(userId, firstName, lastName, username, password, role);
                }

                staffList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffList;
    }


}
