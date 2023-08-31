package uk.gov.justice.laa.crime.contribution.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DateUtil {

    private DateUtil() {}
    public static LocalDate parse(final String date) {
        return date != null ? LocalDate.parse(date) : null;
    }

    public static LocalDate parseLocalDate(final LocalDateTime date) {
        return date != null ? date.toLocalDate() : null;
    }

    public static String getLocalDateString(final LocalDate date) {
        return date != null ? date.toString() : null;
    }


    public static LocalDateTime convertDateToDateTime(LocalDate date) {
        if (date != null) {
            return date.atTime(0, 0);
        } else return null;
    }
}
