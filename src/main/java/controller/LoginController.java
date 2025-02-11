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

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;

    @FXML private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }

    @FXML
    private void loginUser() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // AUTHENTICATE USER
        Optional<User> authenticatedUser = UserService.authenticateUser(username, password);

        if (authenticatedUser.isPresent()) {

            // STORE USER IN SESSION
            SessionManager.getInstance().setCurrentUser(authenticatedUser.get());

            // REDIRECT BASED ON ROLE
            if (SessionManager.getInstance().isManager()) {
                SceneManager.switchScene("manager-view.fxml");
            } else {
                SceneManager.switchScene("admin-view.fxml");
            }
        } else {
            AlertUtils.showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
        }
    }
}