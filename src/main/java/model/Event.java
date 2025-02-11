package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Event implements Serializable {

	private int eventId;
	private String eventName;
	private String artist;
	private LocalDate eventDate;
	private LocalTime eventTime;
	private int duration;
	private int requiredCapacity;
	private String eventType;
	private VenueCategory eventCategory;
	private Client client;

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
	public Event(int eventId, String eventName, String artist, LocalDate eventDate, LocalTime eventTime, int duration, int requiredCapacity, String eventType, String eventCategory, Client client) {
		this.eventId = eventId;
		this.eventName = eventName;
		this.artist = artist;
		this.eventDate = eventDate;
		this.eventTime = eventTime;
		this.duration = duration;
		this.requiredCapacity = requiredCapacity;
		this.eventType = eventType;
		this.eventCategory = setCategory(eventCategory);
		this.client = client;
	}

	public Event(int eventId, String eventName, LocalDate eventDate, LocalTime eventTime, int eventDuration, String eventArtist) {
		this.eventId = eventId;
		this.eventName = eventName;
		this.eventTime = eventTime;
		this.eventDate = eventDate;
		this.duration = eventDuration;
		this.artist = eventArtist;
	}

	public Event(String eventName) {
		this.eventName = eventName;
	}

	// CONSTRUCTOR FOR BACKUP
	public Event(int eventId) {
		this.eventId = eventId;
	}

	// CONSTRUCTOR FOR BACKUP
	public Event(int eventId, String eventName, String eventArtist, LocalDate eventDate, LocalTime eventTime, int eventDuration, int eventCapacity, String eventType, String eventCategory, int clientId) {
		this.eventId = eventId;
		this.eventName = eventName;
		this.artist = eventArtist;
		this.eventDate = eventDate;
		this.eventTime = eventTime;
		this.duration = eventDuration;
		this.requiredCapacity = eventCapacity;
		this.eventType = eventType;
		this.eventCategory = setCategory(eventCategory);
		this.client = new Client(clientId);
	}

	public int getEventId() {
		return this.eventId;
	}

	public String getEventName() {
		return this.eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getArtist() {
		return this.artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public LocalDate getEventDate() {
		return this.eventDate;
	}


	public void setEventDate(LocalDate eventDate) {
		this.eventDate = eventDate;
	}

	public LocalTime getEventTime() {
		return this.eventTime;
	}

	// Property for event name
	public StringProperty eventNameProperty() {
		return new SimpleStringProperty(eventName);
	}

	// Property for event date (as string for UI)
	public StringProperty eventDateProperty() {
		return new SimpleStringProperty(eventDate != null ? eventDate.toString() : "");
	}

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

	public int getClientId() {
		return client.getClientId();
	}

	public Client getClient() {
		return this.client;
	}

	public String getClientName() {
		return client.getClientName();
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
				", client=" + client;
	}

}