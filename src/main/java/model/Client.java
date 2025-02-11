package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Client implements Serializable {
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

	// CONSTRUCTOR FOR BACKUP DATABASE
	public Client(int clientId) {
		this.clientId = clientId;
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

	@Override
	public String toString() {
		return 	"Client Id: " + clientId +
				", Client Name: " + clientName +
				", Contact Info: " + (contactInfo != null ? contactInfo : "N/A") +
				", Confirmed Jobs: " + getConfirmedJobCount() +
				", Client Total Hire: " + currencyFormatter.format(getClientTotalHire()) +
				", Total Commission: " + currencyFormatter.format(getTotalCommission()) +
				", Client Booking Total: " + currencyFormatter.format(getClientBookingTotal()) +
				", Bookings: " + (bookings != null ? bookings.size() + " bookings" : "No bookings");
	}
}