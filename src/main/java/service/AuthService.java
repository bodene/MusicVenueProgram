package service;

public class AuthService {
    private static final String MANAGER_CODE = "909";

    public static boolean validateManagerCode(String inputCode) {
        return MANAGER_CODE.equals(inputCode);
    }
}