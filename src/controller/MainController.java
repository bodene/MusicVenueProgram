package controller;

import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;

public class MainController {

    @FXML
    private AnchorPane managerCodePane;

    @FXML
    private PasswordField managerCodeField;

    // Define Manager code
    private final String MANAGER_CODE = "909";

    @FXML
    private void goToSignUp() {
        // Show manage code pane and prompt for code
        managerCodePane.setVisible(true);
    }

    // When submit is pressed in manager code pane
    @FXML
    private void handleManagerCodeSubmit() {
        String code = managerCodeField.getText();
        if (MANAGER_CODE.equals(code)) {
            // Hide Manager code pane and continue to sign up
            managerCodePane.setVisible(false);
            System.out.println("Manager Code entered succesfully, proceeding to signup...");
            SceneManager.switchScene("signup-view.fxml");
        }
        else {
            // Alert code was incorrect
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Code");
            alert.setHeaderText(null);
            alert.setContentText("The manager code is incorrect. Please try again");
            alert.showAndWait();
        }
    }

    @FXML
    private void goToLogin() {
        SceneManager.switchScene("login-view.fxml");
    }
}
