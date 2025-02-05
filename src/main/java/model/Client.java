package model;

public class Client {

	private int clientId;
	private static int clientIdCounter = 0;
	private String clientName;
	private String contactInfo;

	/**
	 * @param clientName
	 * @param contactInfo
	 */
	public Client(String clientName, String contactInfo) {
		this.clientId = ++clientIdCounter;
		this.clientName = clientName;
		this.contactInfo = contactInfo;
	}

	public int getClientId() {
		return this.clientId;
	}

	public String getClientName() {
		return this.clientName;
	}

	/**
	 * @param clientName
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getContactInfo() {
		return this.contactInfo;
	}

	/**
	 * @param contactInfo
	 */
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	@Override
	public String toString() {
		return "Client{" +
				"clientId=" + clientId +
				", clientName='" + clientName + '\'' +
				", contactInfo='" + contactInfo + '\'' +
				'}';
	}
}