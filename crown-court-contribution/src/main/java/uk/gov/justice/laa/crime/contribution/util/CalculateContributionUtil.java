package uk.gov.justice.laa.crime.contribution.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;

@UtilityClass
public class CalculateContributionUtil {

    public BigDecimal calculateMonthlyContribution(final BigDecimal annualDisposableIncome, final BigDecimal disposableIncomePercent, final BigDecimal minimumMonthlyAmount) {
        if(checkNull(annualDisposableIncome, disposableIncomePercent, minimumMonthlyAmount))
            return null;
        BigDecimal monthlyContributionsCalc = annualDisposableIncome.divide(BigDecimal.valueOf(12), RoundingMode.FLOOR)
                .multiply(disposableIncomePercent)
                .divide(BigDecimal.valueOf(100), RoundingMode.FLOOR);
        BigDecimal monthlyContribution = (monthlyContributionsCalc.compareTo(BigDecimal.ZERO) > 0) ? monthlyContributionsCalc : BigDecimal.ZERO;
        if (monthlyContribution.compareTo(minimumMonthlyAmount) < 0) {
            return BigDecimal.ZERO;
        }
        return monthlyContribution;
    }

    public BigDecimal calculateUpfrontContributions(final BigDecimal monthlyContributions, final BigDecimal contributionCap, final Integer upfrontTotalMonths) {
        if(checkNull(monthlyContributions, contributionCap) || upfrontTotalMonths == null)
            return null;
        BigDecimal upfrontContribution = monthlyContributions.multiply(BigDecimal.valueOf(upfrontTotalMonths));
        if (upfrontContribution.compareTo(contributionCap) < 0) {
            return upfrontContribution;
        } else return contributionCap;
    }

    public BigDecimal calculateUpliftedMonthlyAmount(final BigDecimal annualDisposableIncome, final BigDecimal upliftedIncomePercent, final BigDecimal minUpliftedMonthlyAmount) {
        if(checkNull(annualDisposableIncome, upliftedIncomePercent, minUpliftedMonthlyAmount))
            return null;
        BigDecimal monthlyContributionsCalc = annualDisposableIncome.divide(BigDecimal.valueOf(12), RoundingMode.FLOOR)
                .multiply(upliftedIncomePercent)
                .divide(BigDecimal.valueOf(100), RoundingMode.FLOOR);
        if (monthlyContributionsCalc.compareTo(minUpliftedMonthlyAmount) > 0) {
            return monthlyContributionsCalc;
        }
        return minUpliftedMonthlyAmount;
    }

    private static boolean checkNull(BigDecimal... values){
        return Arrays.stream(values).anyMatch(Objects::isNull);
    }
}
