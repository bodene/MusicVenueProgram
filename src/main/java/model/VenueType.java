package model;
//DONE
public class VenueType {
	private int venueTypeId;
	private String venueType;

	// CONSTRUCTORS
	public VenueType(String venueType) {
		this.venueType = venueType;
	}

	public VenueType(int venueTypeId, String venueType) {
		this.venueTypeId = venueTypeId;
		this.venueType = venueType;
	}

	// GETTERS
	public int getVenueTypeId() {
		return venueTypeId;
	}
	public String getVenueType() {
		return venueType;
	}

	// SETTERS
	public void setVenueTypeId(int venueTypeId) {
		this.venueTypeId = venueTypeId;
	}
	public void setVenueType(String venueType) {
		this.venueType = venueType;
	}

	@Override
	public String toString() {
		return "VenueType{id=" + venueTypeId + ", type='" + venueType + "'}";
	}
}
