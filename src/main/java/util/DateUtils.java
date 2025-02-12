package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting dates and times.
 * <p>
 * The {@code DateUtils} class provides helper methods for converting {@link LocalDate} and {@link LocalTime}
 * objects into human-readable string representations using predefined formats.
 * </p>
 *
 * @author Bodene Downie
 * @version 1.0
 */
public class DateUtils {

    private DateUtils() {}

    /**
     * Formats a {@link LocalDate} into a string using the pattern "dd MMM yyyy".
     * <p>
     * For example, a date of 2025-12-25 would be formatted as "25 Dec 2025". If the input date is {@code null},
     * the method returns "N/A".
     * </p>
     *
     * @param date the {@code LocalDate} to format
     * @return the formatted date string, or "N/A" if the date is {@code null}
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A";
    }

    /**
     * Formats a {@link LocalTime} into a string using the pattern "hh:mm a".
     * <p>
     * For example, a time of 20:30 (8:30 PM) would be formatted as "08:30 PM". If the input time is {@code null},
     * the method returns "N/A".
     * </p>
     *
     * @param time the {@code LocalTime} to format
     * @return the formatted time string, or "N/A" if the time is {@code null}
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(DateTimeFormatter.ofPattern("hh:mm a")) : "N/A";
    }
}