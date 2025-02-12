package service;

/**
 * Provides authentication-related services.
 * <p>
 * The {@code AuthService} class contains utility methods for validating authentication information.
 * Currently, it provides a method to validate a manager code.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class AuthService {

    /**
     * The hard-coded manager code used for authentication.
     */
    private static final String MANAGER_CODE = "909";

    private AuthService() {}

    /**
     * Validates the provided manager code.
     * <p>
     * This method compares the input code with the pre-defined manager code.
     * It returns {@code true} if the codes match, or {@code false} otherwise.
     * </p>
     *
     * @param inputCode the manager code provided by the user
     * @return {@code true} if the provided code matches the manager code; {@code false} otherwise
     */
    public static boolean validateManagerCode(String inputCode) {
        return MANAGER_CODE.equals(inputCode);
    }
}