package controller;

import model.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.SceneManager;
import service.UserService;
import util.AlertUtils;
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

    private ToggleGroup roleToggleGroup;

    // Ensure only one user role button is selected
    @FXML
    public void initialize() {
        roleToggleGroup = new ToggleGroup();
        staffRadioButton.setToggleGroup(roleToggleGroup);
        managerRadioButton.setToggleGroup(roleToggleGroup);
    }

    // Go to Entry Screen
    @FXML
    private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }

    // Add new user
    @FXML
    private void confirmUser() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            AlertUtils.showAlert("Error", "All Fields Must be Filled", Alert.AlertType.ERROR);
            return;
        }

        // Ensure password and confirm password matches
        if(!password.equals(confirmPassword)) {
            AlertUtils.showAlert("Error", "Passwords must match", Alert.AlertType.ERROR);
            return;
        }

        // Determine Role
        UserRole role = null;
        if (staffRadioButton.isSelected()) {
            role = UserRole.STAFF;
        } else if (managerRadioButton.isSelected()) {
            role = UserRole.MANAGER;
        } else {
            AlertUtils.showAlert("Error", "Please select a user role", Alert.AlertType.ERROR);
            return;
        }

        // Create and save user
        try {
            boolean success = UserService.addUser(firstName, lastName, username, password, role);
            if (success) {
                AlertUtils.showAlert("Success", "User has been added", Alert.AlertType.INFORMATION);
                SceneManager.switchScene("login-view.fxml");
            } else {
                AlertUtils.showAlert("Error", "User already exists", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Error", "Database error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
