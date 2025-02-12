package service;

import dao.BookingDAO;
import dao.VenueDAO;
import model.Event;
import model.Venue;
import model.VenueCategory;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class VenueMatchingService {

    // Helper classes
    public static class VenueCandidate {
        public Venue venue;
        public int score;
        public int capacityDiff;

        public VenueCandidate(Venue venue, int score, int capacityDiff) {
            this.venue = venue;
            this.score = score;
            this.capacityDiff = capacityDiff;
        }
    }

    public static class AutoMatchResult {
        public Event event;
        public VenueCandidate candidate;
        public List<String> unmetCriteria;

        public AutoMatchResult(Event event, VenueCandidate candidate, List<String> unmetCriteria) {
            this.event = event;
            this.candidate = candidate;
            this.unmetCriteria = unmetCriteria;
        }
    }

    /**
     * Generates recommendations for a list of events.
     */
    public List<AutoMatchResult> getRecommendations(List<Event> events) {
        List<Venue> allVenues = VenueDAO.getAllVenues();
        List<AutoMatchResult> recommendationsList = new ArrayList<>();

        for (Event event : events) {
            List<VenueCandidate> candidates = new ArrayList<>();

            for (Venue venue : allVenues) {
                try {
                    // Check availability first.
                    boolean isAvailable = BookingDAO.checkAvailability(
                            venue.getVenueId(),
                            event.getEventDate(),
                            event.getEventTime(),
                            event.getDuration()
                    );
                    if (!isAvailable) {
                        continue;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    continue;
                }

                // Ensure venue meets capacity.
                if (venue.getCapacity() < event.getRequiredCapacity()) {
                    continue;
                }

                try {
                    int score = calculateCompatibility(venue, event);
                    int capacityDiff = venue.getCapacity() - event.getRequiredCapacity();
                    candidates.add(new VenueCandidate(venue, score, capacityDiff));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if (candidates.isEmpty()) {
                recommendationsList.add(new AutoMatchResult(
                        event,
                        null,
                        Collections.singletonList("No available venue meets the minimum criteria (availability and capacity).")
                ));
            } else {
                candidates.sort((c1, c2) -> {
                    int scoreCompare = Integer.compare(c2.score, c1.score);
                    return (scoreCompare == 0) ? Integer.compare(c1.capacityDiff, c2.capacityDiff) : scoreCompare;
                });
                VenueCandidate bestCandidate = candidates.get(0);
                List<String> unmetCriteria = new ArrayList<>();

                // Check event category matching.
                boolean eventCategoryMatch;
                switch (event.getCategory()) {
                    case INDOOR:
                        eventCategoryMatch = bestCandidate.venue.getCategory() == VenueCategory.INDOOR ||
                                bestCandidate.venue.getCategory() == VenueCategory.CONVERTIBLE;
                        break;
                    case OUTDOOR:
                        eventCategoryMatch = bestCandidate.venue.getCategory() == VenueCategory.OUTDOOR ||
                                bestCandidate.venue.getCategory() == VenueCategory.CONVERTIBLE;
                        break;
                    case CONVERTIBLE:
                        eventCategoryMatch = bestCandidate.venue.getCategory() == VenueCategory.CONVERTIBLE;
                        break;
                    default:
                        eventCategoryMatch = false;
                }
                if (!eventCategoryMatch) {
                    unmetCriteria.add("Event Category mismatch");
                }

                // Check event type matching.
                boolean eventTypeMatch = bestCandidate.venue.getVenueTypes().stream()
                        .map(type -> type.toString().trim().toLowerCase())
                        .collect(Collectors.toSet())
                        .contains(event.getEventType().toLowerCase().trim());
                if (!eventTypeMatch) {
                    unmetCriteria.add("Venue Type mismatch");
                }

                recommendationsList.add(new AutoMatchResult(event, bestCandidate, unmetCriteria));
            }
        }
        return recommendationsList;
    }

    /**
     * Calculates the compatibility score between a venue and an event.
     * <p>
     * The score is based on four criteria:
     * <ol>
     *   <li>Availability: adds 25 points if the venue is available.</li>
     *   <li>Capacity: adds 25 points if the venue has sufficient capacity.</li>
     *   <li>Event category matching: adds 25 points if the venue category matches the event category.</li>
     *   <li>Venue type matching: adds 25 points if the event type matches one of the venue types.</li>
     * </ol>
     * The maximum score is 100.
     * </p>
     *
     * @param venue the venue to evaluate
     * @param event the event for which compatibility is calculated
     * @return the compatibility score as an integer
     * @throws SQLException if a database access error occurs during availability check
     */
    public static int calculateCompatibility(Venue venue, Event event) throws SQLException {
        int score = 0;

        // 1. Check venue availability.
        boolean isAvailable = BookingDAO.checkAvailability(venue.getVenueId(),
                event.getEventDate(), event.getEventTime(), event.getDuration());
        if (isAvailable) {
            score += 25;
        }

        // 2. Check if the venue's capacity meets the event's requirement.
        if (venue.getCapacity() >= event.getRequiredCapacity()) {
            score += 25;
        }

        // 3. Check if the venue category matches the event category.
        boolean eventCategoryMatch = switch (event.getCategory()) {
            case INDOOR -> venue.getCategory() == VenueCategory.INDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
            case OUTDOOR -> venue.getCategory() == VenueCategory.OUTDOOR || venue.getCategory() == VenueCategory.CONVERTIBLE;
            case CONVERTIBLE -> venue.getCategory() == VenueCategory.CONVERTIBLE;
        };

        if (eventCategoryMatch) {
            score += 25;}

        // 4. Check if the event type matches one of the venue types.
        if (venue.getVenueTypes().stream()
                .map(type -> type.toString().trim().toLowerCase())
                .collect(Collectors.toSet())            // Ensure uniqueness
                .contains(event.getEventType().trim().toLowerCase())) {
            score += 25;
        }

        return score; // Score ranges from 0 to 100.
    }


    /**
     * Performs bulk booking for recommended matches.
     */
    public Map<Event, Boolean> bulkBookRecommendations(List<AutoMatchResult> recommendations) {
        Map<Event, Boolean> bookingResults = new HashMap<>();
        for (AutoMatchResult result : recommendations) {
            if (result.candidate != null) {
                try {
                    boolean success = BookingDAO.bookVenue(
                            LocalDate.now(),
                            "CONFIRMED",
                            result.event.getEventId(),
                            result.candidate.venue.getVenueId(),
                            result.event.getClientId(),
                            // Get current user
                            SessionManager.getInstance().getCurrentUser().getUsername()
                    );
                    bookingResults.put(result.event, success);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    bookingResults.put(result.event, false);
                }
            } else {
                bookingResults.put(result.event, false);
            }
        }
        return bookingResults;
    }
}
