package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

import static util.NumberUtils.formatCurrency;

public class Booking {

	private int bookingId;
	private Event event;
	private Venue venue;
	private Client client;
	private LocalDate bookingDate;
	private BookingStatus status;
	private String bookedBy;

	/**
	 * Constructor for Booking
	 * @param bookingId
	 * @param event
	 * @param venue
	 * @param client
	 * @param bookingDate
	 */
	// CONSTRUCTORS
	public Booking(int bookingId, Event event, Venue venue, Client client, LocalDate bookingDate) {
		this.bookingId = bookingId;
		this.event = event;
		this.venue = venue;
		this.client = client;
		this.bookingDate = bookingDate;
		this.status = BookingStatus.CONFIRMED;
	}

	// CONSTRUCTOR FOR UI BASED OBJECT CREATION
	public Booking(int bookingId, int eventId, String eventName, LocalDate eventDate, LocalTime eventTime, int eventDuration, String eventArtist, int venueId, String venueName, double hirePrice, int clientId, String clientName, String bookingStatus, String bookedBy) {
		this.bookingId = bookingId;
		this.event = new Event(eventId, eventName, eventDate, eventTime, eventDuration, eventArtist);  // Event can be a simplified constructor
		this.venue = new Venue(venueId, venueName, hirePrice);
		this.client = new Client(clientId, clientName);
		this.status = parseBookingStatus(bookingStatus);
		this.bookedBy = bookedBy;
	}

	public Booking(int bookingId, String eventName, String venueName, String bookedBy) {
		this.bookingId = bookingId;
		this.event = new Event(eventName);
		this.venue = new Venue(venueName);
		this.bookedBy = bookedBy;
		this.status = BookingStatus.CONFIRMED;
	}

	// GETTERS
	public int getBookingId() {
		return bookingId;
	}

	public Event getEvent() {
		return event;
	}
	public Venue getVenue() {
		return venue;
	}

	public Client getClient() {
		return client;
	}

	public LocalDate getBookingDate() {
		return bookingDate;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public String getBookedBy() {
		return bookedBy;
	}

	// Calculate event cost dynamically based on venue's base price and event duration
	public double getBookingHirePrice() {
		if (venue == null || event == null) return 0;
		double basePrice = venue.getHirePricePerHour();
		int duration = event.getDuration();
		return basePrice * duration;
	}

	// Calculate commission dynamically based on the client’s commission rate
	public double getBookingEventCommission() {
		if (this.client == null) {
			return 0;
		}
		double eventCost = getBookingHirePrice();
		double commissionRate = client.getCommissionRate();
		return eventCost * commissionRate;
	}

	public double getBookingTotal() {
		return getBookingHirePrice() + getBookingEventCommission();
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	// HELPER METHOD - PARSE BOOKING STATUS
	private BookingStatus parseBookingStatus(String status) {
		try {
			return BookingStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			return BookingStatus.PENDING;  // Default status if parsing fails
		}
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	// Add this method for the status property
	public StringProperty getStatusProperty() {
		return new SimpleStringProperty(status.toString());
	}

	public StringProperty getBookedByProperty() {
		return new SimpleStringProperty(bookedBy.toString());
	}

	public StringProperty getBookingHirePriceProperty() {
		return new SimpleStringProperty(String.format("%.2f", getBookingHirePrice()));
	}

	// Example for booking event commission
	public StringProperty getBookingEventCommissionProperty() {
		return new SimpleStringProperty(String.format("%.2f", getBookingEventCommission()));
	}

	public StringProperty getBookingTotalProperty() {
		return new SimpleStringProperty(String.format("%.2f", getBookingTotal()));
	}

	@Override
	public String toString() {
		return "Booking{" +
				"bookingId=" + bookingId +
				", eventName='" + (event != null ? event.getEventName() : "") + '\'' +
				", venueName='" + (venue != null ? venue.getName() : "") + '\'' +
				", clientName='" + (client != null ? client.getClientName() : "") + '\'' +
				", bookingDate=" + bookingDate +
				", eventCost=" + getBookingHirePrice() +
				", commission=" + getBookingEventCommission() +
				", Booking Total: " + getBookingTotal() +
				", status=" + status +
				'}';
	}
}