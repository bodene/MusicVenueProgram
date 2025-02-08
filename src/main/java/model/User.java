package model;
//done
public abstract class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private UserRole role;

    // CONSTRUCTOR
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

    // ROLE CHECK MANAGER
    public boolean isManager() { return role == UserRole.MANAGER; }
    public boolean isStaff() { return role == UserRole.STAFF; }

    public abstract String getUserType();
}