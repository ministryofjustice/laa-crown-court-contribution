package uk.gov.justice.laa.crime.contribution.enums.staticdata;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class AssessmentStatusTest {

    @Test
    void givenValidValue_whenGetFromIsInvoked_thenValidAssessmentStatusIsReturned() {
        assertThat(AssessmentStatus.getFrom("IN PROGRESS")).isEqualTo(AssessmentStatus.IN_PROGRESS);
    }

    @Test
    void givenBlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(AssessmentStatus.getFrom("")).isNull();
    }

    @Test
    void givenInvalidValue_whenGetFromIsInvoked_thenExceptionIsRaised() {
        assertThatThrownBy(() -> AssessmentStatus.getFrom("FLIBBLE")).isInstanceOf(IllegalArgumentException.class);
    }
}
