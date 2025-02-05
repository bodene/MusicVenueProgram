package service;

import model.Booking;
import model.Client;
import model.Event;
import model.Venue;

import java.time.LocalDate;
import java.util.Set;

public class BookingHandler {

	private LocalDate orderDate;
	private double commission;
	private Set<Booking> bookings;
	private double COMMISSION_RATE = 0.10;
	private double DISCOUNTED_COMMISSION_RATE = 0.09;

	public LocalDate getOrderDate() {
		return this.orderDate;
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

	/**
	 * 
	 * @param booking
	 */
	public double calculateCommission(Booking booking) {
		// TODO - implement BookingHandler.calculateCommission
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param event
	 * @param venue
	 * @param client
	 */
	public Booking bookVenueManually(Event event, Venue venue, Client client) {
		// TODO - implement BookingHandler.bookVenueManually
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param event
	 * @param client
	 */
	public Booking bookVenueAutoMatch(Event event, Client client) {
		// TODO - implement BookingHandler.bookVenueAutoMatch
		throw new UnsupportedOperationException();
	}

	public Set<Booking> getAllBookings() {
		// TODO - implement BookingHandler.getAllBookings
		throw new UnsupportedOperationException();
	}

}