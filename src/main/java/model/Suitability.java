package model;

public class Suitability {
	private String venueName;  // Store venue name instead of ID (for CSV loading)
	private String eventType;  // E.g., "Gig", "Festival", "Live Concert"

	public Suitability(String venueName, String eventType) {
		this.venueName = venueName;
		this.eventType = eventType;
	}

	public String getVenueName() {
		return venueName;
	}

	public String getEventType() {
		return eventType;
	}

	@Override
	public String toString() {
		return "[Venue: " + venueName + ", Event: " + eventType + "]";
	}
}