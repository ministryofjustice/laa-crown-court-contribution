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
        MaatCalculateContributionResponse maatCalculateContributionResponse = new MaatCalculateContributionResponse();
        maatCalculateContributionResponse.withContributionCap(contributionCap);
        maatCalculateContributionResponse.withEffectiveDate(effectiveDate);
        maatCalculateContributionResponse.withTotalMonths(totalMonths);
        maatCalculateContributionResponse.withMonthlyContributions(apiCalculateContributionResponse.getMonthlyContributions());
        maatCalculateContributionResponse.withUpfrontContributions(apiCalculateContributionResponse.getUpfrontContributions());
        maatCalculateContributionResponse.withUpliftApplied(apiCalculateContributionResponse.getUpliftApplied());
        maatCalculateContributionResponse.withBasedOn(apiCalculateContributionResponse.getBasedOn());
        return maatCalculateContributionResponse;
    }
}
