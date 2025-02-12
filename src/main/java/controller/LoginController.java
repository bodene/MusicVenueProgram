package controller;

import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.User;
import service.SceneManager;
import service.SessionManager;
import service.UserService;
import util.AlertUtils;


/**
 * Controller class for handling user login functionality.
 * <p>
 * This class manages the login process by retrieving user input from the UI, authenticating the user via the
 * {@link UserService}, and navigating to the appropriate dashboard based on the user's role. If authentication fails,
 * an error alert is displayed.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;

    /**
     * Navigates the user back to the main view.
     * <p>
     * This method is called when the user opts to cancel the login process or return to the main screen.
     * It uses the {@link SceneManager} to switch the scene to "main-view.fxml".
     * </p>
     */
    @FXML private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }

    /**
     * Authenticates the user and navigates to the appropriate dashboard.
     * <p>
     * This method retrieves the username and password entered by the user, trims any whitespace,
     * and calls the {@link UserService#authenticateUser(String, String)} method to authenticate the user.
     * If authentication is successful, the user is stored in the session via {@link SessionManager}, and the
     * scene is switched to either the manager or admin dashboard based on the user's role.
     * If authentication fails, an error alert is displayed.
     * </p>
     */
    @FXML
    private void loginUser() {

        // Retrieve and trim user input.
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Authenticate user using the UserService.
        Optional<User> authenticatedUser = UserService.authenticateUser(username, password);

        if (authenticatedUser.isPresent()) {
            // Store authenticated user in the session.
            SessionManager.getInstance().setCurrentUser(authenticatedUser.get());

            // Redirect to the appropriate dashboard based on the user's role.
            if (SessionManager.getInstance().isManager()) {
                SceneManager.switchScene("manager-view.fxml");
            } else {
                SceneManager.switchScene("admin-view.fxml");
            }
        } else {
            // Display an error alert if authentication fails.
            AlertUtils.showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
        }
    }
}