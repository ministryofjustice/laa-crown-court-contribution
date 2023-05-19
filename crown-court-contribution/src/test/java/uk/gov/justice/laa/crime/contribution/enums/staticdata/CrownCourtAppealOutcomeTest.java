package uk.gov.justice.laa.crime.contribution.enums.staticdata;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CrownCourtAppealOutcome;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CrownCourtAppealOutcomeTest {

    @Test
    void givenABlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(CrownCourtAppealOutcome.getFrom(null)).isNull();
    }

    @Test
    void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(CrownCourtAppealOutcome.getFrom("SUCCESSFUL"))
                .isEqualTo(CrownCourtAppealOutcome.SUCCESSFUL);
    }

    @Test
    void valueOfCurrentStatusFromString_nullParameter_ReturnsNull() {
        assertThatThrownBy(
                () -> CaseType.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidInput_ValidateEnumValues() {
        assertThat("UNSUCCESSFUL").isEqualTo(CrownCourtAppealOutcome.UNSUCCESSFUL.getValue());
    }
}
