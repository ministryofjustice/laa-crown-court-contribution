package uk.gov.justice.laa.crime.contribution.enums.staticdata;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AppealContributionAmount;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AppealContributionAmountTest {
    @ParameterizedTest
    @MethodSource("appealTypeAndAppealOutcomeCombinations")
    void givenASuccessfulAssessmentResult_thenZeroContributionAmountIsReturned(AppealType appealType, CrownCourtOutcome appealOutcome) {
        AppealContributionAmount result = AppealContributionAmount.calculate(appealType, appealOutcome, AssessmentResult.PASS);

        assertThat(result).isEqualTo(AppealContributionAmount.NO_CONTRIBUTION);
    }

    @ParameterizedTest
    @MethodSource("appealTypes")
    void givenAFailedAssessmentResultAndSuccessfulAppealOutcome_thenZeroContributionAmountIsReturned(AppealType appealType) {
        AppealContributionAmount result = AppealContributionAmount.calculate(appealType, CrownCourtOutcome.SUCCESSFUL, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.NO_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsACNAndPartSuccessAppealOutcome_thenZeroCContributionIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ACN, CrownCourtOutcome.PART_SUCCESS, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.NO_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsASEAndPartSuccessAppealOutcome_thenZeroCContributionIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ASE, CrownCourtOutcome.PART_SUCCESS, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.NO_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsACSAndPartSuccessfulAppealOutcome_thenPartialContributionAmountIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ACS, CrownCourtOutcome.PART_SUCCESS, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.PART_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsASEAndUnsuccessfulAppealOutcome_thenPartialContributionAmountIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ASE, CrownCourtOutcome.UNSUCCESSFUL, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.PART_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsACNAndUnsuccessfulAppealOutcome_thenFullContributionAmountIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ACN, CrownCourtOutcome.UNSUCCESSFUL, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.FULL_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsACSAndUnsuccessfulAppealOutcome_thenFullContributionAmountIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ACS, CrownCourtOutcome.UNSUCCESSFUL, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.FULL_CONTRIBUTION);
    }

    private static Stream<Arguments> appealTypeAndAppealOutcomeCombinations() {
        return Stream.of(
                Arguments.arguments(AppealType.ACS, CrownCourtOutcome.SUCCESSFUL),
                Arguments.arguments(AppealType.ACS, CrownCourtOutcome.PART_SUCCESS),
                Arguments.arguments(AppealType.ACS, CrownCourtOutcome.UNSUCCESSFUL),
                Arguments.arguments(AppealType.ACN, CrownCourtOutcome.SUCCESSFUL),
                Arguments.arguments(AppealType.ACN, CrownCourtOutcome.PART_SUCCESS),
                Arguments.arguments(AppealType.ACN, CrownCourtOutcome.UNSUCCESSFUL),
                Arguments.arguments(AppealType.ASE, CrownCourtOutcome.SUCCESSFUL),
                Arguments.arguments(AppealType.ASE, CrownCourtOutcome.PART_SUCCESS),
                Arguments.arguments(AppealType.ASE, CrownCourtOutcome.UNSUCCESSFUL)
        );
    }

    private static Stream<Arguments> appealTypes() {
        return Stream.of(
                Arguments.arguments(AppealType.ACS),
                Arguments.arguments(AppealType.ACN),
                Arguments.arguments(AppealType.ASE)
        );
    }
}
