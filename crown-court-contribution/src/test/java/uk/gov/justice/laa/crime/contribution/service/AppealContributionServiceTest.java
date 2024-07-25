package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.GetContributionAmountRequestMapper;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtAppealOutcome;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppealContributionServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private GetContributionAmountRequestMapper getContributionAmountRequestMapper;

    @Mock
    private CreateContributionRequestMapper createContributionRequestMapper;

    @InjectMocks
    private AppealContributionService appealContributionService;

    @Test
    void givenAssessmentFailed_whenCalculateContributionIsInvoked_thenContributionDataIsUpdated() {
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.APPEAL_CC.getCaseTypeString(),
                        null,
                        null,
                        null,
                        null,
                        null
                );
        ApiAssessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.setResult(AssessmentResult.FAIL);
        calculateContributionDTO.setAssessments(List.of(assessment));
        GetContributionAmountRequest getContributionAmountRequest =
                new GetContributionAmountRequest()
                        .withCaseType(CaseType.APPEAL_CC)
                        .withAppealType(AppealType.ACS)
                        .withOutcome(CrownCourtAppealOutcome.UNSUCCESSFUL)
                        .withAssessmentResult(AssessmentResult.FAIL);
        Contribution currContribution = Contribution.builder().build();
        currContribution.setUpfrontContributions(BigDecimal.valueOf(500));
        when(getContributionAmountRequestMapper.map(any(CalculateContributionDTO.class), any(AssessmentResult.class)))
                .thenReturn(getContributionAmountRequest);
        when(maatCourtDataService.getContributionAppealAmount(any(GetContributionAmountRequest.class)))
                .thenReturn(BigDecimal.valueOf(250));
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(currContribution));
        when(createContributionRequestMapper.map(any(CalculateContributionDTO.class), any(BigDecimal.class)))
                .thenReturn(new CreateContributionRequest());
        when(maatCourtDataService.createContribution(any(CreateContributionRequest.class)))
                .thenReturn(TestModelDataBuilder.buildContribution());

        ApiMaatCalculateContributionResponse response =
                appealContributionService.calculateAppealContribution(calculateContributionDTO);

        assertThat(response.getUpfrontContributions()).isEqualTo(BigDecimal.valueOf(250));
        verify(maatCourtDataService, times(1))
                .createContribution(any(CreateContributionRequest.class));
    }

    @Test
    void givenAssessmentPassed_whenCalculateContributionIsInvoked_thenAppealContributionDataIsUpdated() {
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.APPEAL_CC.getCaseTypeString(),
                        null,
                        null,
                        null,
                        null,
                        null
                );
        GetContributionAmountRequest getContributionAmountRequest =
                new GetContributionAmountRequest()
                        .withCaseType(CaseType.APPEAL_CC)
                        .withAppealType(AppealType.ACS)
                        .withOutcome(CrownCourtAppealOutcome.UNSUCCESSFUL)
                        .withAssessmentResult(AssessmentResult.PASS);
        Contribution currContribution = Contribution.builder().build();
        currContribution.setUpfrontContributions(BigDecimal.valueOf(0));
        when(getContributionAmountRequestMapper.map(any(CalculateContributionDTO.class), any(AssessmentResult.class)))
                .thenReturn(getContributionAmountRequest);
        when(maatCourtDataService.getContributionAppealAmount(any(GetContributionAmountRequest.class)))
                .thenReturn(BigDecimal.valueOf(250));
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(currContribution));
        when(createContributionRequestMapper.map(any(CalculateContributionDTO.class), any(BigDecimal.class)))
                .thenReturn(new CreateContributionRequest());
        when(maatCourtDataService.createContribution(any(CreateContributionRequest.class)))
                .thenReturn(TestModelDataBuilder.buildContribution());

        ApiMaatCalculateContributionResponse response =
                appealContributionService.calculateAppealContribution(calculateContributionDTO);

        assertThat(response.getUpfrontContributions()).isEqualTo(BigDecimal.valueOf(250));
        verify(maatCourtDataService, times(1))
                .createContribution(any(CreateContributionRequest.class));
    }

    @Test
    void givenNoPriorContributions_whenCalculateAppealContributionIsInvoked_thenNullIsReturned() {

        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.APPEAL_CC.getCaseTypeString(),
                        null,
                        null,
                        null,
                        null,
                        null
                );
        Contribution currContribution = Contribution.builder().build();
        currContribution.setUpfrontContributions(BigDecimal.valueOf(0));

        when(getContributionAmountRequestMapper.map(any(CalculateContributionDTO.class), any(AssessmentResult.class)))
                .thenReturn(new GetContributionAmountRequest());
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(null);
        ApiMaatCalculateContributionResponse response =
                appealContributionService.calculateAppealContribution(calculateContributionDTO);
        assertThat(response.getContributionId()).isNull();
    }
}
