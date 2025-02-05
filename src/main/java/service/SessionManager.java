package service;

import model.Staff;
import model.Manager;

public class SessionManager {
    private static SessionManager instance;
    private static Staff currentUser; // Can be Staff or Manager

    // Private constructor (Singleton pattern)
    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Set the current user (Staff or Manager)
    public void setCurrentUser(Staff user) {
        this.currentUser = user;
    }

    // Retrieve the current user
    public static Staff getCurrentUser() {
        return currentUser;
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
