package model;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class Booking {

	private int bookingId;
	private Event event;
	private Venue venue;
	private Client client;
	private LocalDate bookingDate;
	private double hirePrice;
	private double commission;
	private BookingStatus status;
	private String bookedBy;

	/**
	 * Constructor for Booking
	 * @param bookingId
	 * @param event
	 * @param venue
	 * @param client
	 * @param bookingDate
	 * @param hirePrice
	 */
	// CONSTRUCTORS
	public Booking(int bookingId, Event event, Venue venue, Client client, LocalDate bookingDate, double hirePrice) {
		this.bookingId = bookingId;
		this.event = event;
		this.venue = venue;
		this.client = client;
		this.bookingDate = bookingDate;
		this.hirePrice = hirePrice;
		this.commission = calculateCommission();
		this.status = BookingStatus.CONFIRMED;
	}

	// CONSTRUCTOR FOR UI BASED OBJECT CREATION
	public Booking(int bookingId, String eventName, LocalDate eventDate, String venueName, double eventCost, double eventCommission, double bookingTotal, String bookingStatus) {
		this.bookingId = bookingId;
		this.event = new Event(eventName, eventDate);  // Event can be a simplified constructor
		this.venue = new Venue(venueName);
		this.hirePrice = eventCost;
		this.commission = eventCommission;
		this.status = BookingStatus.valueOf(bookingStatus.toUpperCase());
	}

	public Booking(int bookingId, String eventName, String venueName, double bookingCommission, String bookedBy) {
		this.bookingId = bookingId;
		this.event = new Event(eventName);
		this.venue = new Venue(venueName);
		this.commission = bookingCommission;
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

	public String getEventName() {
		return event != null ? event.getEventName() : "";
	}

	public String getEventDate() {
		return event != null ? event.getEventDate().toString() : "";
	}

	public Venue getVenue() {
		return venue;
	}

	public String getVenueName() {
		return venue != null ? venue.getName() : "";
	}

	public Client getClient() {
		return client;
	}

	public String getClientName() {
		return client != null ? client.getClientName() : "";
	}

	public LocalDate getBookingDate() {
		return bookingDate;
	}

	public double getHirePrice() {
		return hirePrice;
	}

	public double getCommission() {
		return commission;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public String getBookedBy() {
		return bookedBy;
	}

	public String getFormattedHirePrice() {
		return NumberFormat.getCurrencyInstance(Locale.US).format(hirePrice);
	}

	public String getFormattedCommission() {
		return NumberFormat.getCurrencyInstance(Locale.US).format(commission);
	}

	public String getFormattedBookingTotal() {
		return NumberFormat.getCurrencyInstance(Locale.US).format(getBookingTotal());
	}

	// SETTERS
	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public void setHirePrice(double hirePrice) {
		this.hirePrice = hirePrice;
		this.commission = calculateCommission();
	}

	public void setClient(Client client) {
		this.client = client;
		this.commission = calculateCommission();
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	// CALCULATE COMMISSION BASED ON CLIENT JOB COUNT
	public double calculateCommission() {
		int totalJobs = client != null ? client.getTotalJobs() : 1;
		double rate = totalJobs > 1 ? 0.09 : 0.10;
		return hirePrice * rate;
	}

	public double getBookingTotal() {
		return hirePrice + commission;
	}

	@Override
	public String toString() {
		return "Booking{" +
				"bookingId=" + bookingId +
				", eventName='" + getEventName() + '\'' +
				", venueName='" + getVenueName() + '\'' +
				", clientName='" + getClientName() + '\'' +
				", bookingDate=" + bookingDate +
				", hirePrice=" + hirePrice +
				", commission=" + commission +
				", status=" + status +
				'}';
	}
}