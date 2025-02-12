package model;

import java.io.Serializable;

/**
 * Represents an abstract user in the system.
 * <p>
 * The {@code User} class is an abstract base class that encapsulates common attributes and behaviours for
 * different types of users (such as managers and staff). It implements {@code Serializable} to allow user objects
 * to be serialized.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public abstract class User implements Serializable {
    private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private UserRole role;

    /**
     * Constructs a new {@code User} with the specified details.
     *
     * @param userId    the unique identifier for the user
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param username  the username for login
     * @param password  the password for authentication
     * @param role      the role of the user (e.g., {@link UserRole#MANAGER} or {@link UserRole#STAFF})
     */
    public User(int userId, String firstName, String lastName, String username, String password, UserRole role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // GETTERS

    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }

    // SETTERS
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(UserRole role) { this.role = role; }

    // ROLE CHECKS

    /**
     * Checks if the user is a manager.
     *
     * @return {@code true} if the user's role is {@link UserRole#MANAGER}; {@code false} otherwise
     */
    public boolean isManager() {
        return role == UserRole.MANAGER;
    }

    /**
     * Checks if the user is a staff member.
     *
     * @return {@code true} if the user's role is {@link UserRole#STAFF}; {@code false} otherwise
     */
    public boolean isStaff() {
        return role == UserRole.STAFF;
    }
    
    /**
     * Returns the type of the user.
     * <p>
     * Subclasses must implement this method to return a descriptive type (e.g., "Manager" or "Staff").
     * </p>
     *
     * @return a String representing the user type
     */
    public abstract String getUserType();
}