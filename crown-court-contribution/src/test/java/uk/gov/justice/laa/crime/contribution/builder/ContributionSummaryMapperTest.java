package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

@ExtendWith(SoftAssertionsExtension.class)
class ContributionSummaryMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenAValidContributionsSummaryDTO_whenMapIsInvoked_thenReturnContributionsSummary() {

        ContributionsSummaryDTO contributionsSummaryDTO = TestModelDataBuilder.getContributionSummaryDTO();
        ContributionSummaryMapper mapper = new ContributionSummaryMapper();

        ApiContributionSummary contributionSummary = mapper.map(contributionsSummaryDTO);

        softly.assertThat(contributionSummary.getId()).isEqualTo(contributionsSummaryDTO.getId());
        softly.assertThat(contributionSummary.getMonthlyContributions()).isEqualTo(contributionsSummaryDTO.getMonthlyContributions());
        softly.assertThat(contributionSummary.getUpfrontContributions()).isEqualTo(contributionsSummaryDTO.getUpfrontContributions());
        softly.assertThat(contributionSummary.getBasedOn()).isEqualTo(contributionsSummaryDTO.getBasedOn());
        softly.assertThat(contributionSummary.getUpliftApplied()).isEqualTo(contributionsSummaryDTO.getUpliftApplied());
        softly.assertThat(contributionSummary.getEffectiveDate()).isEqualTo(convertDateToDateTime(contributionsSummaryDTO.getEffectiveDate()));
        softly.assertThat(contributionSummary.getCalcDate()).isEqualTo(convertDateToDateTime(contributionsSummaryDTO.getCalcDate()));
        softly.assertThat(contributionSummary.getFileName()).isEqualTo(contributionsSummaryDTO.getFileName());
        softly.assertThat(contributionSummary.getDateSent()).isEqualTo(convertDateToDateTime(contributionsSummaryDTO.getDateSent()));
        softly.assertThat(contributionSummary.getDateReceived()).isEqualTo(convertDateToDateTime(contributionsSummaryDTO.getDateReceived()));
        softly.assertAll();
    }

}