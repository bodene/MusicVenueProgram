package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Utility class for handling database connections and initialisation.
 * <p>
 * This class provides methods to obtain and close database connections, as well as to initialise
 * the database schema if it is not already set up. The database used is an SQLite database located
 * at the path specified by {@code DB_URL}.
 * </p>
 * <p>
 * All methods in this class are static, and the class cannot be instantiated.
 * </p>
 *
 * @author	Bodene Downie
 * @version 1.0
 */
public class DatabaseHandler {

	/**
	 * The database URL for the SQLite database.
	 */
	private static final String DB_URL = "jdbc:sqlite:db/music_venue.db";

	/**
	 * Returns a new connection to the database.
	 * <p>
	 * This method creates and returns a fresh {@code Connection} object using the {@code DB_URL}.
	 * It is the caller's responsibility to close the connection when finished.
	 * </p>
	 *
	 * @return a new {@code Connection} to the database
	 * @throws SQLException if a database access error occurs
	 */
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL);
	}

	/**
	 * Closes the given database connection.
	 * <p>
	 * This method checks if the connection is not {@code null} before attempting to close it.
	 * If an error occurs while closing the connection, the error message is printed to standard error.
	 * </p>
	 *
	 * @param connection the {@code Connection} to close
	 */
	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.err.println("Error closing the database connection: " + e.getMessage());
			}
		}
	}

	/**
	 * Initialises the database schema by executing the SQL statements in {@code schema.sql}.
	 * <p>
	 * This method first checks whether the required tables already exist in the database by querying the
	 * SQLite system table. If all required tables are present, no further action is taken.
	 * Otherwise, the method reads and executes the SQL statements from the file located at
	 * {@code src/main/resources/db/schema.sql} to create the tables.
	 * </p>
	 */
	public static void initialiseDatabase() {

		// Path to the SQL schema file.
		String filePath = "src/main/resources/db/schema.sql";

		// SQL query to check if all required tables exist.
		String checkTablesSQL = """
            SELECT COUNT(*) AS count FROM sqlite_master
            WHERE type='table'
            AND name IN ('clients', 'events', 'venues', 'venue_types', 'venue_types_venues', 'bookings', 'users');
        """;

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement()) {

			// Check if the required tables already exist.
			ResultSet rs = stmt.executeQuery(checkTablesSQL);
			if (rs.next() && rs.getInt("count") == 7) {
				return;
			}

			// Read and execute the SQL schema file to create tables.
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				StringBuilder sql = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sql.append(line).append("\n");
					if (line.trim().endsWith(";")) { 	// Execute when full statement is formed
						stmt.execute(sql.toString());

						// Clear the StringBuilder for the next statement.
						sql.setLength(0);
					}
				}
			}
		} catch (IOException | SQLException e) {
			System.err.println("Error initialising database: " + e.getMessage());
		}
	}
}