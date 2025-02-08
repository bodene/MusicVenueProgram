package service;

import model.Manager;
import model.User;

public class SessionManager {
    private static SessionManager instance;
    private static User currentUser;

    // PRIVATE CONSTRUCTOR (SINGLETON PATTERN)
    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // GETTER
    public static User getCurrentUser() {
        return currentUser;
    }

    // SETTER
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // GET USER ROLE(MANAGER OR STAFF)
    public String getUserRole() {
        if (currentUser instanceof Manager) {
            return "manager";
        }
        return "staff";
    }

    // CHECK IF THE CURRENT USER IS MANAGER
    public boolean isManager() {
        return currentUser instanceof Manager;
    }

    // CLEAR SESSION WHEN LOGGING OUT
    public void clearSession() {
        this.currentUser = null;
    }
}
