package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CalculateContributionRequestMapper {

    public ApiCalculateContributionRequest map(
            ContributionCalcParametersDTO contributionCalcParametersDTO,
            BigDecimal annualDisposableIncome,
            Boolean isUpliftApplied,
            BigDecimal contributionCap) {
        return new ApiCalculateContributionRequest()
                .withAnnualDisposableIncome(annualDisposableIncome)
                .withUpliftApplied(isUpliftApplied)
                .withUpliftedIncomePercent(contributionCalcParametersDTO.getUpliftedIncomePercent())
                .withMinUpliftedMonthlyAmount(contributionCalcParametersDTO.getMinUpliftedMonthlyAmount())
                .withDisposableIncomePercent(contributionCalcParametersDTO.getDisposableIncomePercent())
                .withMinimumMonthlyAmount(contributionCalcParametersDTO.getMinimumMonthlyAmount())
                .withUpfrontTotalMonths(contributionCalcParametersDTO.getUpfrontTotalMonths())
                .withContributionCap(contributionCap);
    }
}
