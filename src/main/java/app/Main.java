package app;

import service.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import dao.DatabaseHandler;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        // Initialize database once at startup
        DatabaseHandler.initialiseDatabase();

        SceneManager.setStage(stage);
        SceneManager.switchScene("main-view.fxml"); // Load main view first
    }

    public static void main(String[] args) {
        launch();
    }
}
