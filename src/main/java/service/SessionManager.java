package service;

import model.Staff;
import model.Manager;
import model.User;

public class SessionManager {
    private static SessionManager instance;
    private static User currentUser;

    // Private constructor (Singleton pattern)
    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Get user role (Manager or Staff)
    public String getUserRole() {
        if (currentUser instanceof Manager) {
            return "manager";
        }
        return "staff";
    }

    // Check if the current user is a Manager
    public boolean isManager() {
        return currentUser instanceof Manager;
    }

    // Clear session when logging out
    public void clearSession() {
        this.currentUser = null;
    }
}
