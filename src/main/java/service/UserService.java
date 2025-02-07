package service;
//done
import dao.UserDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import model.UserRole;

public class UserService {

    // Add User (Checks for Duplicate)
    public static boolean addUser(String firstName, String lastName, String username, String password, UserRole role) throws SQLException {
        if (UserDAO.userExists(username)) {
            return false; // User already exists
        }
        return UserDAO.addUser(firstName, lastName, username, password, role);
    }

    // Get User by Username
    public static Optional<User> getUserByUsername(String username) throws SQLException {
        return UserDAO.findUserByUsername(username);
    }

    // Authenticate User
    public static Optional<User> authenticateUser(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return Optional.empty();
        return UserDAO.authenticateUser(username, password);
    }

    // Get All Users
    public static ObservableList<User> getAllUsers() {
        List<User> userList = UserDAO.getAllUsers();
        return FXCollections.observableArrayList(userList);
    }

    // Search Users
    public static List<User> searchUsers(String query) {
        return UserDAO.searchUsers(query);
    }

    // Delete User
    public static boolean deleteUser(User user) {
        try {
            return UserDAO.deleteUser(user.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Promote to Manager
    public static boolean promoteToManager(User user) {
        try {
            return UserDAO.updateUserRole(user.getUserId(), UserRole.MANAGER);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update User
    public static boolean updateUser(User user) {
        try {
            return UserDAO.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // User Registration
    public static boolean registerUser(String firstName, String lastName, String username,
                                       String password, String confirmPassword, UserRole role) throws SQLException {
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return false;
        }

        if (!password.equals(confirmPassword)) {
            return false;
        }

        if (role == null) {
            return false;
        }

        return addUser(firstName, lastName, username, password, role);
    }
}