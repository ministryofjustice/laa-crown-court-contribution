package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;

import java.math.BigDecimal;

@ExtendWith(SoftAssertionsExtension.class)
class CreateContributionRequestMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    private final CreateContributionRequestMapper mapper = new CreateContributionRequestMapper();

    @Test
    void givenAValidAppealContributionRequest_whenMapIsInvoked_thenReturnCreateContributionRequest() {

        CreateContributionRequest request = mapper
                .map(TestModelDataBuilder.buildAppealContributionRequest(), new BigDecimal(500));
        softly.assertThat(request.getRepId()).isEqualTo(999);
        softly.assertThat(request.getApplId()).isEqualTo(999);
        softly.assertThat(request.getContributionCap()).isZero();
        softly.assertThat(request.getEffectiveDate()).isEqualTo(TestModelDataBuilder.TEST_DATE);
        softly.assertThat(request.getMonthlyContributions()).isZero();
        softly.assertThat(request.getUpliftApplied()).isEqualTo("N");
        softly.assertThat(request.getBasedOn()).isNull();
        softly.assertThat(request.getUpfrontContributions()).isEqualTo(new BigDecimal(500));
        softly.assertThat(request.getUserCreated()).isEqualTo("TEST");
        softly.assertThat(request.getCorrespondenceId()).isNull();
        softly.assertThat(request.getCreateContributionOrder()).isEqualTo("N");
        softly.assertAll();

    }

    @Test
    void givenAValidContributionDTO_whenMapIsInvoked_thenReturnCreateContributionRequest() {

        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCompareContributionService(CaseType.COMMITAL.getCaseTypeString(),
                new BigDecimal(500), null, null,
                TestModelDataBuilder.TEST_DATE.toLocalDate(), "N", null);


        CreateContributionRequest request = mapper.map(contributionDTO, new BigDecimal(500));

        softly.assertThat(request.getRepId()).isEqualTo(123);
        softly.assertThat(request.getApplId()).isEqualTo(123);
        softly.assertThat(request.getContributionCap()).isZero();
        softly.assertThat(request.getMonthlyContributions()).isZero();
        softly.assertThat(request.getUpliftApplied()).isEqualTo("N");
        softly.assertThat(request.getBasedOn()).isNull();
        softly.assertThat(request.getUpfrontContributions()).isEqualTo(new BigDecimal(500));
        softly.assertThat(request.getUserCreated()).isEqualTo("TEST");
        softly.assertThat(request.getCorrespondenceId()).isNull();
        softly.assertThat(request.getCreateContributionOrder()).isEqualTo("N");
        softly.assertAll();
    }

}