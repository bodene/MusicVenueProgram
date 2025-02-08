package model;
//DONE
import service.VenueService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Venue {

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
	public Venue(String name, String category, int capacity, double price) {
		this.name = name;
		this.category = VenueCategory.valueOf(category.toUpperCase());
		this.capacity = capacity;
		this.hirePricePerHour = price;
		this.venueTypes = new ArrayList<>();
		this.compatibilityScore = 0;
	}

	// CONSTRUCTOR FOR DB LOADING
	public Venue(int id, String name, int capacity, List<VenueType> venueTypes) {
		this.venueId = id;
		this.name = name;
		this.capacity = capacity;
		this.venueTypes = venueTypes;
		this.compatibilityScore = 0;
	}

	// CONSTRUCTOR FOR DB-TABLE CONVERSION
	public Venue(int venueId, String venueName, String venueCategory, int venueCapacity, String hirePricePerHour, String venueTypes) {
		this.venueId = venueId;
		this.name = venueName;
		this.category = VenueCategory.valueOf(venueCategory.toUpperCase());
		this.capacity = venueCapacity;
		this.hirePricePerHour = Double.parseDouble(hirePricePerHour); // CONVERT STRING TO DOUBLE
		this.venueTypes = VenueService.parseVenueTypes(venueTypes); // CONVERT STRING TO List<VenueType>
		this.compatibilityScore = 0;
	}

	// GETTERS
	public int getVenueId() {
		return this.venueId;
	}
	public String getName() {
		return this.name;
	}
	public int getCapacity() {
		return this.capacity;
	}
	public double getHirePricePerHour() {
		return this.hirePricePerHour;
	}
	public List<VenueType> getVenueTypes() {
		return this.venueTypes;
	}
	public VenueCategory getCategory() {
		return category;
	}
	public double getCompatibilityScore() {return compatibilityScore;}

	// SETTERS
	public void setVenueId(int venueId) {this.venueId = venueId;}
	public void setName(String name) {
		this.name = name;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public void setHirePricePerHour(double price) {
		this.hirePricePerHour = price;
	}
	public Set<Booking> getBookings() {
		return this.bookings;
	}
	public void setCompatibilityScore(double compatibilityScore) {
		this.compatibilityScore = compatibilityScore;
	}

	// SETTERS & GETTERS FOR FORMATING PRICE & CAPACITY
	public String getFormattedPrice() {
		return formattedPrice;
	}
	public void setFormattedPrice(String formattedPrice) {
		this.formattedPrice = formattedPrice;
	}
	public String getFormattedCapacity() {
		return formattedCapacity;
	}
	public void setFormattedCapacity(String formattedCapacity) {
		this.formattedCapacity = formattedCapacity;
	}

	// ADD VENUE-TYPE MANUALLY (FROM CSV)
	public void addVenueType(VenueType venueTypes) {
		this.venueTypes.add(venueTypes);
	}

	@Override
	public String toString() {
		return "Venue ID: " + venueId + ", Name: " + name + ", Category: " + category + ", Capacity: " + capacity + ", Hourly Hire Price: " + hirePricePerHour + ", Venue Types: " + venueTypes;
	}
}