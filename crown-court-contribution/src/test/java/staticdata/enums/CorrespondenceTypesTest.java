package staticdata.enums;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CorrespondenceTypes;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CorrespondenceTypesTest {

    @Test
    void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(CorrespondenceTypes.getFrom("CONTRIBUTION_NOTICE")).isEqualTo(CorrespondenceTypes.CONTRIBUTION_NOTICE);
    }

    @Test
    void givenBlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(CorrespondenceTypes.getFrom(null)).isNull();
    }

    @Test
    void givenInvalidResultString_whenGetFromIsInvoked_thenExceptionIsThrown() {
        assertThatThrownBy(
                () -> CorrespondenceTypes.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidInput_ValidateEnumValues() {
        assertThat("CONTRIBUTION_NOTICE").isEqualTo(CorrespondenceTypes.CONTRIBUTION_NOTICE.getCode());
        assertThat("Contribution Notice").isEqualTo(CorrespondenceTypes.CONTRIBUTION_NOTICE.getDescription());
        assertThat("Rhybudd Cyfrannu").isEqualTo(CorrespondenceTypes.CONTRIBUTION_NOTICE.getWelshDescription());
    }
}
