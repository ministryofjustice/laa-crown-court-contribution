package uk.gov.justice.laa.crime.contribution.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class CalculateContributionUtil {

    public BigDecimal calculateMonthlyContribution(final BigDecimal annualDisposableIncome, final BigDecimal disposableIncomePercent, final BigDecimal minimumMonthlyAmount) {
        BigDecimal monthlyContributionsCalc = annualDisposableIncome.divide(BigDecimal.valueOf(12), RoundingMode.FLOOR)
                .multiply(disposableIncomePercent)
                .divide(BigDecimal.valueOf(100), RoundingMode.FLOOR);
        BigDecimal monthlyContribution = (monthlyContributionsCalc.compareTo(BigDecimal.ZERO) > 0) ? monthlyContributionsCalc : BigDecimal.ZERO;
        if (monthlyContribution.compareTo(minimumMonthlyAmount) < 0) {
            return BigDecimal.ZERO;
        }
        return monthlyContribution;
    }

    public BigDecimal calculateUpfrontContributions(final BigDecimal monthlyContributions, final BigDecimal contributionCap, final int upfrontTotalMonths) {
        BigDecimal upfrontContribution = monthlyContributions.multiply(BigDecimal.valueOf(upfrontTotalMonths));
        if (upfrontContribution.compareTo(contributionCap) < 0) {
            return upfrontContribution;
        }
        return contributionCap;
    }

    public BigDecimal calculateUpliftedMonthlyAmount(final BigDecimal annualDisposableIncome, final BigDecimal upliftedIncomePercent, final BigDecimal minUpliftedMonthlyAmount) {
        BigDecimal monthlyContributionsCalc = annualDisposableIncome.divide(BigDecimal.valueOf(12), RoundingMode.FLOOR)
                .multiply(upliftedIncomePercent)
                .divide(BigDecimal.valueOf(100), RoundingMode.FLOOR);
        if (monthlyContributionsCalc.compareTo(minUpliftedMonthlyAmount) > 0) {
            return monthlyContributionsCalc;
        }
        return minUpliftedMonthlyAmount;
    }
}
