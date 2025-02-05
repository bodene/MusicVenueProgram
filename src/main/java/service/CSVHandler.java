package service;

import dao.ClientDAO;
import dao.DatabaseHandler;
import model.*;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static dao.ClientDAO.findOrCreateClientId;
import static dao.SuitabilityDAO.findOrCreateSuitabilityId;

public class CSVHandler {


	// Imports a list of venues from the csv file
	public static List<Venue> importVenueDataCSV(String filePath) throws FileNotFoundException, SQLException {
		List<Venue> venues = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			boolean firstLine = true;

			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}

				String[] data = line.split(",");
				if (data.length < 5) continue;

				String venueName = data[0].trim();
				int venueCapacity = Integer.parseInt(data[1].trim());
				String suitableFor = data[2].trim();
				String category = data[3].trim().toUpperCase();
				double pricePerHour = Double.parseDouble(data[4].trim());

				Venue venue = new Venue(venueName, category, venueCapacity, pricePerHour);

				// Add Multiple suitabilities
				String[] eventTypes = suitableFor.split(";");
				for (String eventType : eventTypes) {
					venue.addSuitability(new Suitability(venueName, eventType.trim()));
				}
				venues.add(venue);
			}
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace(); // Handle incorrect formatting issues
		}

//        try {
//            saveVenuesToDatabase(venues);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
		return venues;
	}

	public static List<Event> importEventDataCSV(String filePath) {
		List<Event> events = new ArrayList<>();
		String line = null;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			boolean firstLine = true;

			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue; // Skip header row
				}
				System.out.println("Processing row: " + line); // Debug print

				String[] data = line.split(",");
				if (data.length < 9) { // Ensure enough columns
					System.out.println("Skipping row due to insufficient columns: " + Arrays.toString(data));
					continue;
				}

				try {
					int eventId = generateNewEventId(); // Or extract from CSV
					String clientName = data[0].trim();
					String title = data[1].trim();
					String artist = data[2].trim();
					String rawDate = data[3].trim();
					String rawTime = data[4].trim();
					int duration = Integer.parseInt(data[5].trim());
					int audience = Integer.parseInt(data[6].trim());
					String suitable = data[7].trim();
					String category = data[8].trim().toUpperCase();

					LocalDate eventDate = parseDate(rawDate);
					LocalTime startTime = parseTime(rawTime);

					System.out.println("Creating event: " + title + " on " + eventDate + " at " + startTime);

					Event event = new Event(eventId, title, artist, eventDate, startTime, duration, audience, suitable, category, clientName);
					events.add(event);

				} catch (Exception e) {
					System.err.println("Skipping row due to an error: " + line);
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			System.err.println("Error reading file: " + filePath);
			e.printStackTrace();
		}

		System.out.println("Total events imported: " + events.size());
		return events;
	}

	private static int generateNewEventId() {
		return (int) (System.currentTimeMillis() % Integer.MAX_VALUE); // Simple unique ID
	}

	public static LocalTime parseTime(String timeStr) {
		timeStr = timeStr.trim().toUpperCase();

		// Handle 12-hour format manually (e.g., "8PM", "12PM", "7AM")
		if (timeStr.matches("^(1[0-2]|[1-9])[APap][Mm]$")) {
			int hour = Integer.parseInt(timeStr.replaceAll("[APap][Mm]", ""));
			boolean isPM = timeStr.contains("PM");

			// Convert 12-hour format to 24-hour format
			if (isPM && hour != 12) hour += 12;  // Convert PM times (except 12PM)
			if (!isPM && hour == 12) hour = 0;   // Convert 12AM to 00:00

			return LocalTime.of(hour, 0);
		}

		// Handle 24-hour format (e.g., "20:00", "14:30")
		if (timeStr.matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
			return LocalTime.parse(timeStr); //
		}
		throw new DateTimeParseException("Invalid time format", timeStr, 0);
	}

	// Handles both `dd-MM-yy` and `dd/MM/yyyy` date formats
	public static LocalDate parseDate(String dateStr) {
		String[] formats = {"d-M-yy", "dd-MM-yy", "d/MM/yyyy", "dd/MM/yyyy"};

		for (String format : formats) {
			try {
				return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
			} catch (DateTimeParseException ignored) {
			}
		}
		throw new DateTimeParseException("Invalid date format: " + dateStr, dateStr, 0);
	}

	/**
	 * @param venues
	 */
	public static void saveVenuesToDatabase(List<Venue> venues) throws SQLException {
		String insertVenueSQL = "INSERT INTO venues (venue_name, venue_category, venue_capacity, hire_price) VALUES (?, ?, ?, ?)";
		String insertSuitabilitySQL = "INSERT INTO suitabilities_venues (venue_id, suitability_id) VALUES (?, ?)";

		try (Connection connection = DatabaseHandler.getConnection();
			 PreparedStatement venueStmt = connection.prepareStatement(insertVenueSQL, Statement.RETURN_GENERATED_KEYS);
			 PreparedStatement suitabilityStmt = connection.prepareStatement(insertSuitabilitySQL)) {

			for (Venue venue : venues) {
				try {
					// Insert venue into database
					venueStmt.setString(1, venue.getName());
					venueStmt.setString(2, venue.getCategory().name()); // Convert Enum to String
					venueStmt.setInt(3, venue.getCapacity());
					venueStmt.setDouble(4, venue.getHirePricePerHour());

					venueStmt.executeUpdate();

					// Retrieve generated venueId
					ResultSet rs = venueStmt.getGeneratedKeys();
					if (rs.next()) {
						int venueId = rs.getInt(1);

						// Insert suitability values
						for (Suitability suitability : venue.getSuitabilities()) {
							int suitabilityId = findOrCreateSuitabilityId(suitability.getEventType(), connection);
							suitabilityStmt.setInt(1, venueId);
							suitabilityStmt.setInt(2, suitabilityId);
							suitabilityStmt.executeUpdate();
						}
					}

				} catch (SQLException e) {
					System.err.println("Error inserting venue: " + venue.getName() + " | " + e.getMessage());
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Database error while saving venues: " + e.getMessage(), e);
		}

		System.out.println("Venues and their suitability saved successfully.");
	}

	/**
	 * @param events
	 */
	public static void saveEventsToDatabase(List<Event> events) throws SQLException {
		String insertEventSQL = "INSERT INTO events (event_id, event_name, event_artist, event_date, event_time, event_duration, required_capacity, event_type, event_category, client_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection connection = DatabaseHandler.getConnection();
			 PreparedStatement eventStmt = connection.prepareStatement(insertEventSQL)) {

			connection.setAutoCommit(false); // 🔹 Disable auto-commit to batch inserts

			for (Event event : events) {
				try {
					int eventId = generateUniqueEventId(connection); // Generate unique event ID
					int clientId = ClientDAO.findOrCreateClientId(event.getClientName(), connection); // Ensure client exists

					// 🛑 **Handle invalid client ID**
					if (clientId <= 0) {
						System.err.println("⚠ ERROR: Invalid Client ID for event: " + event.getEventName());
						continue; // Skip this event
					}

					eventStmt.setInt(1, eventId);
					eventStmt.setString(2, event.getEventName());
					eventStmt.setString(3, event.getArtist());
					eventStmt.setString(4, event.getEventDate().toString());
					eventStmt.setString(5, event.getEventTime().toString());
					eventStmt.setInt(6, event.getDuration());
					eventStmt.setInt(7, event.getRequiredCapacity());
					eventStmt.setString(8, event.getEventType());
					eventStmt.setString(9, event.getCategory().name());
					eventStmt.setInt(10, clientId);

					eventStmt.executeUpdate();
					System.out.println("✅ Successfully inserted: " + event.getEventName() + " | Event ID: " + eventId);

				} catch (SQLException e) {
					System.err.println("❌ SQL Error inserting event: " + event.getEventName() + " | " + e.getMessage());
					e.printStackTrace();
				}
			}

			connection.commit(); // 🔹 Commit the batch insertions
			System.out.println("✅ All events committed successfully.");

		} catch (SQLException e) {
			System.err.println("❌ Database Error: " + e.getMessage());
			throw new RuntimeException("Error inserting events", e);
		}
	}


	private static int generateUniqueEventId(Connection connection) throws SQLException {
		String query = "SELECT COALESCE(MAX(event_id), 0) + 1 FROM events";
		try (PreparedStatement stmt = connection.prepareStatement(query);
			 ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1); // Return the next available event_id
			}
		}
		throw new SQLException("Error generating event ID");
	}




}