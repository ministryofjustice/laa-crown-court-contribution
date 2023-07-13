package uk.gov.justice.laa.crime.contribution.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DateUtilTest {

    @Test
    void givenAEmptyStringDate_whenParseIsInvoke_thenReturnNull() {
        String dateModified = null;
        assertThat(DateUtil.parse(dateModified)).isNull();
    }

    @Test
    void givenAValidDate_whenParseIsInvoke_thenReturnDate() {
        String dateModified = "2023-01-01";
        assertThat(DateUtil.parse(dateModified)).isNotNull();
    }

    @Test
    void givenAEmptyLocalDate_whenParseIsInvoke_thenReturnNull() {
        LocalDateTime dateModified = null;
        assertThat(DateUtil.parseLocalDate(dateModified)).isNull();
    }

    @Test
    void givenAValidLocalDate_whenParseIsInvoke_thenReturnDate() {
        LocalDateTime dateModified = LocalDateTime.now();
        assertThat(DateUtil.parseLocalDate(dateModified)).isNotNull();
    }

}