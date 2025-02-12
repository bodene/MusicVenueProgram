package util;

import javafx.scene.control.Alert;


/**
 * Utility class for validating numeric input.
 * <p>
 * The {@code InputValidationUtils} class provides static helper methods to validate that input
 * strings can be parsed as integers or doubles. If the input is invalid, an alert is displayed
 * with a provided error message, and a default error value (-1) is returned.
 * </p>
 * <p>
 * This class follows the utility class pattern and should not be instantiated.
 * </p>
 *
 * @author Bodene Downie
 * @version 1.0
 */
public class InputValidationUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private InputValidationUtils() {}

    /**
     * Validates the provided input string as an integer.
     * <p>
     * Attempts to parse the input string to an integer. If the parsing is successful, the integer value
     * is returned. If a {@code NumberFormatException} is thrown, an error alert is displayed with the provided
     * error message and the method returns -1.
     * </p>
     *
     * @param input        the string to validate as an integer
     * @param errorMessage the error message to display if parsing fails
     * @return the parsed integer value if valid; otherwise, -1
     */
    public static int validateInteger(String input, String errorMessage) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            AlertUtils.showAlert("Error", errorMessage, Alert.AlertType.ERROR);
            return -1;
        }
    }

    /**
     * Validates the provided input string as a double.
     * <p>
     * Attempts to parse the input string to a double and ensures that the parsed value is greater than 0.0.
     * If successful, the parsed double value is returned. Otherwise, an error alert is displayed with the provided
     * error message and the method returns -1.
     * </p>
     *
     * @param input        the string to validate as a double
     * @param errorMessage the error message to display if parsing fails or the value is not greater than 0.0
     * @return the parsed double value if valid and greater than 0.0; otherwise, -1
     */
    public static double validateDouble(String input, String errorMessage) {
        try {
            double value = Double.parseDouble(input);
            if (value <= 0.0) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException e) {
            AlertUtils.showAlert("Error", errorMessage, Alert.AlertType.ERROR);
            return -1;
        }
    }
}