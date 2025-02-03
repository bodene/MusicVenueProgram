package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {

	private int eventId;
	private String eventName;
	private String artist;
	private LocalDate eventDate;
	private LocalTime eventTime;
	private int requiredCapacity;
	private String eventType;
	private VenueCategory category;
	private Client client;

	/**
	 * 
	 * @param eventId
	 * @param eventName
	 * @param artist
	 * @param eventDate
	 * @param requiredCapacity
	 * @param eventTime
	 * @param eventType
	 * @param client
	 */
	public Event(int eventId, String eventName, String artist, LocalDate eventDate, int requiredCapacity, LocalTime eventTime, String eventType, Client client) {
		// TODO - implement Event.Event
		throw new UnsupportedOperationException();
	}

	public int getEventId() {
		return this.eventId;
	}

	/**
	 * 
	 * @param eventId
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
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

	public LocalDate getEventTime() {
		// TODO - implement Event.getEventTime
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param eventTime
	 */
	public void setEventTime(LocalTime eventTime) {
		this.eventTime = eventTime;
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

	public VenueCategory getCategory() {
		return this.category;
	}

	/**
	 * 
	 * @param category
	 */
	public void setCategory(VenueCategory category) {
		this.category = category;
	}

	public Client getClient() {
		return this.client;
	}

	/**
	 * 
	 * @param client
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	public String toString() {
		// TODO - implement Event.toString
		throw new UnsupportedOperationException();
	}

}