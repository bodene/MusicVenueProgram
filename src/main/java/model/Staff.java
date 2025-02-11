package model;

import java.io.Serializable;

//done
public class Staff extends User implements Serializable {

	// Constructor
	public Staff(int userId, String firstName, String lastName, String username, String password) {
		super(userId, firstName, lastName, username, password, UserRole.STAFF);
	}

	@Override
	public String getUserType() {
		return "Staff";
	}

	@Override
	public String toString() {
		return "ID: " + getUserId() +
				"\nFirst Name: " + getFirstName() +
				"\nLast Name: " + getLastName() +
				"\nUsername: " + getUsername() +
				"\nRole: " + getRole();
	}
}