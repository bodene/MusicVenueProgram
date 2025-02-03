package controller;

import service.SceneManager;
import javafx.fxml.FXML;

public class SignupController {
    @FXML
    private void goToMain() {

        SceneManager.switchScene("main-view.fxml");
    }

    @FXML
    private void confirmUser() {
        System.out.println("User confirmed! (Add logic here)");
        // You can later add validation & database storage
    }
}
