package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaatCalculateContributionResponseBuilder {

    public static ApiMaatCalculateContributionResponse build(Contribution contribution) {
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
