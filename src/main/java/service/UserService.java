package service;

import dao.UserDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import model.UserRole;

/**
 * Provides services related to user management.
 * <p>
 * The {@code UserService} class acts as a service layer between the UI and the data access layer.
 * It provides methods to register, authenticate, retrieve, search, update, and delete users.
 * </p>
 *
 * @author Bodene Downie
 * @version 1.0
 */
public class UserService {

    private UserService() {}

    /**
     * Adds a new user to the system after checking for duplicates.
     * <p>
     * This method first checks if a user with the given username already exists using {@link UserDAO#userExists(String)}.
     * If a duplicate exists, it returns {@code false}; otherwise, it proceeds to add the user.
     * </p>
     *
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param username  the user's username
     * @param password  the user's password
     * @param role      the {@code UserRole} of the user
     * @return {@code true} if the user was added successfully; {@code false} if the user already exists or addition fails
     * @throws SQLException if a database access error occurs
     */
    public static boolean addUser(String firstName, String lastName, String username, String password, UserRole role) throws SQLException {
        // Check for duplicate username.
        if (UserDAO.userExists(username)) {
            return false;
        }
        // Add user to the database.
        return UserDAO.addUser(firstName, lastName, username, password, role);
    }

    /**
     * Retrieves a user by username.
     * <p>
     * This method returns an {@code Optional<User>} containing the user if found.
     * </p>
     *
     * @param username the username to search for
     * @return an {@code Optional<User>} containing the found user, or an empty Optional if not found
     * @throws SQLException if a database access error occurs
     */
    public static Optional<User> getUserByUsername(String username) throws SQLException {
        return UserDAO.findUserByUsername(username);
    }

    /**
     * Authenticates a user based on the provided username and password.
     * <p>
     * If either the username or password is empty, the method returns an empty {@code Optional}.
     * Otherwise, it delegates authentication to {@link UserDAO#authenticateUser(String, String)}.
     * </p>
     *
     * @param username the username provided for authentication
     * @param password the password provided for authentication
     * @return an {@code Optional<User>} containing the authenticated user if credentials match; otherwise, an empty Optional
     */
    public static Optional<User> authenticateUser(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return Optional.empty();
        return UserDAO.authenticateUser(username, password);
    }

    /**
     * Retrieves all users from the system.
     * <p>
     * This method returns an {@code ObservableList<User>} constructed from a list obtained via {@link UserDAO#getAllUsers()}.
     * </p>
     *
     * @return an {@code ObservableList<User>} containing all users
     */
    public static ObservableList<User> getAllUsers() {
        List<User> userList = UserDAO.getAllUsers();
        return FXCollections.observableArrayList(userList);
    }

    /**
     * Searches for users based on a query string.
     * <p>
     * The search is performed on first name, last name, and username fields.
     * </p>
     *
     * @param query the search query
     * @return a {@code List<User>} containing users matching the query
     */
    public static List<User> searchUsers(String query) {
        return UserDAO.searchUsers(query);
    }

    /**
     * Deletes a user from the system.
     * <p>
     * This method attempts to delete the user by their user ID using {@link UserDAO#deleteUser(int)}.
     * If a {@code SQLException} occurs, it prints the stack trace and returns {@code false}.
     * </p>
     *
     * @param user the {@code User} to delete
     * @return {@code true} if deletion was successful; {@code false} otherwise
     */
    public static boolean deleteUser(User user) {
        try {
            return UserDAO.deleteUser(user.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Promotes a user to a manager.
     * <p>
     * This method updates the user's role to {@link UserRole#MANAGER} using {@link UserDAO#updateUserRole(int, UserRole)}.
     * </p>
     *
     * @param user the {@code User} to promote
     * @return {@code true} if the promotion was successful; {@code false} otherwise
     */
    public static boolean promoteToManager(User user) {
        try {
            return UserDAO.updateUserRole(user.getUserId(), UserRole.MANAGER);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the details of a user.
     * <p>
     * This method calls {@link UserDAO#updateUser(User)} to update the user's details in the database.
     * </p>
     *
     * @param user the {@code User} object containing updated information
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    public static boolean updateUser(User user) {
        try {
            return UserDAO.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Registers a new user in the system.
     * <p>
     * This method validates that all required fields are filled, that the password and confirmation match,
     * and that a valid role is provided. If validation passes, it delegates the user creation.
     * </p>
     *
     * @param firstName       the user's first name
     * @param lastName        the user's last name
     * @param username        the user's username
     * @param password        the user's password
     * @param confirmPassword the confirmation of the user's password
     * @param role            the {@code UserRole} for the new user
     * @return {@code true} if registration was successful; {@code false} otherwise
     * @throws SQLException if a database access error occurs during registration
     */
    public static boolean registerUser(String firstName, String lastName, String username,
                                       String password, String confirmPassword, UserRole role) throws SQLException {

        // Validate that none of the required fields are empty.
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return false;
        }

        // Ensure that the password and confirmation match.
        if (!password.equals(confirmPassword)) {
            return false;
        }
        // Ensure a valid role is provided.
        if (role == null) {
            return false;
        }

        // Delegate to addUser after passing validation.
        return addUser(firstName, lastName, username, password, role);
    }
}