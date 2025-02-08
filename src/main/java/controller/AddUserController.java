package controller;
//DONE
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

    @FXML
    public void initialize() {
        roleToggleGroup = new ToggleGroup();
        staffRadioButton.setToggleGroup(roleToggleGroup);
        managerRadioButton.setToggleGroup(roleToggleGroup);
    }

    @FXML
    private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }

    @FXML
    private void confirmUser() {
        try {
            boolean success = UserService.registerUser(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    usernameField.getText().trim(),
                    passwordField.getText().trim(),
                    confirmPasswordField.getText().trim(),
                    staffRadioButton.isSelected() ? UserRole.STAFF :
                            managerRadioButton.isSelected() ? UserRole.MANAGER : null
            );

            if (success) {
                SceneManager.switchScene("login-view.fxml");
            }

        } catch (SQLException e) {
            AlertUtils.showAlert("Error", "Database error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}