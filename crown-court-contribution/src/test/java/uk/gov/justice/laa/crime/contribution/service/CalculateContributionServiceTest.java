package uk.gov.justice.laa.crime.contribution.service;

import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.common.Constants;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(SoftAssertionsExtension.class)
class CalculateContributionServiceTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void
            givenValidRequestWithUpliftApplied_whenCalculateContributionServiceIsInvoked_thenReturnMonthlyContributionWhenGreaterThanMinUpliftedMonthlyAmount() {
        CalculateContributionService calculateContributionService = new CalculateContributionService();
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minUpliftedMonthlyAmount = BigDecimal.valueOf(80);
        ApiCalculateContributionRequest request = new ApiCalculateContributionRequest()
                .withAnnualDisposableIncome(annualDisposableIncome)
                .withUpliftApplied(true)
                .withMinUpliftedMonthlyAmount(minUpliftedMonthlyAmount)
                .withUpliftedIncomePercent(new BigDecimal(10));
        ApiCalculateContributionResponse response = calculateContributionService.calculateContribution(request);

        softly.assertThat(response.getMonthlyContributions()).isGreaterThan(minUpliftedMonthlyAmount);
        softly.assertThat(Constants.Y).isEqualTo(response.getUpliftApplied());
    }

    @Test
    void
            givenValidRequestWithUpliftApplied_whenCalculateContributionServiceIsInvoked_thenReturnMinUpliftedMonthlyAmountWhenMonthlyContributionIsSmaller() {
        CalculateContributionService calculateContributionService = new CalculateContributionService();
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(500);
        BigDecimal minUpliftedMonthlyAmount = BigDecimal.valueOf(80);
        ApiCalculateContributionRequest request = new ApiCalculateContributionRequest()
                .withAnnualDisposableIncome(annualDisposableIncome)
                .withUpliftApplied(true)
                .withMinUpliftedMonthlyAmount(minUpliftedMonthlyAmount)
                .withUpliftedIncomePercent(new BigDecimal(10));
        ApiCalculateContributionResponse response = calculateContributionService.calculateContribution(request);

        softly.assertThat(response.getMonthlyContributions()).isEqualTo(minUpliftedMonthlyAmount);
        softly.assertThat(Constants.Y).isEqualTo(response.getUpliftApplied());
    }

    @Test
    void
            givenValidRequestWithDisposableIncome_whenCalculateContributionServiceIsInvoked_thenReturnZeroWhenMonthlyContributionIsSmaller() {
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

        softly.assertThat(response.getMonthlyContributions()).isEqualTo(BigDecimal.ZERO);
        softly.assertThat(response.getUpfrontContributions()).isEqualTo(BigDecimal.ZERO);
        softly.assertThat(Constants.N).isEqualTo(response.getUpliftApplied());
        softly.assertThat(Constants.MEANS).isEqualTo(response.getBasedOn());
    }

    @ParameterizedTest
    @MethodSource("contributionCap")
    void
            givenValidRequestWithDisposableIncome_whenCalculateContributionServiceIsInvoked_thenMonthlyContributionsIsReturned(
                    BigDecimal contributionCap) {
        CalculateContributionService calculateContributionService = new CalculateContributionService();
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(80);
        ApiCalculateContributionRequest request = new ApiCalculateContributionRequest()
                .withUpliftApplied(false)
                .withAnnualDisposableIncome(annualDisposableIncome)
                .withDisposableIncomePercent(BigDecimal.TEN)
                .withContributionCap(contributionCap)
                .withMinimumMonthlyAmount(minimumMonthlyAmount)
                .withUpfrontTotalMonths(12);
        ApiCalculateContributionResponse response = calculateContributionService.calculateContribution(request);

        softly.assertThat(response.getMonthlyContributions()).isEqualTo(new BigDecimal(83).setScale(2));
        softly.assertThat(response.getUpfrontContributions()).isEqualTo(contributionCap);
        softly.assertThat(Constants.N).isEqualTo(response.getUpliftApplied());
        softly.assertThat(Constants.MEANS).isEqualTo(response.getBasedOn());
    }

    @Test
    void
            givenValidRequestWithDisposableIncome_whenCalculateContributionServiceIsInvoked_thenContributionCapIsReturnedWhenMonthlyContributionIsGreater() {
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

        softly.assertThat(response.getMonthlyContributions()).isEqualTo(contributionCap);
        softly.assertThat(response.getUpfrontContributions()).isEqualTo(contributionCap);
        softly.assertThat(Constants.N).isEqualTo(response.getUpliftApplied());
        softly.assertThat(Constants.OFFENCE_TYPE).isEqualTo(response.getBasedOn());
    }

    private static Stream<Arguments> contributionCap() {
        return Stream.of(Arguments.of(null, new BigDecimal(100)));
    }
}
