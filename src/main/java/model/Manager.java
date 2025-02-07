package model;
//done
public class Manager extends User {

	public Manager(int userId, String firstName, String lastName, String username, String password) {
		super(userId, firstName, lastName, username, password, UserRole.MANAGER);
	}
}