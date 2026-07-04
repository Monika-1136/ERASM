package com.erasm.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

    private DateUtil() {
        // Prevent instantiation
    }

    public static String format(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }

    public static LocalDate parseDate(String dateStr) {
        return dateStr != null ? LocalDate.parse(dateStr, DATE_FORMATTER) : null;
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER) : null;
    }

    public static boolean isOverlap(LocalDate startA, LocalDate endA, LocalDate startB, LocalDate endB) {
        if (startA == null || startB == null) {
            return false;
        }
        LocalDate actualEndA = endA != null ? endA : LocalDate.MAX;
        LocalDate actualEndB = endB != null ? endB : LocalDate.MAX;
        return !startA.isAfter(actualEndB) && !startB.isAfter(actualEndA);
    }
}
