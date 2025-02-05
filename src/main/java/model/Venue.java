package model;

import dao.BookingDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Venue {

	private int venueId;
	private static int venueIdCounter = 0;
	private String name;
	private VenueCategory category;
	private int capacity;
	private double hirePricePerHour;
	private List<Suitability> suitabilities;
	private boolean available;
	private Set<Booking> bookings;

	/**
	 * Venue Constructor
	 * @param name
	 * @param category
	 * @param capacity
	 * @param price
	 */
	public Venue(String name, String category, int capacity, double price) {
		this.venueId = ++venueIdCounter;
		this.name = name;
		this.category = setCategory(category);
		this.capacity = capacity;
		this.hirePricePerHour = price;
		this.available = true;
		this.suitabilities = new ArrayList<>();

	}

	// Constructor for DB loading (fetching existing venue data)
	public Venue(int id, String name, int capacity, boolean available, List<Suitability> suitabilities) {
		this.venueId = id;
		this.name = name;
		this.capacity = capacity;
		this.available = available;
		this.suitabilities = suitabilities;
	}

	public int getVenueId() {
		return this.venueId;
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

	// Validates VenueCategory
	public static VenueCategory setCategory(String category) {
		for (VenueCategory venueCategory : VenueCategory.values()) {
			if (venueCategory.name().equalsIgnoreCase(category)) {
				return venueCategory;
			}
		}
		return VenueCategory.INDOOR;
	}

	public VenueCategory getCategory() {
		return category;
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

	public double getHirePricePerHour() {
		return this.hirePricePerHour;
	}

	/**
	 * 
	 * @param price
	 */
	public void setHirePricePerHour(double price) {
		this.hirePricePerHour = price;
	}

	/**
	 * 
	 * @param availability
	 */
	public void setAvailability(boolean availability) {
		this.available = availability;
	}

	// Checks Venue Availability for specific date and time
	public boolean checkAvailability(int venueId, LocalDate requestedDate, LocalTime requestedTime, int duration) {
		if(!available) {
			return false;
		}

		// Convert hours to minutes and calculate the end time of event
		int durationMinutes = duration * 60;
		LocalTime endTime = requestedTime.plusMinutes(durationMinutes);

		// Query bookings for conflicts for the specific venue
		BookingDAO bookingDAO = new BookingDAO();
		List<Booking> conflictingBookings = bookingDAO.getConflictingBookings(venueId, requestedDate, requestedTime, endTime);

		// If no conflicting bookings, the venue is available
		return conflictingBookings.isEmpty();
	}

	// Add a suitability manually (used when loading from CSV)
	public void addSuitability(Suitability suitability) {
		this.suitabilities.add(suitability);
	}

	// Returns a list of suitabilities
	public List<Suitability> getSuitabilities() {
		return this.suitabilities;
	}

	/**
	 * 
	 * @param event
	 */
	public boolean matchesEvent(Event event) {
		// TODO - implement Venue.matchesEvent
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

	@Override
	public String toString() {
		return "Venue ID: " + venueId + ", Name: " + name + ", Category: " + category + ", Capacity: " + capacity + ", Hourly Hire Price: " + hirePricePerHour + ", Suitabilities: " + suitabilities;
	}


}