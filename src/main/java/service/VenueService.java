package service;
//DONE
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

public class VenueService {

    // ADD VENUE & VENUE-TYPES
    public static boolean addVenue(String venueName, String category, int capacity, double pricePerHour, List<String> venueTypes) {
        Venue venue = new Venue(venueName, category, capacity, pricePerHour);
        return VenueDAO.addVenue(venue, venueTypes);
    }

    // GET ALL VENUES AND FORMAT CAPACITY & PRICE
    public static ObservableList<Venue> getAllVenues() {
        List<Venue> venues = VenueDAO.getAllVenues();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

        venues.forEach(venue -> {
            venue.setFormattedCapacity(numberFormat.format(venue.getCapacity()));
            venue.setFormattedPrice(currencyFormat.format(venue.getHirePricePerHour()));
        });

        return FXCollections.observableArrayList(venues);
    }

    public static ObservableList<Venue> searchVenues(String searchText, List<String> categories) throws SQLException {
        return FXCollections.observableArrayList(VenueDAO.searchVenuesByNameAndCategory(searchText, categories));
    }

    // SEARCH VENUES BY NAME AND CATEGORY
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

    public static boolean deleteVenue(int venueId) {
        return VenueDAO.deleteVenue(venueId);
    }


    // HELPER METHOD - GET VENUE CATEGORIES AND COMBINE CONVERTIBLE WITH INDOOR & OUTDOOR
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

    // PARSE VENUE TYPES
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