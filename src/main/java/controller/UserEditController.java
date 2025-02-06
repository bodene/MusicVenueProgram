package controller;

import javafx.event.ActionEvent;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import service.SessionManager;
import dao.UserDAO;
import model.Staff;
import model.Manager;
import model.UserRole;
import util.AlertUtils;

import java.sql.SQLException;

public class UserEditController {
    private Staff selectedUser;

    @FXML private TextField firstNameField, lastNameField, usernameField, passwordField, confirmPasswordField;
    @FXML private Button updateUserDetailsButton, backButton;

    @FXML
    private void initialize() throws SQLException {
        try {
            if (selectedUser == null) { // Get the logged-in user if no selection
                selectedUser = SessionManager.getInstance().getCurrentUser();
            }
            if (selectedUser != null) {
                Staff userFromDB = UserService.getUserByUsername(selectedUser.getUsername());
                if (userFromDB != null) {
                    populateFields(userFromDB);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Called when a manager selects a user to edit
    public void setUserDetails(Staff staff) {
        this.selectedUser = staff;
    }

    // Helper Method - Populate fields
    private void populateFields(Staff user) {
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        usernameField.setText(user.getUsername());
    }

    @FXML
    public void saveProfileChanges(ActionEvent actionEvent) {
        if (selectedUser == null) {
            AlertUtils.showAlert("Error", "No user selected for updating!", Alert.AlertType.ERROR);
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
            AlertUtils.showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        // Check Passwords Match
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            AlertUtils.showAlert("Error", "Passwords do not match", Alert.AlertType.ERROR);
            return;
        }

        // Update user details
        selectedUser.setFirstName(firstName);
        selectedUser.setLastName(lastName);
        selectedUser.setUsername(username);
        if (!password.isEmpty()) {
            selectedUser.setPassword(password);
        }

        boolean success = UserService.updateUser(selectedUser);
        if (success) {
            AlertUtils.showAlert("Success", "User details updated successfully!", Alert.AlertType.INFORMATION);

            // Redirect user based on role
            if (SessionManager.getInstance().isManager()) {
                SceneManager.switchScene("staff-management-view.fxml");
            } else {
                SceneManager.switchScene("dashboard.fxml");
            }
        } else {
            AlertUtils.showAlert("Error", "Failed to update profile", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goToSettings() {
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {
            SceneManager.switchScene("admin-view.fxml");
        }
    }
}