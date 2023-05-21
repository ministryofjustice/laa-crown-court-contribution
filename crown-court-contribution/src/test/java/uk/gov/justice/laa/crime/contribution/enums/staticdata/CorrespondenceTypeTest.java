package uk.gov.justice.laa.crime.contribution.enums.staticdata;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CorrespondenceType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CorrespondenceTypeTest {

    @Test
    void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(CorrespondenceType.getFrom("CONTRIBUTION_NOTICE")).isEqualTo(CorrespondenceType.CONTRIBUTION_NOTICE);
    }

    @Test
    void givenBlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(CorrespondenceType.getFrom(null)).isNull();
    }

    @Test
    void givenInvalidResultString_whenGetFromIsInvoked_thenExceptionIsThrown() {
        assertThatThrownBy(
                () -> CorrespondenceType.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidInput_ValidateEnumValues() {
        assertThat("CONTRIBUTION_NOTICE").isEqualTo(CorrespondenceType.CONTRIBUTION_NOTICE.getCode());
        assertThat("Contribution Notice").isEqualTo(CorrespondenceType.CONTRIBUTION_NOTICE.getDescription());
        assertThat("Rhybudd Cyfrannu").isEqualTo(CorrespondenceType.CONTRIBUTION_NOTICE.getWelshDescription());
    }
}
