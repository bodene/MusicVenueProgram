package controller;


import dao.UserDAO;
import model.Staff;
import model.Manager;
import model.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.SceneManager;

import java.sql.SQLException;

public class AddUserController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private RadioButton staffRadioButton;
    @FXML private RadioButton managerRadioButton;
    @FXML private Button confirmUserButton;
    @FXML private Button backButton1;

    // Ensure only one user role button is selected
    @FXML
    private void initialise() {
        ToggleGroup group = new ToggleGroup();
        staffRadioButton.setToggleGroup(group);
        managerRadioButton.setToggleGroup(group);
    }


    @FXML
    private void goToMain() {

        SceneManager.switchScene("main-view.fxml");
    }

    @FXML
    private void confirmUser() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All Fields Must be Filled", Alert.AlertType.ERROR);
            return;
        }
        // Ensure password and confirm password matches
        if(!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords must match", Alert.AlertType.ERROR);
            return;
        }

        // Determine Role
        UserRole role = null;
        if (staffRadioButton.isSelected()) {
            role = UserRole.STAFF;
        } else if (managerRadioButton.isSelected()) {
            role = UserRole.MANAGER;
        } else {
            showAlert("Error", "Please select a user role", Alert.AlertType.ERROR);
            return;
        }

        // Create and save user
        try {
            if (UserDAO.userExists(username)) {
                showAlert("Error", "User already exists", Alert.AlertType.ERROR);
                return;
            }

            boolean success = UserDAO.addUser(firstName, lastName, username, password, role);
            if (success) {
                showAlert("Success", "User has been added", Alert.AlertType.INFORMATION);
                SceneManager.switchScene("login-view.fxml");
            } else {
                showAlert("Error", "Failed to add user", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Alert System
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
