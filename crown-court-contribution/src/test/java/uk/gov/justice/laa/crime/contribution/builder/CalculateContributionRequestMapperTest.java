package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionRequest;

import java.math.BigDecimal;

@ExtendWith(SoftAssertionsExtension.class)
public class CalculateContributionRequestMapperTest {
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenAValidContributionCalcParametersDTO_whenMapIsInvoked_thenReturnCalculateContributionRequest() {
        ContributionCalcParametersDTO contributionCalcParametersDTO = TestModelDataBuilder.getContributionCalcParametersDTO();
        CalculateContributionRequestMapper calculateContributionRequestMapper = new CalculateContributionRequestMapper();
        ApiCalculateContributionRequest CalculateContributionRequest = calculateContributionRequestMapper.map(contributionCalcParametersDTO, BigDecimal.ZERO, false, BigDecimal.ONE);
        softly.assertThat(CalculateContributionRequest.getContributionCap()).isEqualTo(BigDecimal.ONE);
        softly.assertThat(CalculateContributionRequest.getUpliftApplied()).isEqualTo(Boolean.FALSE);
        softly.assertThat(CalculateContributionRequest.getAnnualDisposableIncome()).isEqualTo(BigDecimal.ZERO);
        softly.assertThat(CalculateContributionRequest.getMinimumMonthlyAmount()).isEqualTo(contributionCalcParametersDTO.getMinimumMonthlyAmount());
        softly.assertThat(CalculateContributionRequest.getMinUpliftedMonthlyAmount()).isEqualTo(contributionCalcParametersDTO.getMinUpliftedMonthlyAmount());
        softly.assertThat(CalculateContributionRequest.getUpfrontTotalMonths()).isEqualTo(contributionCalcParametersDTO.getUpfrontTotalMonths());
        softly.assertThat(CalculateContributionRequest.getDisposableIncomePercent()).isEqualTo(contributionCalcParametersDTO.getDisposableIncomePercent());
        softly.assertThat(CalculateContributionRequest.getUpliftedIncomePercent()).isEqualTo(contributionCalcParametersDTO.getUpliftedIncomePercent());
        softly.assertAll();
    }
}
