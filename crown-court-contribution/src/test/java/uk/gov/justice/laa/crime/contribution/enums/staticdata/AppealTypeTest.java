package uk.gov.justice.laa.crime.contribution.enums.staticdata;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import uk.gov.justice.laa.crime.enums.AppealType;

import org.junit.jupiter.api.Test;

class AppealTypeTest {

    @Test
    void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(AppealType.getFrom("ACN")).isEqualTo(AppealType.ACN);
    }

    @Test
    void givenBlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(AppealType.getFrom(null)).isNull();
    }

    @Test
    void givenInvalidResultString_whenGetFromIsInvoked_thenExceptionIsThrown() {
        assertThatThrownBy(() -> AppealType.getFrom("MOCK_RESULT_STRING")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidInput_ValidateEnumValues() {
        assertThat("ACN").isEqualTo(AppealType.ACN.getCode());
        assertThat("Appeal against conviction and sentence").isEqualTo(AppealType.ACS.getDescription());
    }
}
