package app;

import service.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        SceneManager.switchScene("add-venue.fxml"); // Load main view first
    }

    public static void main(String[] args) {
        launch();
    }
}
