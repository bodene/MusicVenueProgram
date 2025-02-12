package dao;

import model.Staff;
import model.Manager;
import model.User;
import model.UserRole;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Data Access Object (DAO) class for managing User-related database operations.
 * <p>
 * This class provides methods for user retrieval, authentication, creation, updating,
 * deletion, role promotion, and searching. It uses JDBC to interact with an SQLite database.
 * </p>
 *
 * <p>
 * The class supports both staff and manager users, mapping the result set to the appropriate object type.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class UserDAO {

    private UserDAO() {}

    /**
     * Retrieves a user by username.
     * <p>
     * This method executes a query to retrieve a user record from the database based on the provided username.
     * If a record is found, it is mapped to a {@code User} object and returned within an {@code Optional}.
     * </p>
     *
     * @param username the username to search for
     * @return an {@code Optional<User>} containing the found user, or an empty {@code Optional} if not found
     */
    public static Optional<User> findUserByUsername(String username) {
        String sql = "SELECT user_id, user_first_name, user_last_name, user_name, user_password, user_role FROM users WHERE user_name = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapUserFromResultSet(rs)); // Map the result set to a User object (including password)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Authenticates a user based on username and password.
     * <p>
     * This method executes a query to verify that a user exists with the provided username and password.
     * If a matching record is found, it is mapped to a {@code User} object and returned within an {@code Optional}.
     * </p>
     *
     * @param username      the username to authenticate
     * @param inputPassword the password to authenticate
     * @return an {@code Optional<User>} containing the authenticated user, or an empty {@code Optional} if authentication fails
     */
    public static Optional<User> authenticateUser(String username, String inputPassword) {
        String sql = "SELECT * FROM users WHERE user_name = ? AND user_password = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, inputPassword);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Adds a new user to the database.
     * <p>
     * This method inserts a new user record with the provided details into the users table.
     * Note: The password is stored in plaintext.
     * </p>
     *
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param username  the username
     * @param password  the user's password
     * @param role      the user's role
     * @return {@code true} if the user was added successfully; {@code false} otherwise
     */
    public static boolean addUser(String firstName, String lastName, String username, String password, UserRole role) {
        String sql = "INSERT INTO users (user_first_name, user_last_name, user_name, user_password, user_role) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

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

    /**
     * Checks if a user with the specified username exists.
     * <p>
     * This method executes a COUNT query to determine if any user record exists with the given username.
     * </p>
     *
     * @param username the username to check for existence
     * @return {@code true} if the user exists; {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean userExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * Updates the details of an existing user.
     * <p>
     * This method updates the first name, last name, username, and role for the user identified by the user ID.
     * </p>
     *
     * @param user the {@code User} object with updated details
     * @return {@code true} if the update was successful; {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Updates the password for a given user.
     * <p>
     * This method updates the password for the user identified by the provided user ID.
     * </p>
     *
     * @param userId      the ID of the user whose password is to be updated
     * @param newPassword the new password
     * @return {@code true} if the password update was successful; {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET user_password = ? WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a user from the database.
     * <p>
     * This method deletes the user record identified by the provided user ID.
     * </p>
     *
     * @param userId the ID of the user to delete
     * @return {@code true} if the deletion was successful; {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Promotes a user by updating their role.
     * <p>
     * This method updates the role of the user identified by the provided user ID to the specified new role.
     * </p>
     *
     * @param userId  the ID of the user whose role is to be updated
     * @param newRole the new {@code UserRole} to assign
     * @return {@code true} if the update was successful; {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean updateUserRole(int userId, UserRole newRole) throws SQLException {
        String sql = "UPDATE users SET user_role = ? WHERE user_id = ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newRole.name().toLowerCase());
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all users from the database.
     * <p>
     * This method executes a query to fetch all user records and maps each record to a {@code User} object.
     * </p>
     *
     * @return a {@code List<User>} containing all users
     */
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


    /**
     * Maps a {@code ResultSet} row to a {@code User} object.
     * <p>
     * This helper method reads the user fields from the result set and creates either a {@code Manager} or
     * {@code Staff} object based on the user's role.
     * </p>
     *
     * @param rs the {@code ResultSet} containing user data
     * @return a {@code User} object representing the user in the current row of the result set
     * @throws SQLException if an error occurs while accessing the result set data
     */
    private static User mapUserFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String firstName = rs.getString("user_first_name");
        String lastName = rs.getString("user_last_name");
        String username = rs.getString("user_name");
        String password = rs.getString("user_password");
        UserRole role = UserRole.valueOf(rs.getString("user_role").toUpperCase());

        return role == UserRole.MANAGER
                ? new Manager(userId, firstName, lastName, username, password)
                : new Staff(userId, firstName, lastName, username, password);
    }

    /**
     * Searches for users by first name, last name, or username.
     * <p>
     * This method performs a case-insensitive search using the SQL LIKE operator on the {@code user_first_name},
     * {@code user_last_name}, and {@code user_name} columns.
     * </p>
     *
     * @param query the search query string
     * @return a {@code List<User>} containing the users that match the search query
     */
    public static List<User> searchUsers(String query) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE user_first_name LIKE ? OR user_last_name LIKE ? OR user_name LIKE ?";

        try (Connection connection = DatabaseHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Use wildcards to match any part of the name.
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Retrieve user details from the result set.
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("user_first_name");
                String lastName = rs.getString("user_last_name");
                String username = rs.getString("user_name");
                String password = rs.getString("user_password");
                String userRoleStr = rs.getString("user_role");

                UserRole role = UserRole.valueOf(userRoleStr.toUpperCase());

                // Map to the correct user type.
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