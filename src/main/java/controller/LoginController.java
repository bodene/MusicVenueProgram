package controller;

import dao.UserDAO;
import javafx.scene.control.Alert;
import model.Staff;
import model.Manager;
import model.UserRole;
import javafx.scene.control.TextField;
import service.SceneManager;
import javafx.fxml.FXML;
import service.SessionManager;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    @FXML
    private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }

    @FXML
    private void loginUser() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Authenticate user
        UserDAO.UserAuthResult authResult = UserDAO.authenticateUser(username, password);

        if (authResult != null) {
            try {
                // Get user details from DB
                Staff loggedInUser = UserDAO.getUserByUsername(username);

                if (loggedInUser != null) {

                    // Store user in session with full details
                    SessionManager.getInstance().setCurrentUser(loggedInUser);

                    // Redirect based on role
                    if (SessionManager.getInstance().isManager()) {
                        SceneManager.switchScene("manager-view.fxml");
                    } else {
                        SceneManager.switchScene("admin-view.fxml");
                    }
                } else {
                    showAlert("Login Failed", "Error retrieving user details.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Could not retrieve user details. Please try again.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
