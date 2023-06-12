package uk.gov.justice.laa.crime.contribution.enums.staticdata;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.InitAssessmentResult;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class InitAssessmentResultTest {

    @Test
    void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        InitAssessmentResult result = InitAssessmentResult.getFrom("PASS");
        assertThat(InitAssessmentResult.getFrom("PASS")).isEqualTo(InitAssessmentResult.PASS);
        assertThat(Objects.requireNonNull(result).getReason())
                .isEqualTo(InitAssessmentResult.PASS.getReason());
    }

    @Test
    void givenBlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(InitAssessmentResult.getFrom(null)).isNull();
    }

    @Test
    void givenInvalidResultString_whenGetFromIsInvoked_thenExceptionIsThrown() {
        assertThatThrownBy(
                () -> InitAssessmentResult.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
