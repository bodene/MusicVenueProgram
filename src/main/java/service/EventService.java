package service;

import model.Event;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EventService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    public static String formatDate(Optional<String> date) {
        return date.map(d -> DATE_FORMAT.format(java.time.LocalDate.parse(d))).orElse("N/A");
    }

    public static String formatTime(Optional<String> time) {
        return time.map(t -> TIME_FORMAT.format(java.time.LocalTime.parse(t))).orElse("N/A");
    }

    public static String formatEventDetails(Event event) {
        return String.format("""
            Event: %s
            Artist: %s
            Date: %s
            Time: %s
            Duration: %d hours
            Capacity: %d people
            Type: %s
            Category: %s
            Client: %s
        """,
                event.getEventName(),
                event.getArtist(),
                formatDate(Optional.ofNullable(event.getEventDate().toString())),
                formatTime(Optional.ofNullable(event.getEventTime().toString())),
                event.getDuration(),
                event.getRequiredCapacity(),
                event.getEventType(),
                event.getCategory(),
                event.getClientName()
        );
    }
}
