package uk.gov.justice.laa.crime.contribution.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;

@UtilityClass
@Slf4j
public class CalculateContributionUtil {

    public BigDecimal calculateMonthlyContribution(final BigDecimal annualDisposableIncome, final BigDecimal disposableIncomePercent, final BigDecimal minimumMonthlyAmount) {
        log.info("annualDisposableIncome-->" + annualDisposableIncome);
        log.info("disposableIncomePercent-->" + disposableIncomePercent);
        log.info("minimumMonthlyAmount-->" + minimumMonthlyAmount);
        if(checkNull(annualDisposableIncome, disposableIncomePercent, minimumMonthlyAmount))
            return null;
        BigDecimal monthlyContributionsCalc = BigDecimal.valueOf(Math.floor((annualDisposableIncome.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128)
                .multiply(disposableIncomePercent)
                .divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)).doubleValue())).setScale(2);
        log.info("monthlyContributionsCalc-->" + monthlyContributionsCalc);
        BigDecimal monthlyContribution = (monthlyContributionsCalc.compareTo(BigDecimal.ZERO) > 0) ? monthlyContributionsCalc : BigDecimal.ZERO;
        log.info("monthlyContribution-->" + monthlyContribution);
        if (monthlyContribution.compareTo(minimumMonthlyAmount) < 0) {
            return BigDecimal.ZERO;
        }
        log.info(" return monthlyContribution-->" + monthlyContribution);
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
        BigDecimal monthlyContributionsCalc = BigDecimal.valueOf(Math.floor((annualDisposableIncome.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128)
                .multiply(upliftedIncomePercent)
                .divide(BigDecimal.valueOf(100), MathContext.DECIMAL128).doubleValue()))).setScale(2);
        if (monthlyContributionsCalc.compareTo(minUpliftedMonthlyAmount) > 0) {
            return monthlyContributionsCalc;
        }
        return minUpliftedMonthlyAmount;
    }

    private static boolean checkNull(BigDecimal... values){
        return Arrays.stream(values).anyMatch(Objects::isNull);
    }
}
