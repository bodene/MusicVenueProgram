package model;

import java.util.Set;

public class Venue {

	private int venueId;
	private String name;
	private VenueCategory category;
	private int capacity;
	private double hirePrice;
	private boolean availability;
	private Set<Booking> bookings;

	/**
	 * 
	 * @param venueId
	 * @param name
	 * @param category
	 * @param capacity
	 * @param price
	 * @param availability
	 */
	public Venue(int venueId, String name, String category, int capacity, double price, boolean availability) {
		// TODO - implement Venue.Venue
		throw new UnsupportedOperationException();
	}

	public int getVenueId() {
		return this.venueId;
	}

	/**
	 * 
	 * @param venueId
	 */
	public void setVenueId(int venueId) {
		this.venueId = venueId;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		// TODO - implement Venue.getCategory
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param category
	 */
	public void setCategory(String category) {
		// TODO - implement Venue.setCategory
		throw new UnsupportedOperationException();
	}

	public int getCapacity() {
		return this.capacity;
	}

	/**
	 * 
	 * @param capacity
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public double getPrice() {
		// TODO - implement Venue.getPrice
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param price
	 */
	public void setPrice(double price) {
		// TODO - implement Venue.setPrice
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param availability
	 */
	public void setAvailability(boolean availability) {
		this.availability = availability;
	}

	public boolean checkAvailability() {
		// TODO - implement Venue.checkAvailability
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param availability
	 */
	public void updateAvailability(boolean availability) {
		// TODO - implement Venue.updateAvailability
		throw new UnsupportedOperationException();
	}

	public Set<Suitability> getSuitabilities() {
		// TODO - implement Venue.getSuitabilities
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param suitability
	 */
	public void addSuitability(Suitability suitability) {
		// TODO - implement Venue.addSuitability
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param suitability
	 */
	public void removeSuitability(Suitability suitability) {
		// TODO - implement Venue.removeSuitability
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param event
	 */
	public boolean matchesEvent(Event event) {
		// TODO - implement Venue.matchesEvent
		throw new UnsupportedOperationException();
	}

	public boolean bookVenue() {
		// TODO - implement Venue.bookVenue
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param booking
	 */
	public void addBooking(Booking booking) {
		// TODO - implement Venue.addBooking
		throw new UnsupportedOperationException();
	}

	public Set<Booking> getBookings() {
		return this.bookings;
	}

	/**
	 * 
	 * @param event
	 */
	public int calculateSuitabilityScore(Event event) {
		// TODO - implement Venue.calculateSuitabilityScore
		throw new UnsupportedOperationException();
	}

	public String toString() {
		// TODO - implement Venue.toString
		throw new UnsupportedOperationException();
	}

}