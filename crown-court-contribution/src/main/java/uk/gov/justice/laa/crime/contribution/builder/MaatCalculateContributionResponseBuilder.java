package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiMaatCalculateContributionResponse;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;
import static uk.gov.justice.laa.crime.contribution.util.DateUtil.getLocalDateString;

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
