package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.builder.AppealContributionResponseMapper;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.GetContributionAmountRequestMapper;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentResult;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AppealContributionServiceTest {

    private static final String LAA_TRANSACTION_ID = "99999";

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private GetContributionAmountRequestMapper getContributionAmountRequestMapper;

    @Mock
    private CreateContributionRequestMapper createContributionRequestMapper;

    @Mock
    private AppealContributionResponseMapper appealContributionResponseMapper;

    @InjectMocks
    private AppealContributionService appealContributionService;

    @Test
    void givenAssessmentFailed_whenCalculateContributionIsInvoked_thenContributionDataIsUpdated() {
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCompareContributionService(CaseType.APPEAL_CC.getCaseTypeString(),
                null, null, null, null, null, null);

        Assessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.setResult(AssessmentResult.FAIL);
        contributionDTO.setAssessments(List.of(assessment));

        Contribution currContribution = Contribution.builder().build();
        currContribution.setUpfrontContributions(BigDecimal.valueOf(250));

        AppealContributionResponse appealContributionResponse = TestModelDataBuilder.buildAppealContributionResponse();
        appealContributionResponse.setUpfrontContributions(BigDecimal.valueOf(500));

        when(getContributionAmountRequestMapper.map(any(ContributionDTO.class), any(AssessmentResult.class)))
                .thenReturn(new GetContributionAmountRequest());
        when(maatCourtDataService.getContributionAppealAmount(any(GetContributionAmountRequest.class), anyString()))
                .thenReturn(BigDecimal.valueOf(500));
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean()))
                .thenReturn(List.of(currContribution));
       when(createContributionRequestMapper.map(any(ContributionDTO.class), any(BigDecimal.class)))
                .thenReturn(new CreateContributionRequest());
        when(maatCourtDataService.createContribution(any(CreateContributionRequest.class), anyString()))
                .thenReturn(TestModelDataBuilder.buildContribution());

        ContributionDTO response = appealContributionService.calculateContribution(contributionDTO, LAA_TRANSACTION_ID);

        assertThat(response.getUpfrontContributions()).isEqualTo(BigDecimal.valueOf(250));
        verify(maatCourtDataService, times(1)).createContribution(any(CreateContributionRequest.class), anyString());
    }

    @Test
    void givenAssessmentPassed_whenCalculateContributionIsInvoked_thenAppealContributionDataIsNotUpdated() {

        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCompareContributionService(CaseType.APPEAL_CC.getCaseTypeString(),
                null, null, null, null, null, null);
        Contribution currContribution = Contribution.builder().build();
        currContribution.setUpfrontContributions(BigDecimal.valueOf(0));
        when(getContributionAmountRequestMapper.map(any(ContributionDTO.class), any(AssessmentResult.class)))
                .thenReturn(new GetContributionAmountRequest());
        when(maatCourtDataService.getContributionAppealAmount(any(GetContributionAmountRequest.class), anyString()))
                .thenReturn(BigDecimal.valueOf(0));
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean()))
                .thenReturn(List.of(currContribution));
        ContributionDTO response = appealContributionService.calculateContribution(contributionDTO, LAA_TRANSACTION_ID);

        assertThat(response.getUpfrontContributions()).isEqualTo(BigDecimal.ZERO);
        verify(maatCourtDataService, times(0)).createContribution(any(CreateContributionRequest.class), anyString());
    }
}
