package controller;

import service.SceneManager;
import javafx.fxml.FXML;

public class LoginController {
    @FXML
    private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }

    @FXML
    private void loginButton() {
        System.out.println("User Logged in! (Add logic here)");
        // You can later add validation & database storage
    }
}
