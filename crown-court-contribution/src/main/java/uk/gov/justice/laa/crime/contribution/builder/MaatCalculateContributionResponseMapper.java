package uk.gov.justice.laa.crime.contribution.builder;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

import lombok.AllArgsConstructor;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.ContributionResult;
import uk.gov.justice.laa.crime.contribution.util.DateUtil;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MaatCalculateContributionResponseMapper {
    public ApiMaatCalculateContributionResponse map(
            ContributionResult result,
            Contribution createdContribution,
            ContributionResponseDTO contributionResponseDTO) {
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse()
                .withContributionCap(result.contributionCap())
                .withEffectiveDate(DateUtil.convertDateToDateTime(result.effectiveDate()))
                .withTotalMonths(result.totalMonths())
                .withMonthlyContributions(result.monthlyAmount())
                .withUpfrontContributions(result.upfrontAmount())
                .withUpliftApplied(result.isUplift() ? "Y" : "N")
                .withBasedOn(result.basedOn());

        if (createdContribution != null) {
            response.setContributionId(createdContribution.getId());
            response.setCalcDate(DateUtil.convertDateToDateTime(createdContribution.getCalcDate()));
        }

        if (contributionResponseDTO.getId() != null) {
            response.setProcessActivity(true);
        }
        return response;
    }

    public ApiMaatCalculateContributionResponse map(Contribution contribution) {
        return new ApiMaatCalculateContributionResponse()
                .withContributionId(contribution.getId())
                .withRepId(contribution.getRepId())
                .withEffectiveDate(convertDateToDateTime(contribution.getEffectiveDate()))
                .withCalcDate(convertDateToDateTime(contribution.getCalcDate()))
                .withContributionCap(contribution.getContributionCap())
                .withMonthlyContributions(contribution.getMonthlyContributions())
                .withUpfrontContributions(contribution.getUpfrontContributions())
                .withUpliftApplied(contribution.getUpliftApplied())
                .withBasedOn(contribution.getBasedOn())
                .withCreateContributionOrder(contribution.getCreateContributionOrder())
                .withReplacedDate(convertDateToDateTime(contribution.getReplacedDate()))
                .withCcOutcomeCount(contribution.getCcOutcomeCount());
    }
}
