package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.TransferStatus;

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

        BigDecimal appealContributionAmount = BigDecimal.valueOf(500);
        CreateContributionRequest request = mapper.map(contributionDTO, appealContributionAmount);

        softly.assertThat(request.getRepId()).isEqualTo(123);
        softly.assertThat(request.getApplId()).isEqualTo(123);
        softly.assertThat(request.getContributionCap()).isEqualTo(appealContributionAmount);
        softly.assertThat(request.getMonthlyContributions()).isNull();
        softly.assertThat(request.getUpliftApplied()).isNull();
        softly.assertThat(request.getBasedOn()).isNull();
        softly.assertThat(request.getUpfrontContributions()).isEqualTo(new BigDecimal(500));
        softly.assertThat(request.getUserCreated()).isEqualTo("TEST");
        softly.assertThat(request.getCorrespondenceId()).isNull();
        softly.assertThat(request.getCreateContributionOrder()).isNull();
        softly.assertThat(request.getTransferStatus()).isEqualTo(TransferStatus.REQUESTED);
        softly.assertThat(request.getEffectiveDate().getMonth()).isEqualTo(TestModelDataBuilder.TEST_DATE.getMonth());
        softly.assertThat(request.getEffectiveDate().getDayOfMonth()).isEqualTo(TestModelDataBuilder.TEST_DATE.getDayOfMonth());
        softly.assertThat(request.getEffectiveDate().getYear()).isEqualTo(TestModelDataBuilder.TEST_DATE.getYear());
        softly.assertThat(request.getCalcDate().getMonth()).isEqualTo(TestModelDataBuilder.CALC_DATE.getMonth());
        softly.assertThat(request.getCalcDate().getDayOfMonth()).isEqualTo(TestModelDataBuilder.CALC_DATE.getDayOfMonth());
        softly.assertThat(request.getCalcDate().getYear()).isEqualTo(TestModelDataBuilder.CALC_DATE.getYear());
        softly.assertThat(request.getDateUpliftApplied().getYear()).isEqualTo(TestModelDataBuilder.UPLIFT_APPLIED_DATE.getYear());
        softly.assertThat(request.getDateUpliftApplied().getMonth()).isEqualTo(TestModelDataBuilder.UPLIFT_APPLIED_DATE.getMonth());
        softly.assertThat(request.getDateUpliftApplied().getDayOfMonth()).isEqualTo(TestModelDataBuilder.UPLIFT_APPLIED_DATE.getDayOfMonth());
        softly.assertThat(request.getDateUpliftRemoved().getYear()).isEqualTo(TestModelDataBuilder.UPLIFT_REMOVED_DATE.getYear());
        softly.assertThat(request.getDateUpliftRemoved().getMonth()).isEqualTo(TestModelDataBuilder.UPLIFT_REMOVED_DATE.getMonth());
        softly.assertThat(request.getDateUpliftRemoved().getDayOfMonth()).isEqualTo(TestModelDataBuilder.UPLIFT_REMOVED_DATE.getDayOfMonth());
        softly.assertAll();
    }

}