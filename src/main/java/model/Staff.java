package model;

import java.io.Serializable;

/**
 * Represents a staff user.
 * <p>
 * The {@code Staff} class extends the {@link User} class to represent a user with staff-level privileges.
 * It implements {@code Serializable} to support object serialization.
 * </p>
 *
 * @author	Bodene Downie
 * @version 1.0
 */
public class Staff extends User implements Serializable {

	/**
	 * Constructs a {@code Staff} object with the specified details.
	 *
	 * @param userId    the unique identifier for the staff member
	 * @param firstName the first name of the staff member
	 * @param lastName  the last name of the staff member
	 * @param username  the username for the staff member
	 * @param password  the password for the staff member
	 */
	public Staff(int userId, String firstName, String lastName, String username, String password) {
		// Call the superclass constructor with the role set to UserRole.STAFF.
		super(userId, firstName, lastName, username, password, UserRole.STAFF);
	}

	/**
	 * Returns the type of the user as a String.
	 * <p>
	 * This method overrides the abstract {@code getUserType()} method in the {@link User} class.
	 * </p>
	 *
	 * @return "Staff" as a String
	 */
	@Override
	public String getUserType() {
		return "Staff";
	}

	/**
	 * Returns a string representation of the staff member.
	 * <p>
	 * The string includes the user's ID, first name, last name, username, and role.
	 * </p>
	 *
	 * @return a formatted string representing the staff member's details
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