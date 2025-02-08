package service;
//DONE
public class AuthService {
    private static final String MANAGER_CODE = "909";

    // VALIDATE MANAGER CODE
    public static boolean validateManagerCode(String inputCode) {
        return MANAGER_CODE.equals(inputCode);
    }
}