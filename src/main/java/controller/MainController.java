package controller;

import service.AuthService;
import service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import util.AlertUtils;


/**
 * Controller class for the main view that handles navigation and manager code validation.
 * <p>
 * This controller manages the display of a manager code pane for privileged actions, such as adding a new user.
 * It validates the entered manager code via the {@link AuthService} and navigates to the appropriate view based on
 * the validation result. Additionally, it provides navigation back to the login view.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class MainController {

    /** Pane containing the manager code prompt. */
    @FXML private AnchorPane managerCodePane;

    /** PasswordField for entering the manager code. */
    @FXML private PasswordField managerCodeField;


    /**
     * Displays the manager code pane.
     * <p>
     * This method is invoked when the user selects the option to add a new user. It makes the manager code pane visible
     * so that the user can enter the manager code for further authentication.
     * </p>
     */
    @FXML
    private void goToAddNewUser() {
        // Show the pane where the manager can enter the code.
        managerCodePane.setVisible(true);
    }

    /**
     * Validates the manager code and navigates to the add user view if successful.
     * <p>
     * This method retrieves the code entered by the manager from the {@code managerCodeField}, trims any whitespace,
     * and calls {@link AuthService#validateManagerCode(String)} to verify its correctness. If the code is valid,
     * the manager code pane is hidden and the scene is switched to the add user view. Otherwise, an error alert is displayed.
     * </p>
     */
    @FXML
    private void handleManagerCodeSubmit() {

        // Retrieve and trim the manager code from the input field.
        String code = managerCodeField.getText().trim();

        // Validate the manager code using the AuthService.
        if (AuthService.validateManagerCode(code)) {
            // If the code is valid, hide the manager code pane and navigate to the add user view.
            managerCodePane.setVisible(false);
            SceneManager.switchScene("add-user-view.fxml");
        } else {
            // If the code is invalid, display an error alert.
            AlertUtils.showAlert("Invalid Code", "The manager code is incorrect. Please try again.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Navigates back to the login view.
     * <p>
     * This method is called when the user chooses to go back to the login screen.
     * It switches the current scene to the login view.
     * </p>
     */
    @FXML private void goToLogin() {
        SceneManager.switchScene("login-view.fxml");
    }

}