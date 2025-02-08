package controller;
//DONE
import service.AuthService;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import util.AlertUtils;

public class MainController {

    @FXML private AnchorPane managerCodePane;
    @FXML private PasswordField managerCodeField;

    // SHOW MANAGE CODE PANE & PROMPT FOR CODE
    @FXML
    private void goToAddNewUser() {
        managerCodePane.setVisible(true); // Show manager code pane
    }

    // VALIDATE MANAGER CODE
    @FXML
    private void handleManagerCodeSubmit() {
        String code = managerCodeField.getText().trim();

        if (AuthService.validateManagerCode(code)) {
            managerCodePane.setVisible(false);
            SceneManager.switchScene("add-user-view.fxml");
        } else {
            AlertUtils.showAlert("Invalid Code", "The manager code is incorrect. Please try again.", Alert.AlertType.ERROR);
        }
    }

    @FXML private void goToLogin() {
        SceneManager.switchScene("login-view.fxml");
    }
}