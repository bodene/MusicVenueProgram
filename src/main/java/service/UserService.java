package service;
//DONE
import dao.UserDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import model.UserRole;

public class UserService {

    // ADD USER (CHECKS FOR DUPLICATES)
    public static boolean addUser(String firstName, String lastName, String username, String password, UserRole role) throws SQLException {
        if (UserDAO.userExists(username)) {
            return false;
        }
        return UserDAO.addUser(firstName, lastName, username, password, role);
    }

    // GET USER BY USERNAME
    public static Optional<User> getUserByUsername(String username) throws SQLException {
        return UserDAO.findUserByUsername(username);
    }

    // AUTHENTICATE USER
    public static Optional<User> authenticateUser(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return Optional.empty();
        return UserDAO.authenticateUser(username, password);
    }

    // GET ALL USERS
    public static ObservableList<User> getAllUsers() {
        List<User> userList = UserDAO.getAllUsers();
        return FXCollections.observableArrayList(userList);
    }

    // SEARCH USERS
    public static List<User> searchUsers(String query) {
        return UserDAO.searchUsers(query);
    }

    // DELETE USER
    public static boolean deleteUser(User user) {
        try {
            return UserDAO.deleteUser(user.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // PROMOTE TO MANAGER
    public static boolean promoteToManager(User user) {
        try {
            return UserDAO.updateUserRole(user.getUserId(), UserRole.MANAGER);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE USER
    public static boolean updateUser(User user) {
        try {
            return UserDAO.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // USER REGISTRATION
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