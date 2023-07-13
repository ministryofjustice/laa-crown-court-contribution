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
}
