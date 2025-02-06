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
	private String name;
	private VenueCategory category;
	private int capacity;
	private double hirePricePerHour;
	private List<VenueType> venueTypes;
//	private boolean available;
	private Set<Booking> bookings;

	/**
	 * Venue Constructor
	 * @param name
	 * @param category
	 * @param capacity
	 * @param price
	 */
	public Venue(String name, String category, int capacity, double price) {
		this.name = name;
		this.category = setCategory(category);
		this.capacity = capacity;
		this.hirePricePerHour = price;
		this.venueTypes = new ArrayList<>();
	}

	// Constructor for DB loading (fetching existing venue data)
	public Venue(int id, String name, int capacity, List<VenueType> venueTypes) {
		this.venueId = id;
		this.name = name;
		this.capacity = capacity;
//		this.available = available;
		this.venueTypes = venueTypes;
	}

	// Constructor for the database to table conversion
	public Venue(int venueId, String venueName, String venueCategory, int venueCapacity, String hirePricePerHour, String venueTypes) {
		this.venueId = venueId;
		this.name = venueName;
		this.category = setCategory(venueCategory); // Convert String to VenueCategory Enum
		this.capacity = venueCapacity;
		this.hirePricePerHour = Double.parseDouble(hirePricePerHour); // Convert price from String to double
		this.venueTypes = parseVenueTypes(venueTypes); // Convert CSV string to List<VenueTypes>
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


	//public void setAvailability(boolean availability) {
	//	this.available = availability;
	//}

	// Checks Venue Availability for specific date and time
	public boolean checkAvailability(int venueId, LocalDate requestedDate, LocalTime requestedTime, int duration) {
//		if(!available) {
//			return false;
//		}

		// Convert hours to minutes and calculate the end time of event
		int durationMinutes = duration * 60;
		LocalTime endTime = requestedTime.plusMinutes(durationMinutes);

		// Query bookings for conflicts for the specific venue
		BookingDAO bookingDAO = new BookingDAO();
		List<Booking> conflictingBookings = bookingDAO.getConflictingBookings(venueId, requestedDate, requestedTime, endTime);

		// If no conflicting bookings, the venue is available
		return conflictingBookings.isEmpty();
	}

	// Add a venueType manually (used when loading from CSV)
	public void addVenueType(VenueType venueTypes) {
		this.venueTypes.add(venueTypes);
	}

	// Returns a list of venueTypes
	public List<VenueType> getVenueTypes() {
		return this.venueTypes;
	}

	// Converts the String from databse into venueTypes objects
	private List<VenueType> parseVenueTypes(String venueTypes) {
		List<VenueType> venueTypesList = new ArrayList<>();
		if (venueTypes != null && !venueTypes.isEmpty()) {
			String[] venueTypesArray = venueTypes.split(", "); // Split CSV string
			for (String venueType : venueTypesArray) {
				venueTypesList.add(new VenueType(venueType.trim())); // Create Venue Type objects
			}
		}
		return venueTypesList;
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
		return "Venue ID: " + venueId + ", Name: " + name + ", Category: " + category + ", Capacity: " + capacity + ", Hourly Hire Price: " + hirePricePerHour + ", Venue Types: " + venueTypes;
	}


}