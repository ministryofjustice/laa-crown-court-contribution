package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.CaseType;

import java.math.BigDecimal;

@ExtendWith(SoftAssertionsExtension.class)
class ContributionDTOBuilderTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @BeforeEach
    void setUp() {
    }

    @Test
    void givenAValidRequestWithoutContributions_whenBuildIsInvoked_thenACalculateContributionDTOWithZeroContributionsIsCreated() {
        ApiMaatCalculateContributionRequest contributionRequest = TestModelDataBuilder.buildAppealContributionRequest();

        CalculateContributionDTO actualContributionDTO = ContributionDTOBuilder.build(contributionRequest);

        softly.assertThat(actualContributionDTO.getContributionId()).isNull();
        softly.assertThat(actualContributionDTO.getApplicantId()).isEqualTo(999);
        softly.assertThat(actualContributionDTO.getRepId()).isEqualTo(999);
        softly.assertThat(actualContributionDTO.getEffectiveDate()).isNull();
        softly.assertThat(actualContributionDTO.getContributionCap()).isNull();
        softly.assertThat(actualContributionDTO.getMonthlyContributions()).isNull();
        softly.assertThat(actualContributionDTO.getUpfrontContributions()).isEqualTo(BigDecimal.ZERO);
        softly.assertThat(actualContributionDTO.getDateUpliftApplied()).isNull();
        softly.assertThat(actualContributionDTO.getDateUpliftRemoved()).isNull();
        softly.assertThat(actualContributionDTO.getCaseType()).isEqualTo(CaseType.APPEAL_CC);
        softly.assertThat(actualContributionDTO.getAssessments()).hasSize(1);
        softly.assertThat(actualContributionDTO.getAppealType()).isEqualTo(AppealType.ACS);
        softly.assertThat(actualContributionDTO.getRemoveContribs()).isNull();
        softly.assertThat(actualContributionDTO.getCommittalDate()).isNull();
        softly.assertThat(actualContributionDTO.getMagCourtOutcome()).isNull();
        softly.assertThat(actualContributionDTO.getCrownCourtOutcomeList()).isEmpty();
        softly.assertThat(actualContributionDTO.getDisposableIncomeAfterCrownHardship()).isNull();
        softly.assertThat(actualContributionDTO.getDisposableIncomeAfterMagHardship()).isNull();
        softly.assertThat(actualContributionDTO.getTotalAnnualDisposableIncome()).isNull();

        softly.assertAll();
    }

    @Test
    void givenAValidRequestWithContributions_whenBuildIsInvoked_thenACalculateContributionDTOWithContributionsIsCreated() {
        ApiMaatCalculateContributionRequest contributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        contributionRequest.setMonthlyContributions(BigDecimal.valueOf(123));
        contributionRequest.setUpfrontContributions(BigDecimal.valueOf(123));

        CalculateContributionDTO actualContributionDTO = ContributionDTOBuilder.build(contributionRequest);

        softly.assertThat(actualContributionDTO.getContributionId()).isNull();
        softly.assertThat(actualContributionDTO.getApplicantId()).isEqualTo(999);
        softly.assertThat(actualContributionDTO.getRepId()).isEqualTo(999);
        softly.assertThat(actualContributionDTO.getEffectiveDate()).isNull();
        softly.assertThat(actualContributionDTO.getContributionCap()).isNull();
        softly.assertThat(actualContributionDTO.getMonthlyContributions()).isEqualTo(BigDecimal.valueOf(123));
        softly.assertThat(actualContributionDTO.getUpfrontContributions()).isEqualTo(BigDecimal.valueOf(123));
        softly.assertThat(actualContributionDTO.getDateUpliftApplied()).isNull();
        softly.assertThat(actualContributionDTO.getDateUpliftRemoved()).isNull();
        softly.assertThat(actualContributionDTO.getCaseType()).isEqualTo(CaseType.APPEAL_CC);
        softly.assertThat(actualContributionDTO.getAssessments()).hasSize(1);
        softly.assertThat(actualContributionDTO.getAppealType()).isEqualTo(AppealType.ACS);
        softly.assertThat(actualContributionDTO.getRemoveContribs()).isNull();
        softly.assertThat(actualContributionDTO.getCommittalDate()).isNull();
        softly.assertThat(actualContributionDTO.getMagCourtOutcome()).isNull();
        softly.assertThat(actualContributionDTO.getCrownCourtOutcomeList()).isEmpty();
        softly.assertThat(actualContributionDTO.getDisposableIncomeAfterCrownHardship()).isNull();
        softly.assertThat(actualContributionDTO.getDisposableIncomeAfterMagHardship()).isNull();
        softly.assertThat(actualContributionDTO.getTotalAnnualDisposableIncome()).isNull();

        softly.assertAll();
    }
}