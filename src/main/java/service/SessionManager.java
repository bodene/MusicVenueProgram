package service;

import model.Manager;
import model.User;

/**
 * Manages the current user session.
 * <p>
 * The {@code SessionManager} class implements the Singleton pattern to provide a single point
 * of access to the current user session. It stores the currently logged-in user and provides methods
 * to retrieve the user's role and clear the session upon logout.
 * </p>
 *
 * @author Bodene Downie
 * @version 1.0
 */
public class SessionManager {

    /**
     * The singleton instance of the SessionManager.
     */
    private static SessionManager instance;

    /**
     * The currently logged-in user.
     */
    private static User currentUser;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private SessionManager() {}

    /**
     * Returns the singleton instance of the SessionManager.
     *
     * @return the single {@code SessionManager} instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return the current {@code User} object, or {@code null} if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user for the session.
     *
     * @param user the {@code User} object to set as the current user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }


    /**
     * Returns the role of the current user as a String.
     * <p>
     * If the current user is an instance of {@code Manager}, "manager" is returned;
     * otherwise, "staff" is returned.
     * </p>
     *
     * @return a {@code String} representing the user role ("manager" or "staff")
     */
    public String getUserRole() {
        if (currentUser instanceof Manager) {
            return "manager";
        }
        return "staff";
    }

    /**
     * Checks whether the current user is a manager.
     *
     * @return {@code true} if the current user is an instance of {@code Manager}; {@code false} otherwise
     */
    public boolean isManager() {
        return currentUser instanceof Manager;
    }

    /**
     * Clears the current session by setting the current user to {@code null}.
     * <p>
     * This method should be called when logging out.
     * </p>
     */
    public void clearSession() {
        this.currentUser = null;
    }
}