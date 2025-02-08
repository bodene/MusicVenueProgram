package model;
//done
public class Manager extends User {

	public Manager(int userId, String firstName, String lastName, String username, String password) {
		super(userId, firstName, lastName, username, password, UserRole.MANAGER);
	}

	@Override
	public String getUserType() {
		return "Manager";
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