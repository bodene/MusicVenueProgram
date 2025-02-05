package model;

import java.util.List;
import java.util.Map;

public class Staff {

	private int userId;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private UserRole userRole;

	/**
	 *
	 * @param firstName
	 * @param lastName
	 * @param username
	 * @param password
	 * @param role
	 */
	public Staff(int userId, String firstName, String lastName,String username, String password, UserRole role) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
		this.userRole = role;
	}

	public Staff(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return this.userId;
	}
	public String getFirstName() {
		return this.firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public String getUsername() {
		return this.username;
	}
	public String getPassword() {
		return this.password;
	}
	public UserRole getUserRole() {
		return this.userRole;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
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

	public String toString() {
		return "First name: " + this.firstName + "\n Last Name: " + this.lastName + "\n Username: " + this.username + "\n Role: " + this.userRole;
	}

}