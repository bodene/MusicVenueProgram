package controller;

import service.SceneManager;
import javafx.fxml.FXML;

public class UserEditController {
    @FXML
    private void goToMain() {
        SceneManager.switchScene("main-view.fxml");
    }
}