package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.maat_api.MaatCalculateContributionResponse;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class MaatCalculateContributionResponseMapper {

    public MaatCalculateContributionResponse map(ApiCalculateContributionResponse apiCalculateContributionResponse,
                                                 BigDecimal contributionCap, String effectiveDate,
                                                 Integer totalMonths) {
        return new MaatCalculateContributionResponse()
                .withContributionCap(contributionCap)
                .withEffectiveDate(effectiveDate)
                .withTotalMonths(totalMonths)
                .withMonthlyContributions(apiCalculateContributionResponse.getMonthlyContributions())
                .withUpfrontContributions(apiCalculateContributionResponse.getUpfrontContributions())
                .withUpliftApplied(apiCalculateContributionResponse.getUpliftApplied())
                .withBasedOn(apiCalculateContributionResponse.getBasedOn());
    }
}
