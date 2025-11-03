package uk.gov.justice.laa.crime.contribution.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CalculateContributionUtilTest {
    @Test
    void givenMonthlyContributionsSmaller_whenCalculateDisposableContributionIsInvoked_thenZeroIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(100);
        assertThat(CalculateContributionUtil.calculateMonthlyContribution(
                        annualDisposableIncome, BigDecimal.TEN, minimumMonthlyAmount))
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenMonthlyContributionsLessThanZero_whenCalculateDisposableContributionIsInvoked_thenZeroIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(100);
        assertThat(CalculateContributionUtil.calculateMonthlyContribution(
                        annualDisposableIncome, BigDecimal.TEN, minimumMonthlyAmount))
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void
            givenMonthlyContributionsGreater_whenCalculateDisposableContributionIsInvoked_thenMonthlyContributionsIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(80);
        BigDecimal monthlyContributions = BigDecimal.valueOf(83).setScale(2);
        assertThat(CalculateContributionUtil.calculateMonthlyContribution(
                        annualDisposableIncome, BigDecimal.TEN, minimumMonthlyAmount))
                .isEqualTo(monthlyContributions);
    }

    @Test
    void
            givenMonthlyContributionsGreater_whenCalculateUpliftedMonthlyAmountIsInvoked_thenMonthlyContributionsIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minUpliftedMonthlyAmount = BigDecimal.valueOf(80);
        BigDecimal monthlyContributions = BigDecimal.valueOf(83).setScale(2);
        assertThat(CalculateContributionUtil.calculateUpliftedMonthlyAmount(
                        annualDisposableIncome, BigDecimal.TEN, minUpliftedMonthlyAmount))
                .isEqualTo(monthlyContributions);
    }

    @Test
    void
            givenMonthlyContributionsSmaller_whenCalculateUpliftedMonthlyAmountIsInvoked_thenMinUpliftMonthlyAmountIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minUpliftedMonthlyAmount = BigDecimal.valueOf(100);
        assertThat(CalculateContributionUtil.calculateUpliftedMonthlyAmount(
                        annualDisposableIncome, BigDecimal.TEN, minUpliftedMonthlyAmount))
                .isEqualTo(minUpliftedMonthlyAmount);
    }

    @Test
    void givenUpfrontContributionGreater_whenCalculateUpfrontContributionsIsInvoked_thenContributionCapIsReturned() {
        BigDecimal contributionCap = BigDecimal.valueOf(80);
        BigDecimal monthlyContributions = BigDecimal.valueOf(83);
        assertThat(CalculateContributionUtil.calculateUpfrontContributions(monthlyContributions, contributionCap, 2))
                .isEqualTo(contributionCap);
    }

    @Test
    void
            givenUpfrontContributionSmaller_whenCalculateUpfrontContributionsIsInvoked_thenUpfrontContributionIsReturned() {
        BigDecimal contributionCap = BigDecimal.valueOf(80);
        BigDecimal monthlyContributions = BigDecimal.valueOf(75);
        assertThat(CalculateContributionUtil.calculateUpfrontContributions(monthlyContributions, contributionCap, 1))
                .isEqualTo(monthlyContributions);
    }

    @ParameterizedTest
    @MethodSource("calculateMonthlyContribution")
    void checkNullInputsForCalculateMonthlyContribution(
            final BigDecimal annualDisposableIncome,
            final BigDecimal disposableIncomePercent,
            final BigDecimal minimumMonthlyAmount) {
        assertThat(CalculateContributionUtil.calculateMonthlyContribution(
                        annualDisposableIncome, disposableIncomePercent, minimumMonthlyAmount))
                .isNull();
    }

    @ParameterizedTest
    @MethodSource("calculateUpliftedMonthlyAmount")
    void checkNullInputsForCalculateUpliftedMonthlyAmount(
            final BigDecimal annualDisposableIncome,
            final BigDecimal upliftedIncomePercent,
            final BigDecimal minUpliftedMonthlyAmount) {
        assertThat(CalculateContributionUtil.calculateUpliftedMonthlyAmount(
                        annualDisposableIncome, upliftedIncomePercent, minUpliftedMonthlyAmount))
                .isNull();
    }

    @ParameterizedTest
    @MethodSource("calculateUpfrontContributions")
    void checkNullInputsForCalculateUpfrontContributions(
            final BigDecimal monthlyContributions, final BigDecimal contributionCap, final Integer upfrontTotalMonths) {
        assertThat(CalculateContributionUtil.calculateUpfrontContributions(
                        monthlyContributions, contributionCap, upfrontTotalMonths))
                .isNull();
    }

    private static Stream<Arguments> calculateMonthlyContribution() {
        return Stream.of(
                Arguments.of(BigDecimal.ZERO, null, BigDecimal.ZERO),
                Arguments.of(null, BigDecimal.ZERO, BigDecimal.ZERO),
                Arguments.of(BigDecimal.ZERO, BigDecimal.ZERO, null));
    }

    private static Stream<Arguments> calculateUpliftedMonthlyAmount() {
        return Stream.of(
                Arguments.of(BigDecimal.ZERO, null, BigDecimal.ZERO),
                Arguments.of(null, BigDecimal.ZERO, BigDecimal.ZERO),
                Arguments.of(BigDecimal.ZERO, BigDecimal.ZERO, null));
    }

    private static Stream<Arguments> calculateUpfrontContributions() {
        return Stream.of(
                Arguments.of(BigDecimal.ZERO, null, 1),
                Arguments.of(null, BigDecimal.ZERO, null),
                Arguments.of(null, BigDecimal.ZERO, 1),
                Arguments.of(BigDecimal.ZERO, BigDecimal.ZERO, null));
    }
}
