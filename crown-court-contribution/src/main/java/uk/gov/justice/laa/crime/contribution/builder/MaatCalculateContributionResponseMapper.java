package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.util.DateUtil;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class MaatCalculateContributionResponseMapper {

    public ApiMaatCalculateContributionResponse map(ApiCalculateContributionResponse apiCalculateContributionResponse,
                                                    BigDecimal contributionCap, String effectiveDate,
                                                    Integer totalMonths) {
        return new ApiMaatCalculateContributionResponse()
                .withContributionCap(contributionCap)
                .withEffectiveDate(DateUtil.convertDateToDateTime(DateUtil.parse(effectiveDate)))
                .withTotalMonths(totalMonths)
                .withMonthlyContributions(apiCalculateContributionResponse.getMonthlyContributions())
                .withUpfrontContributions(apiCalculateContributionResponse.getUpfrontContributions())
                .withUpliftApplied(apiCalculateContributionResponse.getUpliftApplied())
                .withBasedOn(apiCalculateContributionResponse.getBasedOn());
    }
}
