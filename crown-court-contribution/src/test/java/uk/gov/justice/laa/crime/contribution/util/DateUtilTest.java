package uk.gov.justice.laa.crime.contribution.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DateUtilTest {

    @Test
    void givenAEmptyStringDate_whenParseIsInvoked_thenReturnNull() {
        assertThat(DateUtil.parse(null)).isNull();
    }

    @Test
    void givenAValidDate_whenParseIsInvoked_thenReturnDate() {
        String dateModified = "2023-01-01";
        assertThat(DateUtil.parse(dateModified)).isNotNull();
    }

    @Test
    void givenAEmptyLocalDate_whenParseIsInvoked_thenReturnNull() {
        assertThat(DateUtil.parseLocalDate(null)).isNull();
    }

    @Test
    void givenAValidLocalDate_whenParseIsInvoked_thenReturnDate() {
        LocalDateTime dateModified = LocalDateTime.now();
        assertThat(DateUtil.parseLocalDate(dateModified)).isNotNull();
    }

    @Test
    void givenAValidLocalDate_whenConvertDateToDateTimeIsInvoked_thenReturnDateTime() {
        LocalDate dateModified = LocalDate.now();
        assertThat(DateUtil.convertDateToDateTime(dateModified)).isNotNull();
    }

    @Test
    void givenAEmptyLocalDate_whenConvertDateToDateTimeIsInvoked_thenReturnNull() {
        assertThat(DateUtil.convertDateToDateTime(null)).isNull();
    }
}