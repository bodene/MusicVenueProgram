package controller;
//done
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import service.SessionManager;
import service.UserService;
import model.User;
import util.AlertUtils;
import java.sql.SQLException;
import java.util.Optional;

public class UserEditController {
    private User selectedUser;

    @FXML private TextField firstNameField, lastNameField, usernameField, passwordField, confirmPasswordField;
    @FXML private Button updateUserDetailsButton, backButton;

    @FXML
    private void initialize() throws SQLException {
        // Get logged-in user if no selection
        if (selectedUser == null) {
            selectedUser = SessionManager.getInstance().getCurrentUser();
        }

        if (selectedUser != null) {
            Optional<User> userFromDB = UserService.getUserByUsername(selectedUser.getUsername());
            userFromDB.ifPresent(this::populateFields);
        }
    }

    // Allow managers to edit other users
    public void setUserDetails(User user) {
        this.selectedUser = user;
        populateFields(user);
    }

    // Populate UI fields
    private void populateFields(User user) {
        firstNameField.setText(user.getFirstName());
        usernameField.setText(user.getUsername());
        lastNameField.setText(user.getLastName());
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

        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            AlertUtils.showAlert("Error", "Passwords do not match", Alert.AlertType.ERROR);
            return;
        }

        // Update user fields
        selectedUser.setFirstName(firstName);
        selectedUser.setLastName(lastName);
        selectedUser.setUsername(username);

        if (!password.isEmpty()) {
            selectedUser.setPassword(password);
        }

        boolean success = UserService.updateUser(selectedUser);
        if (success) {
            AlertUtils.showAlert("Success", "User details updated successfully!", Alert.AlertType.INFORMATION);
            SceneManager.switchScene(SessionManager.getInstance().isManager() ? "staff-management-view.fxml" : "dashboard.fxml");
        } else {
            AlertUtils.showAlert("Error", "Failed to update profile", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goToSettings() {
        SceneManager.switchScene(SessionManager.getInstance().isManager() ? "manager-view.fxml" : "admin-view.fxml");
    }
}