package service;

import dao.BookingDAO;
import dao.VenueDAO;
import model.Event;
import model.Venue;
import model.VenueCategory;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@code VenueMatchingService} class provides functionality to generate venue recommendations for events.
 * <p>
 * It evaluates available venues based on several criteria:
 * <ul>
 *   <li><b>Availability</b>: Awards 25 points if the venue is available during the event's time slot.</li>
 *   <li><b>Capacity</b>: Awards 25 points if the venue's capacity meets or exceeds the event's requirement.</li>
 *   <li><b>Event Category Matching</b>: Awards 25 points if the venue's category (or convertible status) matches the event's category.</li>
 *   <li><b>Venue Type Matching</b>: Awards 25 points if the venue supports the event's type.</li>
 * </ul>
 * The total compatibility score is the sum of these criteria and ranges from 0 to 100.
 * </p>
 *
 * <p>
 * This class contains two helper inner classes:
 * <ul>
 *   <li>{@link VenueCandidate} - Encapsulates a candidate venue along with its computed compatibility score and
 *       the difference between the venue's capacity and the event's required capacity.</li>
 *   <li>{@link AutoMatchResult} - Represents a recommendation for an event, including the best candidate venue (if any)
 *       and a list of any unmet criteria.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Key methods:
 * <ul>
 *   <li>{@link #getRecommendations(List)} - Generates a list of venue recommendations for the given events.</li>
 *   <li>{@link #calculateCompatibility(Venue, Event)} - Calculates the compatibility score between a specific venue and event.</li>
 * </ul>
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class VenueMatchingService {

    /**
     * Represents a candidate venue for an event, including its compatibility score and capacity difference.
     */
    public static class VenueCandidate {
        public Venue venue;
        public int score;
        public int capacityDiff;

        /**
         * Constructs a {@code VenueCandidate} with the specified venue, compatibility score, and capacity difference.
         *
         * @param venue the candidate venue
         * @param score the compatibility score for the venue-event pair
         * @param capacityDiff the difference between the venue's capacity and the event's required capacity
         */
        public VenueCandidate(Venue venue, int score, int capacityDiff) {
            this.venue = venue;
            this.score = score;
            this.capacityDiff = capacityDiff;
        }
    }

    /**
     * Represents the result of the auto-matching process for an event.
     * <p>
     * Contains the event, the best matching candidate venue (if any), and a list of criteria that the candidate did not meet.
     * </p>
     */
    public static class AutoMatchResult {
        public Event event;
        public VenueCandidate candidate;
        public List<String> unmetCriteria;

        /**
         * Constructs an {@code AutoMatchResult} with the specified event, candidate, and list of unmet criteria.
         *
         * @param event the event for which a recommendation is generated
         * @param candidate the best matching candidate venue (or {@code null} if no suitable venue is found)
         * @param unmetCriteria a list of criteria that the candidate venue did not meet
         */
        public AutoMatchResult(Event event, VenueCandidate candidate, List<String> unmetCriteria) {
            this.event = event;
            this.candidate = candidate;
            this.unmetCriteria = unmetCriteria;
        }
    }

    /**
     * Generates venue recommendations for a list of events.
     * <p>
     * For each event, this method evaluates all available venues by:
     * <ol>
     *   <li>Checking if the venue is available for the event's time slot.</li>
     *   <li>Ensuring the venue's capacity meets the event's requirements.</li>
     *   <li>Calculating a compatibility score based on availability, capacity, event category, and venue type matching.</li>
     *   <li>Sorting the candidate venues by their compatibility score (and capacity difference as a secondary criterion).</li>
     *   <li>Identifying any unmet criteria for the best candidate.</li>
     * </ol>
     * The method returns a list of {@code AutoMatchResult} objects, one for each event.
     * </p>
     *
     * @param events a list of events for which to generate venue recommendations
     * @return a list of {@code AutoMatchResult} objects representing the recommendations for each event
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
}
