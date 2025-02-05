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

import java.sql.SQLException;

public class UserEditController {
    private Staff selectedUser;

    @FXML
    private TextField firstNameField, lastNameField, usernameField, passwordField, confirmPasswordField;

    @FXML
    private Button updateUserDetailsButton, backButton;

    @FXML
    private void initialize() throws SQLException {
        try {
            if (selectedUser == null) { // If no selected user, get the logged-in user
                selectedUser = SessionManager.getInstance().getCurrentUser();
            }
            if (selectedUser != null) {
                // Get the latest details from the database
                Staff userFromDB = UserDAO.getUserByUsername(selectedUser.getUsername());

                if (userFromDB != null) {
                            populateFields(userFromDB);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is called when a manager selects a user
    public void setUserDetails(Staff staff) {
        this.selectedUser = staff;
    }

    // Helper method to populate fields
    private void populateFields(Staff user) {
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        usernameField.setText(user.getUsername());
    }

    @FXML
    public void saveProfileChanges(ActionEvent actionEvent) {
        if (selectedUser == null) {
            showAlert("Error", "No user selected for updating!", Alert.AlertType.ERROR);
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        // Check Passwords Match
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match", Alert.AlertType.ERROR);
            return;
        }

        try {
            selectedUser.setFirstName(firstName);
            selectedUser.setLastName(lastName);
            selectedUser.setUsername(username);

            if (!password.isEmpty()) {
                selectedUser.setPassword(password);
            }

            UserDAO.updateUser(selectedUser);
            showAlert("Success", "User details have been updated", Alert.AlertType.INFORMATION);

            // Redirect appropriately
            if (SessionManager.getInstance().isManager()) {
                SceneManager.switchScene("staff-management-view.fxml"); // Redirects manager back
            } else {
                SceneManager.switchScene("dashboard.fxml"); // Redirects staff back
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to update profile", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void goToSettings() {
        // Redirect to Admin Setting Page
        if (SessionManager.getInstance().isManager()) {
            SceneManager.switchScene("manager-view.fxml");
        } else {
            SceneManager.switchScene("admin-view.fxml");
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