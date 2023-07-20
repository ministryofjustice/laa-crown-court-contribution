package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateContributionServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private AppealContributionService appealContributionService;

    @InjectMocks
    private CalculateContributionService calculateContributionService;

    @Mock
    private CompareContributionService compareContributionService;

    @Test
    void givenAInvalidCaseType_whenCalculateContributionIsInvoked_thenShouldNotCalledCalculateContribution() {
        when(maatCourtDataService.getRepOrderByRepId(anyInt(), anyString())).thenReturn(TestModelDataBuilder.getRepOrderDTO());
        calculateContributionService.calculateContribution(ContributionDTO.builder().repId(120).build(), TestModelDataBuilder.LAA_TRANSACTION_ID);
        verify(appealContributionService, times(0)).calculateContribution(any(), anyString());
    }

    @Test
    void givenAValidCaseType_whenCalculateContributionIsInvoked_thenShouldNotCalledCalculateContribution() {
        when(maatCourtDataService.getRepOrderByRepId(anyInt(), anyString())).thenReturn(TestModelDataBuilder.getRepOrderDTO());
        when(appealContributionService.calculateContribution(any(ContributionDTO.class), anyString())).thenReturn(ContributionDTO.builder().build());
        calculateContributionService.calculateContribution(ContributionDTO.builder().repId(120).caseType(CaseType.APPEAL_CC).build(),
                TestModelDataBuilder.LAA_TRANSACTION_ID);
        verify(appealContributionService, times(1)).calculateContribution(any(), anyString());
    }


    @Test
    void givenValidContributionAndCompareResultIsLessThanTwo_whenCreateContribsIsInvoked_thenContributionIsReturn() {
        when(compareContributionService.compareContribution(any())).thenReturn(1);
        when(maatCourtDataService.createContribution(any(CreateContributionRequest.class), any())).thenReturn(TestModelDataBuilder.getContribution());
        Contribution result = calculateContributionService.createContribs(new CreateContributionRequest(), TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(result).isNotNull();
    }

    @Test
    void givenValidContributionAndCompareResultIsGreaterThanTwo_whenCreateContribsIsInvoked_thenNullIsReturn() {
        when(compareContributionService.compareContribution(any())).thenReturn(3);
        Contribution result = calculateContributionService.createContribs(new CreateContributionRequest(), TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(result).isNull();
    }

}