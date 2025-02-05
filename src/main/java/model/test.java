package model;

import service.CSVHandler;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import dao.DatabaseHandler;
import java.sql.Connection;

public class test {
    public static void main(String[] args) throws FileNotFoundException, SQLException {
        Connection conn = DatabaseHandler.getConnection();
        if (conn != null) {
            System.out.println("🎵 Database connection successful!");
        } else {
            System.out.println("❌ Database connection failed!");
        }



//        // Path to your CSV file (ensure this matches your actual file location)
//        String filePath = "src/main/resources/venues.csv";
//
//        // Import venues from CSV
//        CSVHandler CSVLoader = new CSVHandler();
//        List<Venue> venues = CSVLoader.importVenueDataCSV(filePath);
//
//        // Check if the venues list is empty
//        if (venues.isEmpty()) {
//            System.out.println("❌ No venues were loaded. Check your CSV file path and format.");
//        } else {
//            System.out.println("✅ Successfully imported venues:");
//            for (Venue venue : venues) {
//                System.out.println(venue);
//            }
//        }
            String filePath = "src/main/resources/requests.csv"; // Ensure the file exists

            CSVHandler CSVEventLoader = null;
            List<Event> events = CSVEventLoader.importEventDataCSV(filePath);

            if (events.isEmpty()) {
                System.out.println("❌ No events loaded. Check the CSV file.");
            } else {
                System.out.println("✅ Successfully imported events:");
                for (Event event : events) {
                    System.out.println(event);
                }
            }
//        }
        DatabaseHandler.closeConnection();
    }
}



