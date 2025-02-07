package service;

import dao.ClientDAO;
import dao.EventDAO;
import dao.VenueDAO;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.io.*;
import java.util.*;

public class CSVHandler {

	// Imports a list of venues from the CSV file
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

				String[] data = line.split(",");
				if (data.length < 5) continue;

				String venueName = data[0].trim();
				int venueCapacity = Integer.parseInt(data[1].trim());
				String venueTypesString = data[2].trim();
				String category = data[3].trim().toUpperCase();
				double pricePerHour = Double.parseDouble(data[4].trim());

				Venue venue = new Venue(venueName, category, venueCapacity, pricePerHour);

				// Splits venue types by semicolon & Add multiple venue types
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

	// Imports events data from csv
	public static List<Event> importEventDataCSV(String filePath) {
		List<Event> events = new ArrayList<>();
		String line = null;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			boolean firstLine = true;

			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}

				String[] data = line.split(",");
				if (data.length < 9) {
					continue;
				}

				try {
					int eventId = generateNewEventId();
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

					Client client = ClientDAO.findOrCreateClient(clientName);
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

	private static int generateNewEventId() {
		return (int) (System.currentTimeMillis() % Integer.MAX_VALUE); // Simple unique ID
	}

	// Helper Method - Convert Time to Standard
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

	// Helper Method - Handles both `dd-MM-yy` and `dd/MM/yyyy` date formats
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
}