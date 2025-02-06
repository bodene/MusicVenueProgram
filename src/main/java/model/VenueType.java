package model;

public class VenueType {
	private String venueType;  // E.g., "Gig", "Festival", "Live Concert"

	public VenueType(String venueType) {
		this.venueType = venueType;
	}

	public String getVenueType() {
		return venueType;
	}

	@Override
	public String toString() {
		return venueType;  // Just return the event type name
	}
}
