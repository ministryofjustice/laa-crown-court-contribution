package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionResponse;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculateContributionServiceTest {

    @Test
    void giveValidRequestWithUpliftApplied_whenCalculateContributionServiceIsInvoked_thenReturnMonthlyContributionWhenGreaterThanMinUpliftedMonthlyAmount() {
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
        assertEquals(response.getUpliftApplied(), "Y");
    }

    @Test
    void giveValidRequestWithUpliftApplied_whenCalculateContributionServiceIsInvoked_thenReturnMinUpliftedMonthlyAmountWhenMonthlyContributionIsSmaller() {
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
        assertEquals(response.getUpliftApplied(), "Y");
    }

    @Test
    void giveValidRequestWithDisposableIncome_whenCalculateContributionServiceIsInvoked_thenReturnZeroWhenMonthlyContributionIsSmaller() {
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
        assertEquals(response.getUpliftApplied(), "N");
        assertEquals(response.getBasedOn(), "Means");
    }

    @Test
    void giveValidRequestWithDisposableIncome_whenCalculateContributionServiceIsInvoked_thenMonthlyContributionsIsReturned() {
        CalculateContributionService calculateContributionService = new CalculateContributionService();
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(80);
        BigDecimal contributionCap = new BigDecimal(100);
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
        assertEquals(response.getUpliftApplied(), "N");
        assertEquals(response.getBasedOn(), "Means");
    }

    @Test
    void giveValidRequestWithDisposableIncome_whenCalculateContributionServiceIsInvoked_thenContributionCapIsReturnedWhenMonthlyContributionIsGreater() {
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
        assertEquals(response.getUpliftApplied(), "N");
        assertEquals(response.getBasedOn(), "Offence Type");
    }
}
