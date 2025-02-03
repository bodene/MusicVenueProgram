package model;

import java.util.List;
import java.util.Map;

public class Staff {

	private int userId;
	private String userName;
	private String password;
	private UserRole userRole;

	/**
	 * 
	 * @param userId
	 * @param userName
	 * @param password
	 * @param role
	 */
	public Staff(int userId, String userName, String password, UserRole role) {
		// TODO - implement Staff.Staff
		throw new UnsupportedOperationException();
	}

	public List<Event> listActiveEvents() {
		// TODO - implement Staff.listActiveEvents
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param eventId
	 */
	public Event viewEventDetails(int eventId) {
		// TODO - implement Staff.viewEventDetails
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param category
	 * @param name
	 */
	public List<Venue> searchVenues(VenueCategory category, String name) {
		// TODO - implement Staff.searchVenues
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param venueId
	 */
	public Venue viewVenueDetails(int venueId) {
		// TODO - implement Staff.viewVenueDetails
		throw new UnsupportedOperationException();
	}

	public Map<Event, Venue> autoMatchVenues() {
		// TODO - implement Staff.autoMatchVenues
		throw new UnsupportedOperationException();
	}

	public List<Booking> viewAllBookings() {
		// TODO - implement Staff.viewAllBookings
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param event
	 * @param venue
	 * @param client
	 */
	public Booking hireVenue(Event event, Venue venue, Client client) {
		// TODO - implement Staff.hireVenue
		throw new UnsupportedOperationException();
	}

}