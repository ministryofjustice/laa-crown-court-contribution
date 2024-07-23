package uk.gov.justice.laa.crime.contribution.enums.staticdata;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AppealContributionAmount;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CrownCourtAppealOutcome;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AppealContributionAmountTest {
    @ParameterizedTest
    @MethodSource("appealTypeAndAppealOutcomeCombinations")
    void givenASuccessfulAssessmentResult_thenZeroContributionAmountIsReturned(AppealType appealType, CrownCourtAppealOutcome appealOutcome) {
        AppealContributionAmount result = AppealContributionAmount.calculate(appealType, appealOutcome, AssessmentResult.PASS);

        assertThat(result).isEqualTo(AppealContributionAmount.NO_CONTRIBUTION);
    }

    @ParameterizedTest
    @MethodSource("appealTypes")
    void givenAFailedAssessmentResultAndSuccessfulAppealOutcome_thenZeroContributionAmountIsReturned(AppealType appealType) {
        AppealContributionAmount result = AppealContributionAmount.calculate(appealType, CrownCourtAppealOutcome.SUCCESSFUL, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.NO_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsACNAndPartSuccessAppealOutcome_thenZeroCContributionIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ACN, CrownCourtAppealOutcome.PART_SUCCESS, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.NO_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsASEAndPartSuccessAppealOutcome_thenZeroCContributionIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ASE, CrownCourtAppealOutcome.PART_SUCCESS, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.NO_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsACSAndPartSuccessfulAppealOutcome_thenPartialContributionAmountIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ACS, CrownCourtAppealOutcome.PART_SUCCESS, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.PART_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsASEAndUnsuccessfulAppealOutcome_thenPartialContributionAmountIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ASE, CrownCourtAppealOutcome.UNSUCCESSFUL, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.PART_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsACNAndUnsuccessfulAppealOutcome_thenFullContributionAmountIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ACN, CrownCourtAppealOutcome.UNSUCCESSFUL, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.FULL_CONTRIBUTION);
    }

    @Test
    void givenAppealTypeIsACSAndUnsuccessfulAppealOutcome_thenFullContributionAmountIsReturned() {
        AppealContributionAmount result = AppealContributionAmount.calculate(AppealType.ACS, CrownCourtAppealOutcome.UNSUCCESSFUL, AssessmentResult.FAIL);

        assertThat(result).isEqualTo(AppealContributionAmount.FULL_CONTRIBUTION);
    }

    private static Stream<Arguments> appealTypeAndAppealOutcomeCombinations() {
        return Stream.of(
                Arguments.arguments(AppealType.ACS, CrownCourtAppealOutcome.SUCCESSFUL),
                Arguments.arguments(AppealType.ACS, CrownCourtAppealOutcome.PART_SUCCESS),
                Arguments.arguments(AppealType.ACS, CrownCourtAppealOutcome.UNSUCCESSFUL),
                Arguments.arguments(AppealType.ACN, CrownCourtAppealOutcome.SUCCESSFUL),
                Arguments.arguments(AppealType.ACN, CrownCourtAppealOutcome.PART_SUCCESS),
                Arguments.arguments(AppealType.ACN, CrownCourtAppealOutcome.UNSUCCESSFUL),
                Arguments.arguments(AppealType.ASE, CrownCourtAppealOutcome.SUCCESSFUL),
                Arguments.arguments(AppealType.ASE, CrownCourtAppealOutcome.PART_SUCCESS),
                Arguments.arguments(AppealType.ASE, CrownCourtAppealOutcome.UNSUCCESSFUL)
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
