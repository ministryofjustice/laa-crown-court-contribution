package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionResponse;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateContributionServiceTest {

    @Test
    void givenValidRequestWithUpliftApplied_whenCalculateContributionServiceIsInvoked_thenReturnMonthlyContributionWhenGreaterThanMinUpliftedMonthlyAmount() {
        CalculateContributionService calculateContributionService = new CalculateContributionService();
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minUpliftedMonthlyAmount = BigDecimal.valueOf(80);
        ApiCalculateContributionRequest request = new ApiCalculateContributionRequest()
                .withAnnualDisposableIncome(annualDisposableIncome)
                .withUpliftApplied(true)
                .withMinUpliftedMonthlyAmount(minUpliftedMonthlyAmount)
                .withUpliftedIncomePercent(new BigDecimal(10));
        ApiCalculateContributionResponse response = calculateContributionService.calculateContribution(request);
        assertThat(response.getMonthlyContributions()).isGreaterThan(minUpliftedMonthlyAmount);
        assertEquals(Constants.Y, response.getUpliftApplied());
    }

    @Test
    void givenValidRequestWithUpliftApplied_whenCalculateContributionServiceIsInvoked_thenReturnMinUpliftedMonthlyAmountWhenMonthlyContributionIsSmaller() {
        CalculateContributionService calculateContributionService = new CalculateContributionService();
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(500);
        BigDecimal minUpliftedMonthlyAmount = BigDecimal.valueOf(80);
        ApiCalculateContributionRequest request = new ApiCalculateContributionRequest()
                .withAnnualDisposableIncome(annualDisposableIncome)
                .withUpliftApplied(true)
                .withMinUpliftedMonthlyAmount(minUpliftedMonthlyAmount)
                .withUpliftedIncomePercent(new BigDecimal(10));
        ApiCalculateContributionResponse response = calculateContributionService.calculateContribution(request);
        assertThat(response.getMonthlyContributions()).isEqualTo(minUpliftedMonthlyAmount);
        assertEquals(Constants.Y, response.getUpliftApplied());
    }

    @Test
    void givenValidRequestWithDisposableIncome_whenCalculateContributionServiceIsInvoked_thenReturnZeroWhenMonthlyContributionIsSmaller() {
        CalculateContributionService calculateContributionService = new CalculateContributionService();
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(100);
        ApiCalculateContributionRequest request = new ApiCalculateContributionRequest()
                .withUpliftApplied(false)
                .withAnnualDisposableIncome(annualDisposableIncome)
                .withDisposableIncomePercent(BigDecimal.TEN)
                .withContributionCap(BigDecimal.ONE)
                .withMinimumMonthlyAmount(minimumMonthlyAmount)
                .withUpfrontTotalMonths(12);
        ApiCalculateContributionResponse response = calculateContributionService.calculateContribution(request);
        assertThat(response.getMonthlyContributions()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getUpfrontContributions()).isEqualTo(BigDecimal.ZERO);
        assertEquals(Constants.N, response.getUpliftApplied());
        assertEquals(Constants.MEANS, response.getBasedOn());
    }

    @ParameterizedTest
    @MethodSource("contributionCap")
    void givenValidRequestWithDisposableIncome_whenCalculateContributionServiceIsInvoked_thenMonthlyContributionsIsReturned(BigDecimal contributionCap) {
        CalculateContributionService calculateContributionService = new CalculateContributionService();
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(80);
        //BigDecimal contributionCap = new BigDecimal(100);
        ApiCalculateContributionRequest request = new ApiCalculateContributionRequest()
                .withUpliftApplied(false)
                .withAnnualDisposableIncome(annualDisposableIncome)
                .withDisposableIncomePercent(BigDecimal.TEN)
                .withContributionCap(contributionCap)
                .withMinimumMonthlyAmount(minimumMonthlyAmount)
                .withUpfrontTotalMonths(12);
        ApiCalculateContributionResponse response = calculateContributionService.calculateContribution(request);
        assertThat(response.getMonthlyContributions()).isEqualTo(new BigDecimal(83));
        assertThat(response.getUpfrontContributions()).isEqualTo(contributionCap);
        assertEquals(Constants.N, response.getUpliftApplied());
        assertEquals(Constants.MEANS, response.getBasedOn());
    }

    @Test
    void givenValidRequestWithDisposableIncome_whenCalculateContributionServiceIsInvoked_thenContributionCapIsReturnedWhenMonthlyContributionIsGreater() {
        CalculateContributionService calculateContributionService = new CalculateContributionService();
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(80);
        BigDecimal contributionCap = new BigDecimal(53);
        ApiCalculateContributionRequest request = new ApiCalculateContributionRequest()
                .withUpliftApplied(false)
                .withAnnualDisposableIncome(annualDisposableIncome)
                .withDisposableIncomePercent(BigDecimal.TEN)
                .withContributionCap(contributionCap)
                .withMinimumMonthlyAmount(minimumMonthlyAmount)
                .withUpfrontTotalMonths(12);
        ApiCalculateContributionResponse response = calculateContributionService.calculateContribution(request);
        assertThat(response.getMonthlyContributions()).isEqualTo(contributionCap);
        assertThat(response.getUpfrontContributions()).isEqualTo(contributionCap);
        assertEquals(Constants.N, response.getUpliftApplied());
        assertEquals(Constants.OFFENCE_TYPE, response.getBasedOn());
    }
    private static Stream<Arguments> contributionCap() {
        return Stream.of(Arguments.of(null, new BigDecimal(100)));
    }
}
