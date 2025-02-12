package controller;

import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.SessionManager;
import service.UserService;
import model.User;
import util.AlertUtils;
import java.sql.SQLException;
import java.util.Optional;


/**
 * Controller class for editing user profile details.
 * <p>
 * This class manages the process of updating user details. It populates the UI fields with the current user's
 * information, allows managers to edit any user's details, validates input data, and saves changes via the
 * {@link UserService}. Navigation to the appropriate view is handled based on the user's role.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class UserEditController {
    private User selectedUser;

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private TextField confirmPasswordField;


    /**
     * Initialises the user edit view.
     * <p>
     * This method is automatically called after the FXML file is loaded. If no user is currently selected,
     * it retrieves the logged-in user from the {@link SessionManager}. It then fetches the most recent user details
     * from the database via the {@link UserService} and populates the UI fields.
     * </p>
     *
     * @throws SQLException if an error occurs while fetching user details from the database
     */
    @FXML
    private void initialize() throws SQLException {

        // If no user is selected, retrieve the current user from the session.
        if (selectedUser == null) {
            selectedUser = SessionManager.getInstance().getCurrentUser();
        }

        // Fetch the latest user details from the database and populate UI fields.
        if (selectedUser != null) {
            Optional<User> userFromDB = UserService.getUserByUsername(selectedUser.getUsername());
            userFromDB.ifPresent(this::populateFields);
        }
    }

    /**
     * Sets the details of the user to be edited.
     * <p>
     * This method is intended for managers who wish to edit any user's details. The provided user is stored
     * and its information is populated into the UI fields.
     * </p>
     *
     * @param user the {@code User} whose details are to be edited
     */
    public void setUserDetails(User user) {
        this.selectedUser = user;
        populateFields(user);
    }

    /**
     * Populates the UI fields with the provided user's details.
     * <p>
     * This helper method fills in the first name, last name, and username fields with data from the user object.
     * </p>
     *
     * @param user the {@code User} whose details are used to populate the fields
     */
    private void populateFields(User user) {
        firstNameField.setText(user.getFirstName());
        usernameField.setText(user.getUsername());
        lastNameField.setText(user.getLastName());
    }

    /**
     * Saves the changes made to the user's profile.
     * <p>
     * This method validates the input data to ensure that all required fields are filled and that the new password,
     * if provided, matches its confirmation. It then updates the user object accordingly and attempts to save the changes
     * using the {@link UserService}. Upon successful update, a success alert is displayed and the scene is switched based
     * on the user's role; otherwise, an error alert is shown.
     * </p>
     */
    @FXML
    public void saveProfileChanges() {

        // Ensure that a user is selected for updating.
        if (selectedUser == null) {
            AlertUtils.showAlert("Error", "No user selected for updating!", Alert.AlertType.ERROR);
            return;
        }

        // Retrieve and trim the input values.
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Validate that required fields are not empty.
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
            AlertUtils.showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        // Validate that the passwords match if a new password is provided.
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            AlertUtils.showAlert("Error", "Passwords do not match", Alert.AlertType.ERROR);
            return;
        }

        // Update the user object with the new details.
        selectedUser.setFirstName(firstName);
        selectedUser.setLastName(lastName);
        selectedUser.setUsername(username);

        if (!password.isEmpty()) {
            selectedUser.setPassword(password);
        }

        // Attempt to update the user details in the database.
        boolean success = UserService.updateUser(selectedUser);
        if (success) {
            AlertUtils.showAlert("Success", "User details updated successfully!", Alert.AlertType.INFORMATION);
            SceneManager.switchScene(SessionManager.getInstance().isManager() ? "staff-management-view.fxml" : "dashboard.fxml");
        } else {
            AlertUtils.showAlert("Error", "Failed to update profile", Alert.AlertType.ERROR);
        }
    }

    /**
     * Navigates to the settings view.
     * <p>
     * This method switches the scene to either the manager view or admin view based on the current user's role.
     * </p>
     */
    @FXML
    private void goToSettings() {
        SceneManager.switchScene(SessionManager.getInstance().isManager() ? "manager-view.fxml" : "admin-view.fxml");
    }
}