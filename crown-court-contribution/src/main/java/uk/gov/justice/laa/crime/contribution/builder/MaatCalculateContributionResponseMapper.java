package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.MaatCalculateContributionResponse;

import java.math.BigDecimal;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaatCalculateContributionResponseMapper {

    public static MaatCalculateContributionResponse map(ApiCalculateContributionResponse apiCalculateContributionResponse,
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
