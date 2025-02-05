package dao;

import model.Booking;
import model.Event;
import model.Venue;
import model.VenueCategory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import dao.DatabaseHandler;
import model.Venue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseHandler {

	private static final String DB_URL = "jdbc:sqlite:src/main/resources/music_venue.db";
	private static Connection connection;

	public static Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed()) {
			try {
				connection = DriverManager.getConnection(DB_URL);
				System.out.println("Connected to SQLite database successfully.");
				initialiseDatabase();
			} catch (SQLException e) {
				throw new SQLException("Error connecting to database: " + e.getMessage(), e);
			}
		}
		return connection;
	}

	// Close connection
	public static void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				System.out.println("Disconnected from SQLite database.");
			}
		} catch (SQLException e) {
			System.err.println("Error closing the database connection: " + e.getMessage());
		}
	}

	// Execute schema.sql to create tables
	private static void initialiseDatabase() {
		String filePath = "src/main/resources/db/schema.sql"; // Path to SQL script
		try (BufferedReader br = new BufferedReader(new FileReader(filePath));
			 Statement stmt = connection.createStatement()) {

			// Check if tables exist first
			ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users';");
			if (rs.next()) {
				System.out.println("Database tables already exist. Skipping initialization.");
				return; // Tables exist, skip execution
			}

			StringBuilder sql = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sql.append(line).append("\n");
				if (line.trim().endsWith(";")) { // Execute SQL when encountering semicolon
					stmt.execute(sql.toString());
					sql.setLength(0); // Clear the buffer
				}
			}
			System.out.println("Database schema initialised successfully.");

		} catch (IOException | SQLException e) {
			System.err.println("Error executing schema.sql: " + e.getMessage());
		}
	}




	/**
	 * 
	 * @param event
	 */
	public void saveEvent(Event event) {
		// TODO - implement DatabaseHandler.saveEvent
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param booking
	 */
	public void saveBooking(Booking booking) {
		// TODO - implement DatabaseHandler.saveBooking
		throw new UnsupportedOperationException();
	}

	public List<Venue> getVenues() {
		// TODO - implement DatabaseHandler.getVenues
		throw new UnsupportedOperationException();
	}

	public List<Event> getEvents() {
		// TODO - implement DatabaseHandler.getEvents
		throw new UnsupportedOperationException();
	}

	public List<Booking> getBookings() {
		// TODO - implement DatabaseHandler.getBookings
		throw new UnsupportedOperationException();
	}

	public List<VenueCategory> getVenueCategories() {
		// TODO - implement DatabaseHandler.getVenueCategories
		throw new UnsupportedOperationException();
	}

	public List<Venue> getAvailableVenues() {
		// TODO - implement DatabaseHandler.getAvailableVenues
		throw new UnsupportedOperationException();
	}

}