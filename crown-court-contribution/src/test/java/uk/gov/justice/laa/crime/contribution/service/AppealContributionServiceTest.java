package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.common.model.contribution.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.MaatCalculateContributionResponseMapper;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CaseType;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppealContributionServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private CreateContributionRequestMapper createContributionRequestMapper;

    @InjectMocks
    private AppealContributionService appealContributionService;
    @Mock
    private MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper;

    @Test
    void givenAppealContributionAmountChange_whenCalculateContributionIsInvoked_thenContributionDataIsUpdated() {
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.APPEAL_CC.getCaseTypeString(),
                        null,
                        null,
                        null,
                        null,
                        null
                );

        calculateContributionDTO.setCrownCourtOutcomeList(TestModelDataBuilder.getApiCrownCourtSummaryAppeal());
        ApiAssessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.setResult(AssessmentResult.FAIL);
        calculateContributionDTO.setAssessments(List.of(assessment));
        Contribution currContribution = Contribution.builder().build();
        currContribution.setUpfrontContributions(BigDecimal.valueOf(250));
        Contribution createdContribution = TestModelDataBuilder.buildContribution();

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(currContribution));
        when(createContributionRequestMapper.map(any(CalculateContributionDTO.class), any(BigDecimal.class)))
                .thenReturn(new CreateContributionRequest());
        when(maatCourtDataService.createContribution(any(CreateContributionRequest.class)))
                .thenReturn(createdContribution);

        appealContributionService.calculateAppealContribution(calculateContributionDTO);

        verify(maatCourtDataService, times(1))
                .findContribution(calculateContributionDTO.getRepId(), true);
        verify(maatCourtDataService, times(1))
                .createContribution(any(CreateContributionRequest.class));
        verify(maatCalculateContributionResponseMapper, times(1))
                .map(createdContribution);
    }

    @Test
    void givenLatestOutcomeNotAppeal_whenCalculateAppealContributionIsInvoked_thenNullIsReturned() {
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.APPEAL_CC.getCaseTypeString(),
                        null,
                        null,
                        null,
                        null,
                        null
                );
        calculateContributionDTO.setCrownCourtOutcomeList(TestModelDataBuilder.getApiCrownCourtSummary());
        ApiAssessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.setResult(AssessmentResult.FAIL);
        calculateContributionDTO.setAssessments(List.of(assessment));

        ApiMaatCalculateContributionResponse response =
                appealContributionService.calculateAppealContribution(calculateContributionDTO);

        verify(maatCourtDataService, times(0))
                .findContribution(anyInt(), anyBoolean());
        assertThat(response.getContributionId()).isNull();
    }

    @Test
    void givenAppealContributionAmountIsUnchanged_whenCalculateContributionIsInvoked_thenContributionDataIsReturned() {
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.APPEAL_CC.getCaseTypeString(),
                        null,
                        null,
                        null,
                        null,
                        null
                );
        calculateContributionDTO.setCrownCourtOutcomeList(TestModelDataBuilder.getApiCrownCourtSummaryAppeal());
        Contribution currContribution = Contribution.builder().build();
        currContribution.setUpfrontContributions(BigDecimal.valueOf(0));

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(currContribution));

        appealContributionService.calculateAppealContribution(calculateContributionDTO);

        verify(maatCourtDataService, times(1))
                .findContribution(calculateContributionDTO.getRepId(), true);
        verify(maatCourtDataService, times(0))
                .createContribution(any(CreateContributionRequest.class));
        verify(maatCalculateContributionResponseMapper, times(1))
                .map(currContribution);
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

        ApiMaatCalculateContributionResponse response =
                appealContributionService.calculateAppealContribution(calculateContributionDTO);

        verify(maatCourtDataService, times(0))
                .findContribution(anyInt(), anyBoolean());
        assertThat(response.getContributionId()).isNull();
    }
}
