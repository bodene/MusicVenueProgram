package model;

import java.time.LocalDate;

public class Booking {

	private int bookingID;
	private Event event;
	private Venue venue;
	private Client client;
	private LocalDate bookingDate;
	private double hirePrice;
	private double commission;
	private BookingStatus status;

	/**
	 * Constructor for Booking
	 * @param event
	 * @param venue
	 * @param client
	 * @param bookingDate
	 * @param hirePrice
	 */
	public Booking(Event event, Venue venue, Client client, LocalDate bookingDate, double hirePrice) {
		// TODO - implement Booking.Booking
		throw new UnsupportedOperationException();
	}

	public int getBookingId() {
		// TODO - implement Booking.getBookingId
		throw new UnsupportedOperationException();
	}

	public Event getEvent() {
		return this.event;
	}

	public Venue getVenue() {
		return this.venue;
	}

	public Client getClient() {
		return this.client;
	}

	public LocalDate getBookingDate() {
		return this.bookingDate;
	}

	public double getHirePrice() {
		return this.hirePrice;
	}

	public double getCommission() {
		return this.commission;
	}

	/**
	 * 
	 * @param commission
	 */
	public void setCommission(double commission) {
		this.commission = commission;
	}

	public BookingStatus getStatus() {
		return this.status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public double calculateCommission() {
		// TODO - implement Booking.calculateCommission
		throw new UnsupportedOperationException();
	}

	public String toString() {
		// TODO - implement Booking.toString
		throw new UnsupportedOperationException();
	}

}