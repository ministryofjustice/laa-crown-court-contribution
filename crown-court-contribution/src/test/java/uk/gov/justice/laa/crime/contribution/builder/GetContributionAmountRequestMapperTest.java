package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtAppealOutcome;

@ExtendWith(SoftAssertionsExtension.class)
class GetContributionAmountRequestMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenAValidContributionDTOAndAssessmentResult_whenMapIsInvoked_thenReturnGetContributionAmountRequest() {

        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder().caseType(CaseType.APPEAL_CC).appealType(AppealType.ACN)
                        .lastOutcome(TestModelDataBuilder.buildLastOutcome()).build();
        GetContributionAmountRequestMapper mapper = new GetContributionAmountRequestMapper();

        GetContributionAmountRequest request = mapper.map(calculateContributionDTO, AssessmentResult.PASS);

        softly.assertThat(request.getCaseType()).isEqualTo(CaseType.APPEAL_CC);
        softly.assertThat(request.getAppealType()).isEqualTo(AppealType.ACN);
        softly.assertThat(request.getOutcome()).isEqualTo(CrownCourtAppealOutcome.SUCCESSFUL);
        softly.assertThat(request.getAssessmentResult()).isEqualTo(AssessmentResult.PASS);
        softly.assertAll();

    }

    @Test
    void givenContributionDTOWithNoLastOutcomeAndAssessmentResult_whenMapIsInvoked_thenReturnGetContributionAmountRequest() {

        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder().caseType(CaseType.APPEAL_CC).appealType(AppealType.ACN)
                .build();
        GetContributionAmountRequestMapper mapper = new GetContributionAmountRequestMapper();

        GetContributionAmountRequest request = mapper.map(calculateContributionDTO, AssessmentResult.PASS);

        softly.assertThat(request.getCaseType()).isEqualTo(CaseType.APPEAL_CC);
        softly.assertThat(request.getAppealType()).isEqualTo(AppealType.ACN);
        softly.assertThat(request.getOutcome()).isNull();
        softly.assertThat(request.getAssessmentResult()).isEqualTo(AssessmentResult.PASS);
        softly.assertAll();

    }
}