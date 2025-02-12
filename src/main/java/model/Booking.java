package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

/**
 * Represents a booking for an event at a venue.
 * <p>
 * The {@code Booking} class encapsulates all the details of a booking, including the associated event,
 * venue, client, booking date, booking status, and the user who made the booking. It also provides methods
 * for dynamically calculating the cost, commission, and total amount of the booking.
 * </p>
 *
 * <p>
 * This class implements {@code Serializable} to allow instances to be serialized.
 * </p>
 *
 * @author	Bodene Downie
 * @version 1.0
 */
public class Booking implements Serializable {

	private static final long serialVersionUID = 1L;  // Ensure compatibility for serialization

	private int bookingId;
	private Event event;
	private Venue venue;
	private Client client;
	private LocalDate bookingDate;
	private BookingStatus status;
	private String bookedBy;
	private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

	/**
	 * Constructs a {@code Booking} with the specified details.
	 *
	 * @param bookingId   the unique identifier for the booking
	 * @param event       the associated event
	 * @param venue       the venue where the event is held
	 * @param client      the client who made the booking
	 * @param bookingDate the date of the booking
	 */
	public Booking(int bookingId, Event event, Venue venue, Client client, LocalDate bookingDate) {
		this.bookingId = bookingId;
		this.event = event;
		this.venue = venue;
		this.client = client;
		this.bookingDate = bookingDate;
		this.status = BookingStatus.CONFIRMED;
	}

	/**
	 * Constructs a {@code Booking} with the specified details.
	 *
	 * @param bookingId     the unique identifier for the booking
	 * @param bookingStatus the booking status as a String
	 * @param event         the associated event
	 * @param venue         the venue where the event is held
	 * @param client        the client who made the booking
	 * @param bookingDate   the date of the booking
	 * @param bookedBy      the username of the person who made the booking
	 */
	public Booking(int bookingId, String bookingStatus, Event event, Venue venue, Client client, LocalDate bookingDate, String bookedBy) {
		this.bookingId = bookingId;
		this.event = event;
		this.venue = venue;
		this.client = client;
		this.bookingDate = bookingDate;
		this.status = parseBookingStatus(bookingStatus);
		this.bookedBy = bookedBy;
	}

	/**
	 * Constructs a {@code Booking} using simplified event, venue, and client details.
	 * <p>
	 * This constructor is useful when creating a booking from UI data.
	 * </p>
	 *
	 * @param bookingId      the unique identifier for the booking
	 * @param eventId        the ID of the event
	 * @param eventName      the name of the event
	 * @param eventDate      the date of the event
	 * @param eventTime      the start time of the event
	 * @param eventDuration  the duration of the event in hours
	 * @param eventArtist    the artist performing at the event
	 * @param venueId        the ID of the venue
	 * @param venueName      the name of the venue
	 * @param hirePrice      the base hire price per hour for the venue
	 * @param clientId       the ID of the client
	 * @param clientName     the name of the client
	 * @param bookingStatus  the booking status as a String
	 * @param bookedBy       the username of the person who made the booking
	 */
	public Booking(int bookingId, int eventId, String eventName, LocalDate eventDate, LocalTime eventTime, int eventDuration, String eventArtist, int venueId, String venueName, double hirePrice, int clientId, String clientName, String bookingStatus, String bookedBy) {
		this.bookingId = bookingId;
		// Create a simplified Event using a suitable constructor.
		this.event = new Event(eventId, eventName, eventDate, eventTime, eventDuration, eventArtist);
		// Create a simplified Venue using a suitable constructor.
		this.venue = new Venue(venueId, venueName, hirePrice);
		// Create a simplified Client using a suitable constructor.
		this.client = new Client(clientId, clientName);
		this.status = parseBookingStatus(bookingStatus);
		this.bookedBy = bookedBy;
	}

	/**
	 * Constructs a {@code Booking} with minimal information for UI display purposes.
	 *
	 * @param bookingId the unique identifier for the booking
	 * @param eventName the name of the event
	 * @param venueName the name of the venue
	 * @param bookedBy  the username of the person who made the booking
	 */
	public Booking(int bookingId, String eventName, String venueName, String bookedBy) {
		this.bookingId = bookingId;
		this.event = new Event(eventName);
		this.venue = new Venue(venueName);
		this.bookedBy = bookedBy;
		this.status = BookingStatus.CONFIRMED;
	}

	/**
	 * Constructs a {@code Booking} for backup purposes.
	 *
	 * @param bookingId   the unique identifier for the booking
	 * @param bookingDate the date of the booking
	 * @param status      the booking status as a String
	 * @param eventId     the ID of the event
	 * @param venueId     the ID of the venue
	 * @param clientId    the ID of the client
	 * @param bookedBy    the username of the person who made the booking
	 */
	public Booking(int bookingId, LocalDate bookingDate, String status, int eventId, int venueId, int clientId, String bookedBy) {
		this.bookingId = bookingId;
		this.bookingDate = bookingDate;
		this.status = parseBookingStatus(status);
		this.event = new Event(eventId);
		this.venue = new Venue(venueId);
		this.client = new Client(clientId);
		this.bookedBy = bookedBy;
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

	/**
	 * Calculates the total hire price for the booking.
	 * <p>
	 * This method multiplies the venue's base hire price per hour by the event duration.
	 * If the booking is not confirmed or the venue or event is not set, it returns 0.
	 * </p>
	 *
	 * @return the calculated booking hire price
	 */
	public double getBookingHirePrice() {
		if (status != BookingStatus.CONFIRMED || venue == null || event == null) return 0;
		double basePrice = venue.getHirePricePerHour();
		int duration = event.getDuration();
		return basePrice * duration;
	}

	/**
	 * Calculates the commission for the booking.
	 * <p>
	 * This method calculates the commission as a percentage of the booking hire price, based on the client's commission rate.
	 * If the booking is not confirmed or the client is not set, it returns 0.
	 * </p>
	 *
	 * @return the calculated commission amount
	 */
	public double getBookingEventCommission() {
		if (status != BookingStatus.CONFIRMED || client == null) return 0;
		double eventCost = getBookingHirePrice();
		double commissionRate = client.getCommissionRate();
		return eventCost * commissionRate;
	}

	/**
	 * Calculates the total booking cost.
	 * <p>
	 * The total cost is the sum of the booking hire price and the commission.
	 * If the booking is not confirmed, it returns 0.
	 * </p>
	 *
	 * @return the total booking cost
	 */
	public double getBookingTotal() {
		if (status != BookingStatus.CONFIRMED) return 0;
		return getBookingHirePrice() + getBookingEventCommission();
	}

	// SETTERS
	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	/**
	 * Parses a booking status string and returns the corresponding {@code BookingStatus}.
	 * <p>
	 * If the provided string cannot be parsed, it returns {@code BookingStatus.PENDING} as the default.
	 * </p>
	 *
	 * @param status the booking status string to parse
	 * @return the corresponding {@code BookingStatus}
	 */
	private BookingStatus parseBookingStatus(String status) {
		try {
			return BookingStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			return BookingStatus.PENDING;  // Default status if parsing fails
		}
	}

	/**
	 * Returns the booking status as a {@code StringProperty} for use in UI bindings.
	 *
	 * @return the booking status as a {@code StringProperty}
	 */
	public StringProperty getStatusProperty() {
		return new SimpleStringProperty(status.toString());
	}

	/**
	 * Returns the booked-by username as a {@code StringProperty} for use in UI bindings.
	 *
	 * @return the booked-by value as a {@code StringProperty}
	 */
	public StringProperty getBookedByProperty() {
		return new SimpleStringProperty(this.bookedBy != null ? this.bookedBy : "N/A");
	}

	/**
	 * Returns the booking hire price as a formatted currency {@code StringProperty} for use in UI bindings.
	 *
	 * @return the booking hire price formatted as currency
	 */
	public StringProperty getBookingHirePriceProperty() {
		return new SimpleStringProperty(currencyFormatter.format(getBookingHirePrice()));
	}

	/**
	 * Returns the booking event commission as a formatted currency {@code StringProperty} for use in UI bindings.
	 *
	 * @return the booking event commission formatted as currency
	 */
	public StringProperty getBookingEventCommissionProperty() {
		return new SimpleStringProperty(currencyFormatter.format(getBookingEventCommission()));
	}

	/**
	 * Returns the total booking cost as a formatted currency {@code StringProperty} for use in UI bindings.
	 *
	 * @return the total booking cost formatted as currency
	 */
	public StringProperty getBookingTotalProperty() {
		return new SimpleStringProperty(currencyFormatter.format(getBookingTotal()));
	}

	/**
	 * Returns a string representation of this booking.
	 *
	 * @return a {@code String} representation of the booking details
	 */
	@Override
	public String toString() {
		return "Booking{" +
				"bookingId: " + bookingId +
				", eventName: " + (event != null ? event.getEventName() : "") + '\'' +
				", venueName: " + (venue != null ? venue.getName() : "") + '\'' +
				", clientName: " + (client != null ? client.getClientName() : "") + '\'' +
				", bookingDate: " + bookingDate +
				", eventCost: " + getBookingHirePrice() +
				", commission: " + getBookingEventCommission() +
				", Booking Total: " + getBookingTotal() +
				", status: " + status;
	}
}