package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Client {
	private int clientId;
	private String clientName;
	private String contactInfo;
	private List<Booking> bookings;
	private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);


	// CONSTRUCTORS
	public Client(int clientId, String clientName) {
		this.clientId = clientId;
		this.clientName = clientName;
		bookings = new ArrayList<Booking>();
	}

	public Client(int clientId, String clientName, String contactInfo) {
		this.clientId = clientId;
		this.clientName = clientName;
		this.contactInfo = contactInfo;
		bookings = new ArrayList<>();
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

	// Calculate commission rate dynamically
	public double getCommissionRate() {
		long confirmedJobs = bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.count();

		return (confirmedJobs > 1) ? 0.09 : 0.10;
	}

	public double getClientTotalHire() {
		return bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.mapToDouble(Booking::getBookingHirePrice)
				.sum();
	}
	// Calculate total commission for this client across all bookings
	public double getTotalCommission() {
		return bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.mapToDouble(Booking::getBookingEventCommission)
				.sum();
	}

	public double getClientBookingTotal() {
		return bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.mapToDouble(Booking::getBookingTotal)
				.sum();
	}



	// Count confirmed bookings for reporting purposes
	public long getConfirmedJobCount() {
		return bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.count();
	}

	// Confirmed job count
	public IntegerProperty confirmedJobCountProperty() {
		return new SimpleIntegerProperty((int) getConfirmedJobCount());
	}

	// Client Total Hire Property
	public StringProperty getClientTotalHireProperty() {
		return new SimpleStringProperty(currencyFormatter.format(getClientTotalHire()));
	}

	// Client Commission Property
	public StringProperty getTotalCommissionProperty() {
		return new SimpleStringProperty(currencyFormatter.format(getTotalCommission()));
	}

	// Client Booking Total Property
	public StringProperty getClientBookingTotalProperty() {
		return new SimpleStringProperty(currencyFormatter.format(getClientBookingTotal()));
	}


	// SETTERS
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

	public List<Booking> getBookings() {
		return bookings;
	}

	public void addBooking(Booking booking) {
		if (this.bookings == null) {
			this.bookings = new ArrayList<>();
		}
		this.bookings.add(booking);
	}



}

//	public double getTotalCommission() {
//		return bookings == null ? 0 : bookings.stream()
//				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
//				.mapToDouble(booking -> booking.getHirePrice() * getCommissionRate())
//				.sum();
//	}

//	// Formatted Values for UI Display
//	public String getFormattedClientEventSpend() {
//		return formatCurrency(getTotalEventSpend());
//	}
//
//	public String getFormattedClientCommission() {
//		return formatCurrency(getTotalCommission());
//	}
//
//	public String getFormattedTotalClientSpend() {
//		return formatCurrency(getTotalClientSpend());
//	}

//	// HELPER METHOD: Format currency
//	private String formatCurrency(double amount) {
//		return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
//	}
//package model;
//
//import java.text.NumberFormat;
//import java.util.List;
//import java.util.Locale;
//
//public class Client {
//	private int clientId;
//	private String clientName;
//	private String contactInfo;
//	private List<Booking> bookings;
//
//	public Client(int clientId, String clientName) {
//		this.clientId = clientId;
//		this.clientName = clientName;
//	}
//
//	public Client(int clientId, String clientName, String contactInfo) {
//		this.clientId = clientId;
//		this.clientName = clientName;
//		this.contactInfo = contactInfo;
//	}
//
//	// GETTERS
//	public int getClientId() {
//		return clientId;
//	}
//
//	public String getClientName() {
//		return clientName;
//	}
//
//	public String getContactInfo() {
//		return contactInfo;
//	}
//
//	public int getTotalJobs() {
//		return (int) bookings.stream()
//				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
//				.count();
//	}
//
//	public double getTotalEventSpend() {
//		return bookings.stream()
//				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
//				.mapToDouble(Booking::getHirePrice)
//				.sum();
//	}
//
//	public double getTotalClientSpend() {
//		return getTotalEventSpend() + getTotalCommission();
//	}
//
//	public double getCommissionRate() {
//		int confirmedBookings = (int) bookings.stream()
//				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
//				.count();
//
//		// Example commission tiers
//		if (confirmedBookings > 1) {
//			return 0.09;  // 9% commission for more than 1 confirmed bookings
//		} else {
//			return 0.10;  // 10% commission for 1 confirmed bookings
//		}
//	}
//
//	public double getTotalCommission() {
//		return bookings.stream()
//				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
//				.mapToDouble(Booking::getCommission)
//				.sum();
//	}
//
//	public String getFormattedClientEventSpend() {
//		return NumberFormat.getCurrencyInstance(Locale.US).format(getTotalEventSpend());
//	}
//
//	public String getFormattedClientCommission() {
//		return NumberFormat.getCurrencyInstance(Locale.US).format(getTotalCommission());
//	}
//
//	public String getFormattedTotalClientSpend() {
//		return NumberFormat.getCurrencyInstance(Locale.US).format(getTotalClientSpend());
//	}
//	public void setContactInfo(String contactInfo) {
//		this.contactInfo = contactInfo;
//	}


//	private int totalJobs;
//	private double totalAmountSpent;
//	private double totalCommission;
//
//	public int getTotalJobs() { return totalJobs; }
//	public void setTotalJobs(int totalJobs) { this.totalJobs = totalJobs; }
//
//	public double getTotalAmountSpent() { return totalAmountSpent; }
//	public void setTotalAmountSpent(double totalAmountSpent) { this.totalAmountSpent = totalAmountSpent; }
//
//	public double getTotalCommission() { return totalCommission; }
//	public void setTotalCommission(double totalCommission) { this.totalCommission = totalCommission; }


//
//
//	@Override
//	public String toString() {
//		return "Client Id: " + clientId +
//				", Client Name: " + clientName +
//				", Total Jobs: " + totalJobs +
//				", Total Amount Spent: " + totalAmountSpent +
//				", Total Commission: " + totalCommission;
//	}
//}



//	public Client(int clientId, String clientName, String contactInfo) {
//		this.clientId = clientId;
//		this.clientName = clientName;
//		this.contactInfo = contactInfo;
//		this.totalJobs = 0;
//		this.totalAmountSpent = 0.0;
//		this.totalCommission = 0.0;
//	}

//	public Client(int clientId, String clientName, int totalJobs, double totalCommission, double totalAmountSpent) {
//		this.clientId = clientId;
//		this.clientName = clientName;
//		this.totalJobs = totalJobs;
//		this.totalCommission = totalCommission;
//		this.totalAmountSpent = totalAmountSpent;
//	}

//	public void setTotalJobs(int totalJobs) {
//		this.totalJobs = totalJobs;
//	}
//
//	public void setTotalAmountSpent(double totalAmountSpent) {
//		this.totalAmountSpent = totalAmountSpent;
//	}
//
//	public void setTotalCommission(double totalCommission) {
//		this.totalCommission = totalCommission;
//	}

//
//
//	// METHOD TO UPDATE TOTAL COMMISSION
//	public void updateCommission() {
//		this.totalCommission = calculateCommission();
//	}

//	public int getTotalJobs() {
//		return totalJobs;
//	}

//	public double getTotalAmountSpent() {
//		return totalAmountSpent;
//	}
//
//	public double getTotalCommission() {
//		return totalCommission;
//	}

//	public double getTotalEventSpend() {
//		return totalAmountSpent;  // Replace with your logic
//	}
//
//	public double getClientCommission() {
//		this.totalCommission = calculateCommission();
//		return totalCommission;
//	}
//
//	public double getTotalClientSpend() {
//		this.totalCommission = calculateCommission();
//		return totalAmountSpent + totalCommission;  // Replace with your logic
//	}

//	// Return 0 if bookings list is null
//	public int getTotalJobs() {
//		return bookings == null ? 0 : (int) bookings.stream()
//				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
//				.count();
//	}
//
//	public double getTotalEventSpend() {
//		return bookings == null ? 0 : bookings.stream()
//				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
//				.mapToDouble(Booking::getHirePrice)
//				.sum();
//	}
//
//	public double getClientEventTotal() {
//		return getTotalEventSpend() + getTotalCommission();
//	}
//
//	public double getClientTotalAmountSpent() {
//		return bookings.stream()
//				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
//				.mapToDouble(Booking::getHirePrice)
//				.sum();
//	}
//
//	public double getClientTotalCommission() {
//		return bookings.stream()
//				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
//				.mapToDouble(Booking::getCommission)
//				.sum();
//	}