package controller;

import model.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.SceneManager;
import service.UserService;
import util.AlertUtils;
import java.sql.SQLException;

/**
 * Controller class for the "Add User" view.
 * This class handles the interactions for adding a new user.
 * It collects user inputs, performs necessary validations (such as trimming
 * input values), and delegates the registration process to the {@link UserService}. Upon successful
 * registration, it navigates to the login view using {@link SceneManager}.
 *
 * @author Bodene Downie
 * @version 1.0
 */
public class AddUserController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private RadioButton staffRadioButton;
    @FXML private RadioButton managerRadioButton;


    /**
     * Initialises the controller after its root element has been completely processed.
     * <p>
     * This method is automatically invoked by the JavaFX framework once the FXML file has been loaded.
     * It sets up a {@code ToggleGroup} for the radio buttons representing user roles so that
     * only one role can be selected at any time.
     * </p>
     */
    @FXML
    public void initialize() {
        // Create and assign a ToggleGroup to ensure exclusive selection between role radio buttons.
        ToggleGroup roleToggleGroup = new ToggleGroup();
        staffRadioButton.setToggleGroup(roleToggleGroup);
        managerRadioButton.setToggleGroup(roleToggleGroup);
    }

    /**
     * Navigates back to the main view.
     * <p>
     * This method is called when the user indicates they wish to cancel the add user operation.
     * It uses the {@link SceneManager} to switch the current scene to the main view defined in "main-view.fxml".
     * </p>
     */
    @FXML
    private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }

    /**
     * Confirms the addition of a new user by processing the provided input.
     * <p>
     * This method gathers input from various UI controls, trims whitespace,
     * and determines the user role based on the selected radio button. It then calls
     * {@link UserService#registerUser(String, String, String, String, String, UserRole)} to register the user.
     * If the registration is successful, it navigates to the login view; otherwise, if a database error occurs,
     * the exception is caught and an error alert is displayed.
     * </p>
     */
    @FXML
    private void confirmUser() {
        try {
            // Determine the user role from the selected radio button.
            UserRole selectedRole = staffRadioButton.isSelected() ? UserRole.STAFF
                    : (managerRadioButton.isSelected() ? UserRole.MANAGER : null);

            // Attempt to register the user with trimmed input values.
            boolean success = UserService.registerUser(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    usernameField.getText().trim(),
                    passwordField.getText().trim(),
                    confirmPasswordField.getText().trim(),
                    selectedRole
            );

            // If registration succeeds, switch to the login view.
            if (success) {
                SceneManager.switchScene("login-view.fxml");
            }

        } catch (SQLException e) {
            // Display an error alert if a database error occurs.
            AlertUtils.showAlert("Error", "Database error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}