package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHandler {

	private static final String DB_URL = "jdbc:sqlite:db/music_venue.db";

	// Always return a fresh connection
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL);
	}

	// Close connection properly
	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.err.println("Error closing the database connection: " + e.getMessage());
			}
		}
	}

	// Initialise database by checking & executing schema.sql
	public static void initialiseDatabase() {
		String filePath = "src/main/resources/db/schema.sql";
		String checkTablesSQL = """
            SELECT COUNT(*) AS count FROM sqlite_master
            WHERE type='table' 
            AND name IN ('clients', 'events', 'venues', 'venue_types', 'venue_types_venues', 'bookings', 'users');
        """;

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement()) {

			// Check if tables already exist
			ResultSet rs = stmt.executeQuery(checkTablesSQL);
			if (rs.next() && rs.getInt("count") == 7) {
				return;
			}

			// Execute schema.sql to create tables
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				StringBuilder sql = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sql.append(line).append("\n");
					if (line.trim().endsWith(";")) { // Execute when full statement is formed
						stmt.execute(sql.toString());
						sql.setLength(0);
					}
				}
			}
		} catch (IOException | SQLException e) {
			System.err.println("Error initializing database: " + e.getMessage());
		}
	}
}