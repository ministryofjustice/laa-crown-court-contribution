package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.maat_api.MaatCalculateContributionResponse;

import java.math.BigDecimal;

@ExtendWith(SoftAssertionsExtension.class)
class MaatCalculateContributionResponseMapperTest {
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenAValidApiCalculateContributionResponse_whenMapIsInvoked_thenReturnMaatCalculateContributionResponse() {
        ApiCalculateContributionResponse apiCalculateContributionResponse = TestModelDataBuilder.getApiCalculateContributionResponse();
        MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper = new MaatCalculateContributionResponseMapper();
        MaatCalculateContributionResponse maatCalculateContributionResponse = maatCalculateContributionResponseMapper.map(apiCalculateContributionResponse, BigDecimal.ONE, null, 0);
        softly.assertThat(maatCalculateContributionResponse.getMonthlyContributions()).isEqualTo(apiCalculateContributionResponse.getMonthlyContributions());
        softly.assertThat(maatCalculateContributionResponse.getUpfrontContributions()).isEqualTo(apiCalculateContributionResponse.getUpfrontContributions());
        softly.assertThat(maatCalculateContributionResponse.getBasedOn()).isEqualTo(apiCalculateContributionResponse.getBasedOn());
        softly.assertThat(maatCalculateContributionResponse.getUpliftApplied()).isEqualTo(apiCalculateContributionResponse.getUpliftApplied());
        softly.assertThat(maatCalculateContributionResponse.getEffectiveDate()).isEqualTo(null);
        softly.assertThat(maatCalculateContributionResponse.getTotalMonths()).isEqualTo(0);
        softly.assertThat(maatCalculateContributionResponse.getContributionCap()).isEqualTo(BigDecimal.ONE);
        softly.assertAll();
    }
}
