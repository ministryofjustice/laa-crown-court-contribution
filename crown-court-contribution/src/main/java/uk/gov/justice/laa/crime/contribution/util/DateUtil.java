package uk.gov.justice.laa.crime.contribution.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DateUtil {

    public static LocalDate parse(final String date) {
        return date != null ? LocalDate.parse(date) : null;
    }

    public static LocalDate parse(final LocalDateTime date) {
        return date != null ? date.toLocalDate() : null;
    }

    public static LocalDateTime parseDateTime(final String dateTime) {
        return dateTime != null ? LocalDateTime.parse(dateTime) : null;
    }
}
