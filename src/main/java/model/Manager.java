package model;

public class Manager extends Staff {

	/**
	 * @param firstName
	 * @param lastName
	 * @param username
	 * @param password
	 */
	public Manager(int userId, String firstName, String lastName, String username, String password) {
        super(userId, firstName, lastName, username, password, UserRole.MANAGER);
	}

	public Manager(int userId) {
		super(userId);
	}

	/**
	 * 
	 * @param staff
	 */
	public void addStaff(Staff staff) {
		// TODO - implement Manager.addStaff
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param staff
	 */
	public void updateStaff(Staff staff) {
		// TODO - implement Manager.updateStaff
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param staffId
	 */
	public void deleteStaff(int staffId) {
		// TODO - implement Manager.deleteStaff
		throw new UnsupportedOperationException();
	}

	public void performTransactionBackup() {
		// TODO - implement Manager.performTransactionBackup
		throw new UnsupportedOperationException();
	}

	public void restoreTransactionBackup() {
		// TODO - implement Manager.restoreTransactionBackup
		throw new UnsupportedOperationException();
	}

	public void performMasterBackup() {
		// TODO - implement Manager.performMasterBackup
		throw new UnsupportedOperationException();
	}

	public void restoreMasterBackup() {
		// TODO - implement Manager.restoreMasterBackup
		throw new UnsupportedOperationException();
	}

	public void viewReport() {
		// TODO - implement Manager.viewReport
		throw new UnsupportedOperationException();
	}

}