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


/**
 * Represents a client who books events at a venue.
 * <p>
 * The {@code Client} class stores client details such as client ID, client name, and contact information.
 * It also maintains a list of {@link Booking} objects associated with the client and provides methods for
 * calculating commission rates, total hire amounts, commissions, and overall booking totals.
 * </p>
 * <p>
 * This class implements {@code Serializable} to support object serialization.
 * </p>
 *
 * @author	Bodene Downie
 * @version 1.0
 */
public class Client implements Serializable {
	private int clientId;
	private String clientName;
	private String contactInfo;
	private List<Booking> bookings;
	private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

	// CONSTRUCTORS

	/**
	 * Constructs a Client with the specified ID and name.
	 *
	 * @param clientId   the unique client identifier
	 * @param clientName the name of the client
	 */
	public Client(int clientId, String clientName) {
		this.clientId = clientId;
		this.clientName = clientName;
		bookings = new ArrayList<>();
	}

	/**
	 * Constructs a Client with the specified ID, name, and contact information.
	 *
	 * @param clientId    the unique client identifier
	 * @param clientName  the name of the client
	 * @param contactInfo the contact information for the client
	 */
	public Client(int clientId, String clientName, String contactInfo) {
		this.clientId = clientId;
		this.clientName = clientName;
		this.contactInfo = contactInfo;
		bookings = new ArrayList<>();
	}

	/**
	 * Constructs a Client for backup purposes with only the client ID.
	 *
	 * @param clientId the unique client identifier
	 */
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

	public List<Booking> getBookings() {
		return bookings;
	}

	/**
	 * Calculates the commission rate for the client based on confirmed bookings.
	 * <p>
	 * If the client has more than one confirmed booking, a commission rate of 9% is applied; otherwise, 10%.
	 * </p>
	 *
	 * @return the commission rate as a double
	 */
	public double getCommissionRate() {
		long confirmedJobs = bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.count();

		return (confirmedJobs > 1) ? 0.09 : 0.10;
	}

	/**
	 * Calculates the total hire cost for the client from confirmed bookings.
	 *
	 * @return the total hire amount as a double
	 */
	public double getClientTotalHire() {
		return bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.mapToDouble(Booking::getBookingHirePrice)
				.sum();
	}

	/**
	 * Calculates the total commission earned for the client across all confirmed bookings.
	 *
	 * @return the total commission as a double
	 */
	public double getTotalCommission() {
		return bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.mapToDouble(Booking::getBookingEventCommission)
				.sum();
	}

	/**
	 * Calculates the total booking cost (hire price + commission) for the client from confirmed bookings.
	 *
	 * @return the total booking cost as a double
	 */
	public double getClientBookingTotal() {
		return bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.mapToDouble(Booking::getBookingTotal)
				.sum();
	}

	/**
	 * Counts the number of confirmed bookings for the client.
	 *
	 * @return the count of confirmed bookings as a long
	 */
	public long getConfirmedJobCount() {
		return bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.count();
	}

	/**
	 * Returns an {@code IntegerProperty} representing the confirmed job count.
	 *
	 * @return the confirmed job count as an {@code IntegerProperty}
	 */
	public IntegerProperty confirmedJobCountProperty() {
		return new SimpleIntegerProperty((int) getConfirmedJobCount());
	}

	/**
	 * Returns a {@code StringProperty} representing the client's total hire amount formatted as currency.
	 *
	 * @return the formatted total hire amount as a {@code StringProperty}
	 */
	public StringProperty getClientTotalHireProperty() {
		return new SimpleStringProperty(currencyFormatter.format(getClientTotalHire()));
	}

	/**
	 * Returns a {@code StringProperty} representing the total commission formatted as currency.
	 *
	 * @return the formatted total commission as a {@code StringProperty}
	 */
	public StringProperty getTotalCommissionProperty() {
		return new SimpleStringProperty(currencyFormatter.format(getTotalCommission()));
	}

	/**
	 * Returns a {@code StringProperty} representing the total booking cost formatted as currency.
	 *
	 * @return the formatted total booking cost as a {@code StringProperty}
	 */
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

	/**
	 * Adds a booking to the client's list of bookings.
	 *
	 * @param booking the {@code Booking} object to add
	 */
	public void addBooking(Booking booking) {
		if (this.bookings == null) {
			this.bookings = new ArrayList<>();
		}
		this.bookings.add(booking);
	}

	/**
	 * Returns a string representation of the client.
	 *
	 * @return a {@code String} representing the client details and booking summaries
	 */
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