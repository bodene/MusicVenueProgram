package service;

import dao.ClientDAO;
import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.io.*;
import java.util.*;


/**
 * Provides methods for importing event and venue data from CSV files.
 * <p>
 * The {@code CSVHandler} class reads CSV files containing venue and event data,
 * parses the data, and converts it into corresponding model objects. It handles different
 * date and time formats and supports custom delimiters for venue types.
 * </p>
 *
 * @author	Bodene Downie
 * @version 1.0
 */
public class CSVHandler {

	private CSVHandler() {}

	/**
	 * Imports venue data from a CSV file.
	 * <p>
	 * This method reads the CSV file located at the given file path and converts each row into a {@code Venue} object.
	 * It expects the CSV to have a header row which is skipped, and each subsequent row must contain at least 5 columns:
	 * venue name, capacity, venue types (separated by semicolons), category, and price per hour.
	 * </p>
	 *
	 * @param filePath the path to the CSV file containing venue data
	 * @return a {@code List<Venue>} representing the venues imported from the CSV file
	 * @throws FileNotFoundException if the CSV file cannot be found
	 * @throws SQLException          if a database access error occurs during processing
	 */// Imports a list of venues from the CSV file
	public static List<Venue> importVenueDataCSV(String filePath) throws FileNotFoundException, SQLException {
		List<Venue> venues = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			boolean firstLine = true;

			// Skip header row
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}

				// Split the CSV row by commas.
				String[] data = line.split(",");
				// Ensure there are at least 5 columns; otherwise, skip the row.
				if (data.length < 5) continue;

				// Extract and trim values from each column.
				String venueName = data[0].trim();
				int venueCapacity = Integer.parseInt(data[1].trim());
				String venueTypesString = data[2].trim();
				String category = data[3].trim().toUpperCase();
				double pricePerHour = Double.parseDouble(data[4].trim());

				// Create a new Venue object.
				Venue venue = new Venue(venueName, category, venueCapacity, pricePerHour);

				// Splits venue types by semicolon and Add multiple venue types
				String[] venueTypes = venueTypesString.split(";");
				for (String venueType : venueTypes) {
					venue.addVenueType(new VenueType(venueType.trim()));
				}

				venues.add(venue);
			}
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}
		return venues;
	}

	/**
	 * Imports event data from a CSV file.
	 * <p>
	 * This method reads the CSV file located at the given file path and converts each row into an {@code Event} object.
	 * It expects the CSV to have a header row which is skipped, and each subsequent row must contain at least 9 columns.
	 * The method parses the event details including client name, title, artist, date, time, duration, audience size,
	 * suitability, and category. The client is retrieved or created using {@link model.Client}.
	 * </p>
	 *
	 * @param filePath the path to the CSV file containing event data
	 * @return a {@code List<Event>} representing the events imported from the CSV file
	 */
	public static List<Event> importEventDataCSV(String filePath) {
		List<Event> events = new ArrayList<>();
		String line = null;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			boolean firstLine = true;

			// Read through the CSV file, skipping the header.
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}

				String[] data = line.split(",");
				// Ensure the row has enough columns.
				if (data.length < 9) {
					continue;
				}

				try {
					// Generate a unique event ID.
					int eventId = generateNewEventId();
					// Parse event details from the CSV columns.
					String clientName = data[0].trim();
					String title = data[1].trim();
					String artist = data[2].trim();
					String rawDate = data[3].trim();
					String rawTime = data[4].trim();
					int duration = Integer.parseInt(data[5].trim());
					int audience = Integer.parseInt(data[6].trim());
					String suitable = data[7].trim();
					String category = data[8].trim().toUpperCase();

					// Parse date and time using helper methods.
					LocalDate eventDate = parseDate(rawDate);
					LocalTime startTime = parseTime(rawTime);

					// Retrieve or create the client.
					Client client = ClientDAO.findOrCreateClient(clientName);

					// Create a new Event object with the parsed details.
					Event event = new Event(eventId, title, artist, eventDate, startTime, duration,
							audience, suitable, category, client);
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
		return events;
	}

	/**
	 * Generates a new unique event ID.
	 * <p>
	 * This method uses the current system time modulo {@code Integer.MAX_VALUE} to generate a simple unique ID.
	 * </p>
	 *
	 * @return a new event ID as an integer
	 */
	private static int generateNewEventId() {
		return (int) (System.currentTimeMillis() % Integer.MAX_VALUE); // Simple unique ID
	}

	/**
	 * Parses a time string into a {@code LocalTime} object.
	 * <p>
	 * This helper method supports both 12-hour and 24-hour time formats. For example, "8PM" and "20:00" are both valid.
	 * If the time string does not match a supported format, a {@code DateTimeParseException} is thrown.
	 * </p>
	 *
	 * @param timeStr the time string to parse
	 * @return the parsed {@code LocalTime} object
	 * @throws DateTimeParseException if the time string is not in a valid format
	 */
	public static LocalTime parseTime(String timeStr) {
		timeStr = timeStr.trim().toUpperCase();

		// Handle 12-hour format manually (e.g., "8PM", "12PM", "7AM").
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

	/**
	 * Parses a date string into a {@code LocalDate} object.
	 * <p>
	 * This helper method attempts multiple date formats (e.g., "d-M-yy", "dd-MM-yy", "d/MM/yyyy", "dd/MM/yyyy")
	 * to convert the provided date string into a {@code LocalDate}. If none of the formats match, a
	 * {@code DateTimeParseException} is thrown.
	 * </p>
	 *
	 * @param dateStr the date string to parse
	 * @return the parsed {@code LocalDate} object
	 * @throws DateTimeParseException if the date string does not match any of the supported formats
	 */
	public static LocalDate parseDate(String dateStr) {
		String[] formats = {"d-M-yy", "dd-MM-yy", "d/MM/yyyy", "dd/MM/yyyy"};

		for (String format : formats) {
			try {
				return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
			} catch (DateTimeParseException ignored) {
				// Try the next format.
			}
		}
		throw new DateTimeParseException("Invalid date format: " + dateStr, dateStr, 0);
	}
}