package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionVariationDTO;
import uk.gov.justice.laa.crime.contribution.model.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.contribution.staticdata.repository.ContributionRulesRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContributionRulesServiceTest {
    @InjectMocks
    private ContributionRulesService contributionRulesService;
    @Mock
    private ContributionRulesRepository contributionRulesRepository;
    @Mock
    private ApiCrownCourtSummary apiCrownCourtSummary;

    @Test
    void givenEitherWayCaseType_whenGetContributionVariationIsInvoked_thenReturnCorrectResponse() {
        when(contributionRulesRepository.findContributionRulesEntitiesByCaseTypeAndVariationNotNullAndMagistratesCourtOutcomeAndCrownCourtOutcome(
                CaseType.EITHER_WAY.getCaseTypeString(), null, null))
                .thenReturn(TestModelDataBuilder.getContributionRules());

        Optional<ContributionVariationDTO> response = contributionRulesService.getContributionVariation(
                CaseType.EITHER_WAY, null, null);
        assertThat(response.isPresent() ? response.get() : Optional.empty()).isEqualTo(TestModelDataBuilder.getContributionVariationDTO());
    }

    @Test
    void givenEitherWayCaseTypeAndValidMagCourtOutcome_whenGetContributionVariationIsInvoked_thenReturnCorrectResponse() {
        when(contributionRulesRepository.findContributionRulesEntitiesByCaseTypeAndVariationNotNullAndMagistratesCourtOutcomeAndCrownCourtOutcome(
                CaseType.EITHER_WAY.getCaseTypeString(), MagCourtOutcome.COMMITTED.getOutcome(), null))
                .thenReturn(TestModelDataBuilder.getContributionRules());

        Optional<ContributionVariationDTO> response = contributionRulesService.getContributionVariation(
                CaseType.EITHER_WAY, MagCourtOutcome.COMMITTED, null);
        assertThat(response.isPresent() ? response.get() : Optional.empty()).isEqualTo(TestModelDataBuilder.getContributionVariationDTO());
    }

    @Test
    void givenEitherWayCaseTypeAndAValidCCOutcome_whenGetContributionVariationIsInvoked_thenReturnEmptyResponse() {
        when(contributionRulesRepository.findContributionRulesEntitiesByCaseTypeAndVariationNotNullAndMagistratesCourtOutcomeAndCrownCourtOutcome(
                CaseType.EITHER_WAY.getCaseTypeString(), null, CrownCourtOutcome.PART_CONVICTED.getCode()))
                .thenReturn(null);

        Optional<ContributionVariationDTO> response = contributionRulesService.getContributionVariation(
                CaseType.EITHER_WAY, null, CrownCourtOutcome.PART_CONVICTED);
        assertThat(response).isEmpty();
    }

    @Test
    void givenIndictableCaseTypeWithValidOutcomes_whenGetContributionVariationIsInvoked_thenReturnEmptyResponse() {
        when(contributionRulesRepository.findContributionRulesEntitiesByCaseTypeAndVariationNotNullAndMagistratesCourtOutcomeAndCrownCourtOutcome(
                CaseType.INDICTABLE.getCaseTypeString(), MagCourtOutcome.COMMITTED.getOutcome(), CrownCourtOutcome.AQUITTED.getCode()))
                .thenReturn(null);

        Optional<ContributionVariationDTO> response = contributionRulesService.getContributionVariation(
                CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.AQUITTED);
        assertThat(response).isEmpty();
    }

    @Test
    void givenCrownCourtSummaryWithValidOutcomes_whenGetActiveCCOutcomeIsInvoked_thenValidOutcomeIsReturned() {
        assertThat(contributionRulesService.getActiveCCOutcome(TestModelDataBuilder.getApiCrownCourtSummary()))
                .isEqualTo(CrownCourtOutcome.ABANDONED);
    }

    @Test
    void givenCrownCourtSummaryWithEmptyOutcome_whenGetActiveCCOutcomeIsInvoked_thenNullIsReturned() {
        assertThat(contributionRulesService.getActiveCCOutcome(apiCrownCourtSummary)).isNull();
    }

    @Test
    void givenNoContributionRulesAvailable_whenIsContributionRuleApplicableIsInvoked_thenFalseIsReturned() {
        assertThat(contributionRulesService
                .isContributionRuleApplicable(CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.CONVICTED)).isFalse();
    }

    @Test
    void givenContributionRulesAvailable_whenIsContributionRuleApplicableIsInvoked_thenTrueIsReturned() {
        when(contributionRulesRepository.findContributionRulesEntitiesByCaseTypeAndVariationNotNullAndMagistratesCourtOutcomeAndCrownCourtOutcome(
                CaseType.EITHER_WAY.getCaseTypeString(), MagCourtOutcome.COMMITTED.getOutcome(), null))
                .thenReturn(TestModelDataBuilder.getContributionRules());
        assertThat(contributionRulesService
                .isContributionRuleApplicable(CaseType.EITHER_WAY, MagCourtOutcome.COMMITTED, null)).isTrue();
    }

}