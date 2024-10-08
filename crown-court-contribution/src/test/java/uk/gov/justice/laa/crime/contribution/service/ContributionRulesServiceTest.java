package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class ContributionRulesServiceTest {
    @InjectMocks
    private ContributionRulesService contributionRulesService;

    @Test
    void givenCrownCourtSummaryWithValidOutcomes_whenGetActiveCCOutcomeIsInvoked_thenValidOutcomeIsReturned() {
        assertThat(contributionRulesService.getActiveCCOutcome(TestModelDataBuilder.getApiCrownCourtSummaryAppeal()))
                .isEqualTo(CrownCourtOutcome.ABANDONED);
    }

    @Test
    void givenCrownCourtSummaryWithEmptyOutcome_whenGetActiveCCOutcomeIsInvoked_thenNullIsReturned() {
        assertThat(contributionRulesService.getActiveCCOutcome(List.of())).isNull();
    }

    @Test
    void givenNoContributionRulesAvailable_whenIsContributionRuleApplicableIsInvoked_thenFalseIsReturned() {
        assertThat(contributionRulesService.isContributionRuleApplicable(
                CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.CONVICTED
        )).isFalse();
    }

    @Test
    void givenEitherWayCaseResolvedInMagsCourt_whenIsContributionRuleApplicableIsInvoked_thenFalseIsReturned() {
        assertThat(contributionRulesService.isContributionRuleApplicable(
                CaseType.EITHER_WAY, MagCourtOutcome.RESOLVED_IN_MAGS, null
        )).isFalse();
    }

    @ParameterizedTest
    @MethodSource("contributionRuleApplicable")
    void givenCaseTypeAndOutcomes_whenIsContributionRuleApplicableIsInvoked_thenValidResponseReturned(
            CaseType caseType, MagCourtOutcome magCourtOutcome, CrownCourtOutcome crownCourtOutcome) {
        assertThat(contributionRulesService.isContributionRuleApplicable(
                caseType, magCourtOutcome, crownCourtOutcome
        )).isTrue();
    }

    private static Stream<Arguments> contributionRuleApplicable() {
        return Stream.of(
                Arguments.of(CaseType.EITHER_WAY, null, null),
                Arguments.of(CaseType.EITHER_WAY, MagCourtOutcome.COMMITTED_FOR_TRIAL, null),
                Arguments.of(CaseType.EITHER_WAY, MagCourtOutcome.COMMITTED, null),
                Arguments.of(CaseType.EITHER_WAY, MagCourtOutcome.SENT_FOR_TRIAL, null),
                Arguments.of(CaseType.EITHER_WAY, MagCourtOutcome.APPEAL_TO_CC, null)
        );
    }
}