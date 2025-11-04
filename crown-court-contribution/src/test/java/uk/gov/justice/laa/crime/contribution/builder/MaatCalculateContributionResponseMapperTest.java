package uk.gov.justice.laa.crime.contribution.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.ContributionResult;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class MaatCalculateContributionResponseMapperTest {
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenAValidContributionResult_whenMapIsInvoked_thenReturnApiMaatCalculateContributionResponse() {
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();
        MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper =
                new MaatCalculateContributionResponseMapper();
        ApiMaatCalculateContributionResponse maatCalculateContributionResponse =
                maatCalculateContributionResponseMapper.map(
                        contributionResult,
                        null,
                        ContributionResponseDTO.builder().build());
        softly.assertThat(maatCalculateContributionResponse.getMonthlyContributions())
                .isEqualTo(contributionResult.monthlyAmount());
        softly.assertThat(maatCalculateContributionResponse.getUpfrontContributions())
                .isEqualTo(contributionResult.upfrontAmount());
        softly.assertThat(maatCalculateContributionResponse.getBasedOn()).isEqualTo(contributionResult.basedOn());
        softly.assertThat(maatCalculateContributionResponse.getUpliftApplied()).isEqualTo("N");
        softly.assertThat(maatCalculateContributionResponse.getTotalMonths())
                .isEqualTo(contributionResult.totalMonths());
        softly.assertThat(maatCalculateContributionResponse.getContributionCap())
                .isEqualTo(contributionResult.contributionCap());
        softly.assertThat(maatCalculateContributionResponse.getEffectiveDate())
                .isEqualTo(convertDateToDateTime(contributionResult.effectiveDate()));

        softly.assertThat(maatCalculateContributionResponse.getContributionId()).isNull();
        softly.assertThat(maatCalculateContributionResponse.getCalcDate()).isNull();
        softly.assertThat(maatCalculateContributionResponse.getProcessActivity())
                .isNull();
        softly.assertAll();
    }

    @Test
    void givenContributionResultWithUplift_whenMapIsInvoked_thenUpliftIsMappedCorrectly() {
        ContributionResult contributionResult =
                ContributionResult.builder().isUplift(true).build();
        MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper =
                new MaatCalculateContributionResponseMapper();
        ApiMaatCalculateContributionResponse maatCalculateContributionResponse =
                maatCalculateContributionResponseMapper.map(
                        contributionResult,
                        null,
                        ContributionResponseDTO.builder().build());
        assertThat(maatCalculateContributionResponse.getUpliftApplied()).isEqualTo("Y");
    }

    @Test
    void givenAValidateContributionCreated_whenMapIsInvoked_thenCreateContributionFieldsMappedCorrectly() {
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();
        Contribution createdContribution = TestModelDataBuilder.getContribution();
        ContributionResponseDTO contributionResponseDTO =
                ContributionResponseDTO.builder().id(1).build();
        MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper =
                new MaatCalculateContributionResponseMapper();
        ApiMaatCalculateContributionResponse maatCalculateContributionResponse =
                maatCalculateContributionResponseMapper.map(
                        contributionResult, createdContribution, contributionResponseDTO);

        softly.assertThat(maatCalculateContributionResponse.getContributionId()).isEqualTo(createdContribution.getId());
        softly.assertThat(maatCalculateContributionResponse.getCalcDate())
                .isEqualTo(convertDateToDateTime(createdContribution.getCalcDate()));
        softly.assertThat(maatCalculateContributionResponse.getProcessActivity())
                .isTrue();
        softly.assertAll();
    }

    @Test
    void givenValidContribution_whenMapIsInvoked_thenReturnApiMaatCalculateContributionResponse() {
        Contribution contribution = TestModelDataBuilder.buildContribution();
        MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper =
                new MaatCalculateContributionResponseMapper();
        ApiMaatCalculateContributionResponse response = maatCalculateContributionResponseMapper.map(contribution);
        softly.assertThat(response.getContributionId()).isEqualTo(contribution.getId());
        softly.assertThat(response.getRepId()).isEqualTo(contribution.getRepId());
        softly.assertThat(response.getEffectiveDate())
                .isEqualTo(convertDateToDateTime(contribution.getEffectiveDate()));
        softly.assertThat(response.getCalcDate()).isEqualTo(convertDateToDateTime(contribution.getCalcDate()));
        softly.assertThat(response.getContributionCap()).isEqualTo(contribution.getContributionCap());
        softly.assertThat(response.getMonthlyContributions()).isEqualTo(contribution.getMonthlyContributions());
        softly.assertThat(response.getUpfrontContributions()).isEqualTo(contribution.getUpfrontContributions());
        softly.assertThat(response.getUpliftApplied()).isEqualTo(contribution.getUpliftApplied());
        softly.assertThat(response.getBasedOn()).isEqualTo(contribution.getBasedOn());
        softly.assertThat(response.getCreateContributionOrder()).isEqualTo(contribution.getCreateContributionOrder());
        softly.assertThat(response.getReplacedDate()).isEqualTo(convertDateToDateTime(contribution.getReplacedDate()));
        softly.assertThat(response.getCcOutcomeCount()).isEqualTo(contribution.getCcOutcomeCount());
        softly.assertAll();
    }
}
