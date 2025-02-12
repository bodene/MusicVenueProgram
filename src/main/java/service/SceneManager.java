package service;

import controller.UserEditController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;
import java.io.IOException;

/**
 * Manages scene transitions within the application.
 * <p>
 * The {@code SceneManager} class provides static methods to set the primary stage and switch scenes
 * by loading FXML files. It supports scene switching with or without passing user details to the target controller.
 * </p>
 *
 * @author Bodene Downie
 * @version 1.0
 */
public class SceneManager {

    private SceneManager() {}

    /**
     * The primary stage of the application.
     */
    private static Stage primaryStage;

    /**
     * Sets the primary stage for the application.
     * <p>
     * This method assigns the provided {@code Stage} as the primary stage and maximises it to full-screen mode.
     * </p>
     *
     * @param stage the primary {@code Stage} to set
     */
    public static void setStage(Stage stage) {
        primaryStage = stage;
        primaryStage.setMaximized(true); // Ensure full-screen mode
    }

    /**
     * Switches the current scene to the one defined by the specified FXML file.
     * <p>
     * The method loads the FXML file from the {@code /view/} directory, preserves the current stage size,
     * applies the default stylesheet, and sets the new scene on the primary stage.
     * </p>
     *
     * @param fxmlFile the name of the FXML file to load (e.g., "main-view.fxml")
     */
    public static void switchScene(String fxmlFile) {
        try {
            // Load the FXML file from the /view/ directory.
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/" + fxmlFile));
            Parent root = loader.load();

            // Preserve the stage size
            double width = primaryStage.getWidth();
            double height = primaryStage.getHeight();

            // Create a new Scene with the loaded root and apply the stylesheet.
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(SceneManager.class.getResource("/css/styles.css").toExternalForm());

            // Set and show the new scene on the primary stage.
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML file: " + fxmlFile);
        }
    }

    /**
     * Switches the current scene to the one defined by the specified FXML file and passes a user object.
     * <p>
     * This overloaded method is used when you need to pass a {@code User} object (typically for editing user details)
     * to the target controller. After loading the FXML file, it retrieves the controller, calls its
     * {@code setUserDetails()} method, and then sets the new scene.
     * </p>
     *
     * @param fxmlFile the name of the FXML file to load (e.g., "user-profile-edit-view.fxml")
     * @param staff    the {@code User} object to pass to the controller
     */
    public static void switchScene(String fxmlFile, User staff) {
        try {
            // Load the FXML file from the /view/ directory.
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/view/" + fxmlFile));
            Parent root = loader.load();

            // Preserve the current stage dimensions.
            double width = primaryStage.getWidth();
            double height = primaryStage.getHeight();

            // Create a new Scene with the loaded root and apply the stylesheet.
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(SceneManager.class.getResource("/css/styles.css").toExternalForm());

            // Retrieve the controller from the loader and pass the user details.
            UserEditController controller = loader.getController();
            controller.setUserDetails(staff);

            // Set and show the new scene on the primary stage.
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}