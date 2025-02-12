package model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import service.VenueService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents a venue where events are hosted.
 * <p>
 * The {@code Venue} class encapsulates details about a venue including its unique identifier,
 * name, capacity, hire price per hour, associated venue types, category, compatibility score,
 * and related bookings. It also provides methods for retrieving formatted strings for price and capacity,
 * which are useful for display in the user interface.
 * </p>
 * <p>
 * This class implements {@code Serializable} to allow venue objects to be serialized.
 * </p>
 *
 * @author	Bodene Downie
 * @version 1.0
 */
public class Venue implements Serializable {

	private int venueId;
	private String name;
	private int capacity;
	private double hirePricePerHour;
	private List<VenueType> venueTypes;
	private VenueCategory category;
	private double compatibilityScore;
	private Set<Booking> bookings;

	private String formattedPrice;
	private String formattedCapacity;

	// CONSTRUCTORS

	/**
	 * Constructs a new Venue with the specified name, category, capacity, and hire price.
	 *
	 * @param name     the name of the venue
	 * @param category the category of the venue as a String (e.g., "INDOOR"); this will be converted to {@link VenueCategory}
	 * @param capacity the capacity of the venue
	 * @param price    the hire price per hour
	 */
	public Venue(String name, String category, int capacity, double price) {
		this.name = name;
		this.category = VenueCategory.valueOf(category.toUpperCase());
		this.capacity = capacity;
		this.hirePricePerHour = price;
		this.venueTypes = new ArrayList<>();
		this.compatibilityScore = 0;
	}

	/**
	 * Constructs a Venue for database loading with the specified ID, name, capacity, and venue types.
	 *
	 * @param id         the unique identifier for the venue
	 * @param name       the name of the venue
	 * @param capacity   the capacity of the venue
	 * @param venueTypes the list of associated venue types
	 */
	public Venue(int id, String name, int capacity, List<VenueType> venueTypes) {
		this.venueId = id;
		this.name = name;
		this.capacity = capacity;
		this.venueTypes = venueTypes;
		this.compatibilityScore = 0;
	}

	/**
	 * Constructs a Venue from database table conversion.
	 * <p>
	 * This constructor converts string representations of hire price and venue types
	 * to the appropriate data types.
	 * </p>
	 *
	 * @param venueId         the unique venue ID
	 * @param venueName       the name of the venue
	 * @param venueCategory   the venue category as a String (converted to {@link VenueCategory})
	 * @param venueCapacity   the capacity of the venue
	 * @param hirePricePerHour the hourly hire price as a String (converted to double)
	 * @param venueTypes      the venue types as a comma-separated String (converted to {@code List<VenueType>} using {@link VenueService#parseVenueTypes(String)})
	 */
	public Venue(int venueId, String venueName, String venueCategory, int venueCapacity, String hirePricePerHour, String venueTypes) {
		this.venueId = venueId;
		this.name = venueName;
		this.category = VenueCategory.valueOf(venueCategory.toUpperCase());
		this.capacity = venueCapacity;
		this.hirePricePerHour = Double.parseDouble(hirePricePerHour); // CONVERT STRING TO DOUBLE
		this.venueTypes = VenueService.parseVenueTypes(venueTypes); // CONVERT STRING TO List<VenueType>
		this.compatibilityScore = 0;
	}

	/**
	 * Constructs a Venue with the specified ID, name, and hire price.
	 *
	 * @param venueId          the unique venue ID
	 * @param name             the name of the venue
	 * @param hirePricePerHour the hire price per hour
	 */
	public Venue(int venueId, String name, double hirePricePerHour) {
		this.venueId = venueId;
		this.name = name;
		this.hirePricePerHour = hirePricePerHour;
	}

	/**
	 * Constructs a Venue with only the venue name.
	 * <p>
	 * This constructor is useful for simple UI display or searching.
	 * </p>
	 *
	 * @param venueName the name of the venue
	 */
	public Venue(String venueName) {
		this.name = venueName;
	}

	/**
	 * Constructs a Venue for backup purposes with only the venue ID.
	 *
	 * @param venueId the unique venue ID
	 */
	public Venue(int venueId) {
		this.venueId = venueId;
	}

	/**
	 * Constructs a Venue for backup purposes with detailed parameters.
	 *
	 * @param venueId          the unique venue ID
	 * @param venueName        the name of the venue
	 * @param venueCategory    the venue category as a String (converted to {@link VenueCategory})
	 * @param venueCapacity    the capacity of the venue
	 * @param hirePricePerHour the hourly hire price as a String (converted to double)
	 */
	public Venue(int venueId, String venueName, String venueCategory, int venueCapacity, String hirePricePerHour) {
		this.venueId = venueId;
		this.name = venueName;
		this.category = VenueCategory.valueOf(venueCategory.toUpperCase());
		this.capacity = venueCapacity;
		this.hirePricePerHour = Double.parseDouble(hirePricePerHour);
	}

	// GETTERS

	public int getVenueId() { return this.venueId; }

	public String getName() { return this.name; }

	public int getCapacity() { return this.capacity; }

	public double getHirePricePerHour() { return this.hirePricePerHour; }

	public List<VenueType> getVenueTypes() { return this.venueTypes; }

	public VenueCategory getCategory() { return category; }

	public double getCompatibilityScore() { return compatibilityScore; }

	// SETTERS

	public void setVenueId(int venueId) {this.venueId = venueId;}

	public void setName(String name) { this.name = name; }

	public void setCapacity(int capacity) { this.capacity = capacity; }

	public void setHirePricePerHour(double price) { this.hirePricePerHour = price; }

	public Set<Booking> getBookings() { return this.bookings; }

	public void setCompatibilityScore(double compatibilityScore) { this.compatibilityScore = compatibilityScore; }

	// SETTERS & GETTERS FOR FORMATING PRICE & CAPACITY

	/**
	 * Returns the formatted price string.
	 *
	 * @return the formatted price as a String
	 */
	public String getFormattedPrice() {
		return formattedPrice;
	}

	/**
	 * Sets the formatted price string.
	 *
	 * @param formattedPrice the formatted price to set
	 */
	public void setFormattedPrice(String formattedPrice) {
		this.formattedPrice = formattedPrice;
	}

	/**
	 * Returns the formatted capacity string.
	 *
	 * @return the formatted capacity as a String
	 */
	public String getFormattedCapacity() {
		return formattedCapacity;
	}

	/**
	 * Sets the formatted capacity string.
	 *
	 * @param formattedCapacity the formatted capacity to set
	 */
	public void setFormattedCapacity(String formattedCapacity) {
		this.formattedCapacity = formattedCapacity;
	}

	// VENUE TYPE MANAGEMENT

	/**
	 * Adds a venue type to the list of associated venue types.
	 * <p>
	 * This method is typically used when processing CSV imports.
	 * </p>
	 *
	 * @param venueTypes the {@code VenueType} to add
	 */
	public void addVenueType(VenueType venueTypes) {
		this.venueTypes.add(venueTypes);
	}

	/**
	 * Sets the list of venue types associated with the venue.
	 *
	 * @param venueTypes the {@code List<VenueType>} to set
	 */
	public void setVenueTypes(List<VenueType> venueTypes) {
		this.venueTypes = venueTypes;
	}

	// PROPERTY METHODS FOR UI BINDINGS

	/**
	 * Returns a {@code StringProperty} for the venue name.
	 *
	 * @return the venue name as a {@code StringProperty}
	 */
	public StringProperty venueNameProperty() {
		return new SimpleStringProperty(name);
	}

	/**
	 * Returns a {@code DoubleProperty} for the hire price per hour.
	 *
	 * @return the hire price per hour as a {@code DoubleProperty}
	 */
	public DoubleProperty hirePricePerHourProperty() {
		return new SimpleDoubleProperty(hirePricePerHour);
	}

	/**
	 * Returns a string representation of the venue.
	 *
	 * @return a {@code String} describing the venue details
	 */
	@Override
	public String toString() {
		return "Venue ID: " + venueId + ", Name: " + name + ", Category: " + category + ", Capacity: " + capacity + ", Hourly Hire Price: " + hirePricePerHour + ", Venue Types: " + venueTypes;
	}
}