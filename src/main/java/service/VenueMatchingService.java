package service;

import model.Event;
import model.Venue;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class VenueMatchingService {

    public static int calculateCompatibility(Venue venue, Event event) throws SQLException {
        int score = 0;
//
//        // 1. Availability Check
//        if (venue.isAvailable()) score += 25;
//
//        // 2. Capacity Check
//        if (venue.getCapacity() >= event.getRequiredCapacity()) score += 25;
//
//        // 3. Event Category Match
//        boolean eventCategoryMatch = switch (event.getCategory()) {
//            case INDOOR -> venue.getCategory() == VenueCategory.INDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
//            case OUTDOOR -> venue.getCategory() == VenueCategory.OUTDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
//            case CONVERTIBLE -> venue.getCategory() == VenueCategory.CONVERTIBLE;
//            default -> false;
//        };
//        if (eventCategoryMatch) score += 25;
//
//        // 4. Venue Types Match (case-insensitive)
//        if (venue.getVenueTypes().stream()
//                .map(type -> type.getVenueType().toLowerCase())
//                .collect(Collectors.toSet())
//                .contains(event.getEventType().toLowerCase())) {
//            score += 25;
//        }
//
        return score; // 0 to 100%
    }

}
