package model;

public class Client {

	private int clientId;
	private String name;
	private String contactInfo;

	/**
	 * 
	 * @param clientId
	 * @param name
	 * @param contactInfo
	 */
	public Client(int clientId, String name, String contactInfo) {
		// TODO - implement Client.Client
		throw new UnsupportedOperationException();
	}

	public int getClientId() {
		return this.clientId;
	}

	/**
	 * 
	 * @param clientId
	 */
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getContactInfo() {
		return this.contactInfo;
	}

	/**
	 * 
	 * @param contactInfo
	 */
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String toString() {
		// TODO - implement Client.toString
		throw new UnsupportedOperationException();
	}

}