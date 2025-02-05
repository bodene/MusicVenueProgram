package model;

import dao.ClientDAO;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {

	private int eventId;
	private static int eventIdCounter = 0;
	private String eventName;
	private String artist;
	private LocalDate eventDate;
	private LocalTime eventTime;
	private int duration;
	private int requiredCapacity;
	private String eventType;
	private VenueCategory eventCategory;
	private Client clientName;

	/**
	 *
	 * @param eventName
	 * @param artist
	 * @param eventDate
	 * @param requiredCapacity
	 * @param eventTime
	 * @param duration
	 * @param eventType
	 * @param client
	 */
	public Event(String eventName, String artist, LocalDate eventDate, LocalTime eventTime, int duration, int requiredCapacity, String eventType, String eventCategory, String client) {
		this.eventId = ++eventIdCounter;
		this.eventName = eventName;
		this.artist = artist;
		this.eventDate = eventDate;
		this.eventTime = eventTime;
		this.duration = duration;
		this.requiredCapacity = requiredCapacity;
		this.eventType = eventType;
		this.eventCategory = setCategory(eventCategory);
		this.clientName = ClientDAO.findOrCreateClient(client);
	}

	public int getEventId() {
		return this.eventId;
	}

	public String getEventName() {
		return this.eventName;
	}

	/**
	 * 
	 * @param eventName
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getArtist() {
		return this.artist;
	}

	/**
	 * 
	 * @param artist
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	public LocalDate getEventDate() {
		return this.eventDate;
	}

	/**
	 * 
	 * @param eventDate
	 */
	public void setEventDate(LocalDate eventDate) {
		this.eventDate = eventDate;
	}

	public LocalTime getEventTime() {
		return this.eventTime;
	}

	/**
	 * 
	 * @param eventTime
	 */
	public void setEventTime(LocalTime eventTime) {
		this.eventTime = eventTime;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return this.duration;
	}

	public int getRequiredCapacity() {
		return this.requiredCapacity;
	}

	/**
	 * 
	 * @param requiredCapacity
	 */
	public void setRequiredCapacity(int requiredCapacity) {
		this.requiredCapacity = requiredCapacity;
	}

	public String getEventType() {
		return this.eventType;
	}

	/**
	 * 
	 * @param eventType
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	// Validates Category
	public static VenueCategory setCategory(String category) {
		for (VenueCategory venueCategory : VenueCategory.values()) {
			if (venueCategory.name().equalsIgnoreCase(category)) {
				return venueCategory;
			}
		}
		return VenueCategory.INDOOR;
	}

	public VenueCategory getCategory() {
		return eventCategory;
	}

	public Client getClient() {
		return this.clientName;
	}

	public void setClient(Client client) {
		this.clientName = client; //
	}

	// Overloaded method to set Client using a String name
	public void setClient(String clientName) {
		this.clientName = ClientDAO.findOrCreateClient(clientName);
	}


	@Override
	public String toString() {
		return "Event{" +
				"eventId=" + eventId +
				", eventName='" + eventName + '\'' +
				", artist='" + artist + '\'' +
				", eventDate=" + eventDate +
				", eventTime=" + eventTime +
				", duration=" + duration + " minutes" +
				", requiredCapacity=" + requiredCapacity +
				", eventType='" + eventType + '\'' +
				", eventCategory=" + eventCategory +
				", client=" + (clientName != null ? clientName.getClientName() : "No Client Assigned") +
				'}';
	}

}