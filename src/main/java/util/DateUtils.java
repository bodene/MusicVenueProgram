package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A";
    }

    public static String formatTime(LocalTime time) {
        return time != null ? time.format(DateTimeFormatter.ofPattern("hh:mm a")) : "N/A";
    }
}