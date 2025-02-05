package service;

import controller.UserEditController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Staff;

import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
        primaryStage.setMaximized(true); // Ensure full-screen mode
    }

    public static void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/" + fxmlFile));
            Parent root = loader.load();

            // Preserve the stage size
            double width = primaryStage.getWidth();
            double height = primaryStage.getHeight();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(SceneManager.class.getResource("/css/styles.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML file: " + fxmlFile);
        }
    }

    // Overloaded method to pass Staff object
    public static void switchScene(String fxmlFile, Staff staff) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/" + fxmlFile));
            Parent root = loader.load();

            // Preserve the stage size
            double width = primaryStage.getWidth();
            double height = primaryStage.getHeight();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(SceneManager.class.getResource("/css/styles.css").toExternalForm());

            // Get controller and pass the selected user details
            UserEditController controller = loader.getController();
            controller.setUserDetails(staff); // Pass user data to controller

            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
