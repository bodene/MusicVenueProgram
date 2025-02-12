package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import java.util.Optional;


/**
 * Utility class for displaying alerts and confirmation dialogs.
 * <p>
 * The {@code AlertUtils} class provides static methods for showing information, error, and confirmation dialogs
 * with a consistent style. It applies a custom stylesheet to all dialogs to ensure a unified look and feel.
 * </p>
 *
 * <p>
 * This class is not meant to be instantiated.
 * </p>
 *
 * @author Bodene Downie
 * @version 1.0
 */
public class AlertUtils {

    /**
     * The path to the custom CSS stylesheet used to style alert dialogs.
     */
    private static final String STYLESHEET_PATH = "/css/dialog-style.css";

    /**
     * Private constructor to prevent instantiation.
     */
    private AlertUtils() {}

    /**
     * Displays an information alert with the specified title, header, and content.
     * <p>
     * The dialog is styled using the custom stylesheet.
     * </p>
     *
     * @param title   the title of the alert dialog
     * @param header  the header text of the alert dialog
     * @param content the content text of the alert dialog
     */
    public static void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * Displays an alert with the specified title, message, and alert type.
     * <p>
     * The header is set to null, and the dialog is styled using the custom stylesheet.
     * </p>
     *
     * @param title   the title of the alert dialog
     * @param message the message content of the alert dialog
     * @param type    the type of the alert (e.g., INFORMATION, ERROR, WARNING)
     */
    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation dialog with the specified title and message.
     * <p>
     * The header is set to "Confirmation Required". The dialog is styled using the custom stylesheet.
     * </p>
     *
     * @param title   the title of the confirmation dialog
     * @param message the message content of the confirmation dialog
     * @return {@code true} if the user confirms (clicks OK), {@code false} otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("Confirmation Required");
        alert.setContentText(message);
        styleAlert(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Displays a confirmation dialog with the specified title, header, and content.
     * <p>
     * The dialog is styled using the custom stylesheet.
     * </p>
     *
     * @param title   the title of the confirmation dialog
     * @param header  the header text of the confirmation dialog
     * @param content the content text of the confirmation dialog
     * @return {@code true} if the user confirms (clicks OK), {@code false} otherwise
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleAlert(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Applies the custom stylesheet to the provided alert dialog.
     * <p>
     * The method retrieves the {@code DialogPane} from the alert, adds the stylesheet, and
     * applies a custom style class.
     * </p>
     *
     * @param alert the {@code Alert} to style
     */
    private static void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(AlertUtils.class.getResource(STYLESHEET_PATH).toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");  // Ensure you define this class in dialog-style.css
    }
}