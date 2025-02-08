package util;

import javafx.scene.control.Alert;

public class InputValidationUtils {

    // VALIDATE INTEGERS
    public static int validateInteger(String input, String errorMessage) {
        try {
            int value = Integer.parseInt(input);
            return value;
        } catch (NumberFormatException e) {
            AlertUtils.showAlert("Error", errorMessage, Alert.AlertType.ERROR);
            return -1;
        }
    }

    // VALIDATE DOUBLES
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
