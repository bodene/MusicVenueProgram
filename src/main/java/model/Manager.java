package model;

import java.io.Serializable;

/**
 * Represents a Manager user.
 * <p>
 * The {@code Manager} class extends the {@link User} class to represent a user with manager-level
 * privileges. It implements {@code Serializable} to allow manager objects to be serialized.
 * </p>
 *
 * @author	Bodene Downie
 * @version 1.0
 */
public class Manager extends User implements Serializable {


	/**
	 * Constructs a {@code Manager} with the specified details.
	 *
	 * @param userId    the unique identifier for the manager
	 * @param firstName the first name of the manager
	 * @param lastName  the last name of the manager
	 * @param username  the username for the manager
	 * @param password  the password for the manager
	 */
	public Manager(int userId, String firstName, String lastName, String username, String password) {
		// Calls the superclass constructor with the role set to UserRole.MANAGER.
		super(userId, firstName, lastName, username, password, UserRole.MANAGER);
	}

	/**
	 * Returns the type of the user as a String.
	 * <p>
	 * This method overrides the abstract {@code getUserType()} method from the {@link User} class.
	 * </p>
	 *
	 * @return "Manager" as a String
	 */
	@Override
	public String getUserType() {
		return "Manager";
	}

	/**
	 * Returns a string representation of the manager.
	 *
	 * @return a formatted string containing manager details
	 */
	@Override
	public String toString() {
		return "ID: " + getUserId() +
				"\nFirst Name: " + getFirstName() +
				"\nLast Name: " + getLastName() +
				"\nUsername: " + getUsername() +
				"\nRole: " + getRole();
	}
}