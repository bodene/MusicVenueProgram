package service;

import dao.VenueDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Venue;

import java.sql.SQLException;
import java.util.List;

public class VenueService {

    public static boolean addVenue(Venue venue, List<String> venueTypes) {
        return VenueDAO.addVenue(venue, venueTypes);
    }

    public static ObservableList<Venue> getAllVenues() {
        return FXCollections.observableArrayList(VenueDAO.getAllVenues());
    }

    public static ObservableList<Venue> searchVenues(String searchText, List<String> categories) throws SQLException {
        return FXCollections.observableArrayList(VenueDAO.searchVenues(searchText, categories));
    }

    public static boolean deleteVenue(int venueId) {
        return VenueDAO.deleteVenue(venueId);
    }


}
