package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents an event request.
 * <p>
 * The {@code Event} class encapsulates details such as the event's ID, name, artist,
 * date, start time, duration, required capacity, type, category, and the associated client.
 * It provides multiple constructors for creating event objects in different contexts
 * (e.g., UI creation, backup, or simplified construction).
 * </p>
 * <p>
 * This class implements {@code Serializable} to allow event objects to be serialized.
 * </p>
 *
 * @author	Bodene Downie
 * @version 1.0
 */
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
	 * Constructs an {@code Event} with detailed information.
	 *
	 * @param eventId          the unique event identifier
	 * @param eventName        the name of the event
	 * @param artist           the artist performing at the event
	 * @param eventDate        the date on which the event occurs
	 * @param eventTime        the starting time of the event
	 * @param duration         the duration of the event
	 * @param requiredCapacity the required capacity for the event
	 * @param eventType        the type of the event (e.g., concert, festival)
	 * @param eventCategory    the category of the event as a String; this is validated and converted to a {@link VenueCategory}
	 * @param client           the client associated with the event
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

	/**
	 * Constructs an {@code Event} with essential details.
	 * <p>
	 * This constructor is useful when only a subset of event information is available.
	 * </p>
	 *
	 * @param eventId       the unique event identifier
	 * @param eventName     the name of the event
	 * @param eventDate     the date of the event
	 * @param eventTime     the starting time of the event
	 * @param eventDuration the duration of the event
	 * @param eventArtist   the artist performing at the event
	 */
	public Event(int eventId, String eventName, LocalDate eventDate, LocalTime eventTime, int eventDuration, String eventArtist) {
		this.eventId = eventId;
		this.eventName = eventName;
		this.eventTime = eventTime;
		this.eventDate = eventDate;
		this.duration = eventDuration;
		this.artist = eventArtist;
	}

	/**
	 * Constructs an {@code Event} with only the event name.
	 * <p>
	 * This constructor is typically used for simplified UI display or search purposes.
	 * </p>
	 *
	 * @param eventName the name of the event
	 */
	public Event(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * Constructs an {@code Event} for backup purposes with only the event ID.
	 *
	 * @param eventId the unique event identifier
	 */
	public Event(int eventId) {
		this.eventId = eventId;
	}

	/**
	 * Constructs an {@code Event} for backup purposes with detailed parameters.
	 *
	 * @param eventId      the unique event identifier
	 * @param eventName    the name of the event
	 * @param eventArtist  the artist performing at the event
	 * @param eventDate    the date of the event
	 * @param eventTime    the starting time of the event
	 * @param eventDuration the duration of the event
	 * @param eventCapacity the required capacity for the event
	 * @param eventType    the type of the event
	 * @param eventCategory the category of the event as a String; will be converted to a {@link VenueCategory}
	 * @param clientId     the ID of the client associated with the event
	 */
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

	// GETTERS

	public int getEventId() {
		return this.eventId;
	}

	public String getEventName() {
		return this.eventName;
	}

	public String getArtist() {
		return this.artist;
	}

	public LocalTime getEventTime() {
		return this.eventTime;
	}

	public int getDuration() {
		return this.duration;
	}

	public int getRequiredCapacity() {
		return this.requiredCapacity;
	}

	public String getEventType() {
		return this.eventType;
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

	// SETTERS

	public LocalDate getEventDate() {
		return this.eventDate;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public void setEventDate(LocalDate eventDate) {
		this.eventDate = eventDate;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setEventTime(LocalTime eventTime) {
		this.eventTime = eventTime;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setRequiredCapacity(int requiredCapacity) {
		this.requiredCapacity = requiredCapacity;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * Returns a {@code StringProperty} for the event name.
	 * <p>
	 * This property is useful for binding the event name to UI controls.
	 * </p>
	 *
	 * @return the event name as a {@code StringProperty}
	 */
	public StringProperty eventNameProperty() {
		return new SimpleStringProperty(eventName);
	}

	/**
	 * Returns a {@code StringProperty} for the event date.
	 * <p>
	 * If the event date is not null, its {@code toString()} value is returned; otherwise, an empty string.
	 * </p>
	 *
	 * @return the event date as a {@code StringProperty}
	 */
	public StringProperty eventDateProperty() {
		return new SimpleStringProperty(eventDate != null ? eventDate.toString() : "");
	}

	/**
	 * Validates and converts the provided event category string to a {@link VenueCategory}.
	 * <p>
	 * If the provided string matches one of the defined {@link VenueCategory} values (case-insensitive),
	 * the corresponding {@code VenueCategory} is returned; otherwise, {@code VenueCategory.INDOOR} is returned as default.
	 * </p>
	 *
	 * @param category the event category as a String
	 * @return the corresponding {@code VenueCategory}
	 */
	public static VenueCategory setCategory(String category) {
		for (VenueCategory venueCategory : VenueCategory.values()) {
			if (venueCategory.name().equalsIgnoreCase(category)) {
				return venueCategory;
			}
		}
		return VenueCategory.INDOOR;
	}


	/**
	 * Returns a string representation of the event.
	 *
	 * @return a {@code String} describing the event details
	 */
	@Override
	public String toString() {
		return "Event{" +
				"eventId: " + eventId +
				", eventName: " + eventName +
				", artist: " + artist +
				", eventDate: " + eventDate +
				", eventTime: " + eventTime +
				", duration: " + duration + " minutes" +
				", requiredCapacity: " + requiredCapacity +
				", eventType: " + eventType +
				", eventCategory: " + eventCategory +
				", client: " + client;
	}
}