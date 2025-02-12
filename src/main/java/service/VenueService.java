package service;

import dao.VenueDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Venue;
import model.VenueType;
import util.AlertUtils;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Provides services related to venue operations.
 * <p>
 * The {@code VenueService} class acts as a service layer between the user interface and the data access layer,
 * offering methods for adding, retrieving, searching, and deleting venues. It also provides helper methods
 * to format venue-related data for display purposes.
 * </p>
 *
 * @author Bodene Downie
 * @version 1.0
 */
public class VenueService {

    private VenueService() {}

    /**
     * Adds a new venue along with its associated venue types.
     * <p>
     * This method creates a new {@code Venue} object with the specified details and then saves it to the database
     * using {@link dao.VenueDAO#addVenue(Venue, List)}.
     * </p>
     *
     * @param venueName  the name of the venue
     * @param category   the venue category as a String (e.g., "INDOOR")
     * @param capacity   the capacity of the venue
     * @param pricePerHour the hire price per hour
     * @param venueTypes a {@code List<String>} representing the venue types
     * @return {@code true} if the venue is added successfully; {@code false} otherwise
     */
    public static boolean addVenue(String venueName, String category, int capacity, double pricePerHour, List<String> venueTypes) {
        Venue venue = new Venue(venueName, category, capacity, pricePerHour);
        return VenueDAO.addVenue(venue, venueTypes);
    }

    /**
     * Retrieves all venues from the database and formats their capacity and hire price for display.
     * <p>
     * This method fetches the list of venues via {@link dao.VenueDAO#getAllVenues()} and then formats each venue's
     * capacity and hourly hire price using the appropriate number formats. The formatted values are set in the venue
     * object and the list is returned as an {@code ObservableList}.
     * </p>
     *
     * @return an {@code ObservableList<Venue>} containing all venues with formatted values
     */
    public static ObservableList<Venue> getAllVenues() {
        List<Venue> venues = VenueDAO.getAllVenues();

        // Create formatters for currency and numbers.
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

        // Format each venue's capacity and hire price.
        venues.forEach(venue -> {
            venue.setFormattedCapacity(numberFormat.format(venue.getCapacity()));
            venue.setFormattedPrice(currencyFormat.format(venue.getHirePricePerHour()));
        });

        return FXCollections.observableArrayList(venues);
    }

    /**
     * Searches for venues based on search text and a list of categories.
     * <p>
     * This method delegates the search to {@link dao.VenueDAO#searchVenuesByNameAndCategory(String, List)},
     * converts the resulting list into an {@code ObservableList}, and returns it.
     * </p>
     *
     * @param searchText the text to search for in the venue name
     * @param categories a list of venue categories to filter by
     * @return an {@code ObservableList<Venue>} containing the search results
     * @throws SQLException if a database access error occurs
     */
    public static ObservableList<Venue> searchVenues(String searchText, List<String> categories) throws SQLException {
        return FXCollections.observableArrayList(VenueDAO.searchVenuesByNameAndCategory(searchText, categories));
    }

    /**
     * Searches for venues based on a name and a single category.
     * <p>
     * This overloaded method converts the single category into a list of categories using
     * {@link #getVenueCategories(String)} and then performs the search.
     * </p>
     *
     * @param name     the venue name to search for
     * @param category the venue category to filter by
     * @return an {@code ObservableList<Venue>} containing the search results
     */
    public static ObservableList<Venue> searchVenues(String name, String category) {
        List<Venue> venues = new ArrayList<>();
        try {
            venues = VenueDAO.searchVenuesByNameAndCategory(name, getVenueCategories(category));
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showAlert("Database Error", "Failed to retrieve venues: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        return FXCollections.observableArrayList(venues);
    }

    /**
     * Deletes a venue from the database.
     * <p>
     * This method delegates the deletion to {@link dao.VenueDAO#deleteVenue(int)}.
     * </p>
     *
     * @param venueId the ID of the venue to delete
     * @return {@code true} if the venue was successfully deleted; {@code false} otherwise
     */
    public static boolean deleteVenue(int venueId) {
        return VenueDAO.deleteVenue(venueId);
    }


    /**
     * Retrieves a list of venue categories based on the selected category.
     * <p>
     * If the selected category is "INDOOR" or "OUTDOOR", the list will include both the specific category and "CONVERTIBLE",
     * since convertible venues are considered suitable for both indoor and outdoor events. If "CONVERTIBLE" is selected,
     * only that category is returned.
     * </p>
     *
     * @param selectedCategory the selected venue category as a String
     * @return a {@code List<String>} of venue categories to use for filtering
     */
    public static List<String> getVenueCategories(String selectedCategory) {
        List<String> categories = new ArrayList<>();
        if ("INDOOR".equals(selectedCategory)) {
            categories.add("INDOOR");
            categories.add("CONVERTIBLE");
        } else if ("OUTDOOR".equals(selectedCategory)) {
            categories.add("OUTDOOR");
            categories.add("CONVERTIBLE");
        } else if ("CONVERTIBLE".equals(selectedCategory)) {
            categories.add("CONVERTIBLE");
        }
        return categories;
    }

    /**
     * Parses a comma-separated String of venue types into a {@code List<VenueType>}.
     * <p>
     * This method splits the input string by comma and space, trims each resulting type, and creates
     * a new {@code VenueType} object for each type.
     * </p>
     *
     * @param venueTypes the comma-separated String of venue types
     * @return a {@code List<VenueType>} representing the parsed venue types
     */
    public static List<VenueType> parseVenueTypes(String venueTypes) {
        List<VenueType> venueTypesList = new ArrayList<>();
        if (venueTypes != null && !venueTypes.isEmpty()) {
            String[] typesArray = venueTypes.split(", ");
            for (String type : typesArray) {
                venueTypesList.add(new VenueType(type.trim()));
            }
        }
        return venueTypesList;
    }
}