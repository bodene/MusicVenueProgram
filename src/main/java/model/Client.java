package model;

public class Client {
	private int clientId;
	private String clientName;
	private String contactInfo;
	private int totalJobs;
	private double totalAmountSpent;
	private double totalCommission;

	public Client(int clientId, String clientName, String contactInfo) {
		this.clientId = clientId;
		this.clientName = clientName;
		this.contactInfo = contactInfo;
		this.totalJobs = 0;
		this.totalAmountSpent = 0.0;
		this.totalCommission = 0.0;
	}

	public Client(int clientId, String clientName, int totalJobs, double totalCommission, double totalAmountSpent) {
		this.clientId = clientId;
		this.clientName = clientName;
		this.totalJobs = totalJobs;
		this.totalCommission = totalCommission;
		this.totalAmountSpent = totalAmountSpent;
	}

	// GETTERS
	public int getClientId() {
		return clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public int getTotalJobs() {
		return totalJobs;
	}

	public double getTotalAmountSpent() {
		return totalAmountSpent;
	}

	public double getTotalCommission() {
		return totalCommission;
	}

	// SETTERS
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public void setTotalJobs(int totalJobs) {
		this.totalJobs = totalJobs;
	}

	public void setTotalAmountSpent(double totalAmountSpent) {
		this.totalAmountSpent = totalAmountSpent;
	}

	public void setTotalCommission(double totalCommission) {
		this.totalCommission = totalCommission;
	}

	// METHOD TO CALCULATE COMMISSION
	public double calculateCommission() {
		if (totalJobs > 1) {
			return totalAmountSpent * 0.09;  // 9% COMMISSION FOR MULTIPLE JOBS
		} else {
			return totalAmountSpent * 0.10;  // 10% COMMISSION FOR A SINGLE JOB
		}
	}

	// METHOD TO UPDATE TOTAL COMMISSION
	public void updateCommission() {
		this.totalCommission = calculateCommission();
	}

	@Override
	public String toString() {
		return "Client{" +
				"clientId=" + clientId +
				", clientName='" + clientName + '\'' +
				", totalJobs=" + totalJobs +
				", totalAmountSpent=" + totalAmountSpent +
				", totalCommission=" + totalCommission +
				'}';
	}
}
