package service;

import dao.UserDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javafx.collections.ObservableList;
import model.Staff;
import model.UserRole;

public class UserService {

    public static boolean addUser(String firstName, String lastName, String username, String password, UserRole role) throws SQLException {
        if (UserDAO.userExists(username)) {
            return false;
        }
        return UserDAO.addUser(firstName, lastName, username, password, role);
    }

    // Authenticate Users login
    public static Optional<Staff> authenticateUser(String username, String password) {
        // Avoid querying DB for empty credentials
        if (username.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }

        if (UserDAO.authenticateUser(username, password) != null) {
            try {
                return Optional.ofNullable(UserDAO.getUserByUsername(username));
            } catch (SQLException e) {
                System.err.println("Error retrieving user details: " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    public static ObservableList<Staff> getAllUsers() {
        return UserDAO.getAllUsers();
    }

    public static List<Staff> searchUsers(String query) {
        return UserDAO.searchUsers(query);
    }

    public static boolean deleteUser(Staff user) {
        try {
            return UserDAO.deleteUser(user.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean promoteToManager(Staff user) {
        try {
            return UserDAO.updateUserRole(user.getUserId(), UserRole.MANAGER);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Staff getUserByUsername(String username) throws SQLException {
        return UserDAO.getUserByUsername(username);
    }

    public static boolean updateUser(Staff user) {
        try {
            return UserDAO.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}