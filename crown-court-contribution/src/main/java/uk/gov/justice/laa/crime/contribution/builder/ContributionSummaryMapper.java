package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

@Component
@AllArgsConstructor
public class ContributionSummaryMapper {
    public ApiContributionSummary map(ContributionsSummaryDTO contributionsSummaryDTO) {
        return new ApiContributionSummary()
                .withId(contributionsSummaryDTO.getId())
                .withMonthlyContributions(contributionsSummaryDTO.getMonthlyContributions())
                .withUpfrontContributions(contributionsSummaryDTO.getUpfrontContributions())
                .withBasedOn(contributionsSummaryDTO.getBasedOn())
                .withUpliftApplied(contributionsSummaryDTO.getUpliftApplied())
                .withEffectiveDate(convertDateToDateTime(contributionsSummaryDTO.getEffectiveDate()))
                .withCalcDate(convertDateToDateTime(contributionsSummaryDTO.getCalcDate()))
                .withFileName(contributionsSummaryDTO.getFileName())
                .withDateSent(convertDateToDateTime(contributionsSummaryDTO.getDateSent()))
                .withDateReceived(convertDateToDateTime(contributionsSummaryDTO.getDateReceived()));
    }
}
