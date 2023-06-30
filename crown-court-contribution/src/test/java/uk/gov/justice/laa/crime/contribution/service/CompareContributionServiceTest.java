package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.CorrespondenceState;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CorrespondenceStatus;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.MagCourtOutcome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompareContributionServiceTest {

    @InjectMocks
    private CompareContributionService compareContributionService;
    @Mock
    private MaatCourtDataService maatCourtDataService;
    @Mock
    private ContributionService contributionService;

    @Test
    void givenNoPreviousContributionAndWhenCaseTypeIsApealCC_whenCompareContributionServiceIsInvoked_thenReturnZero() {
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.APPEAL_CC.getCaseTypeString(),
                null, null, null, null, "N", null);
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of());
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isZero();
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

    @Test
    void givenNoPreviousContributionAndCds15WorkAroundIsTrue_whenCompareContributionServiceIsInvoked_thenReturnZero() {
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                null, null, null, null, "N", null);
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of());
        when(contributionService.isCds15WorkAround(any())).thenReturn(true);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isZero();
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

    @Test
    void givenNoPreviousContributionAndIsReassesment_whenCompareContributionServiceIsInvoked_thenReturnZero() {
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                null, null, null, null, "N", null);
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of());
        when(contributionService.checkReassessment(anyInt(), anyString())).thenReturn(true);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isZero();
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

    @Test
    void givenActiveContributionIdenticalContributionsAndHasMagCourtOutcome_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));
        when(contributionService.hasMessageOutcomeChanged(anyString(), any())).thenReturn(true);
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                BigDecimal.valueOf(250), BigDecimal.valueOf(250), BigDecimal.valueOf(250), LocalDate.now(),"Y", MagCourtOutcome.APPEAL_TO_CC);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isOne();
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

    @Test
    void givenActiveNonContributionWithReassesment_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));
        when(contributionService.checkReassessment(anyInt(), anyString())).thenReturn(true);
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                BigDecimal.valueOf(250), BigDecimal.valueOf(1), BigDecimal.valueOf(250), LocalDate.now(),"Y", MagCourtOutcome.APPEAL_TO_CC);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isOne();
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

    @Test
    void givenActiveIdenticalContributionWithCaseTypeAndCorrespondenceSatusAsApealToCC_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));
        when(maatCourtDataService.findCorrespondenceState(anyInt(), anyString())).thenReturn(CorrespondenceState.builder().status(CorrespondenceStatus.APPEAL_CC.getStatus()).build());
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.APPEAL_CC.getCaseTypeString(),
                BigDecimal.valueOf(250), BigDecimal.valueOf(250), BigDecimal.valueOf(250), LocalDate.now(),"Y", MagCourtOutcome.APPEAL_TO_CC);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isOne();
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

    @Test
    void givenActiveIdenticalContributionWithCorrespondenceSatusAsApealToCC_whenCompareContributionServiceIsInvoked_thenReturnTwo() {
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));
        when(maatCourtDataService.findCorrespondenceState(anyInt(), anyString())).thenReturn(CorrespondenceState.builder().status(CorrespondenceStatus.APPEAL_CC.getStatus()).build());
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                BigDecimal.valueOf(250), BigDecimal.valueOf(250), BigDecimal.valueOf(250), LocalDate.now(),"Y", MagCourtOutcome.APPEAL_TO_CC);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isEqualTo(2);
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

    @Test
    void givenActiveIdenticalContributionForCds15EWorkAroundAndCorrespondenceStatusCds15_whenCompareContributionServiceIsInvoked_thenReturnTwo() {
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));
        when(contributionService.isCds15WorkAround(any())).thenReturn(true);
        when(maatCourtDataService.findCorrespondenceState(anyInt(), anyString())).thenReturn(CorrespondenceState.builder().status(CorrespondenceStatus.CDS15.getStatus()).build());
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                BigDecimal.valueOf(250), BigDecimal.valueOf(250), BigDecimal.valueOf(250), LocalDate.now(),"Y", MagCourtOutcome.APPEAL_TO_CC);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isEqualTo(2);
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

    @Test
    void givenActiveIdenticalContributionForCds15EWorkAroundAndCorrespondenceStatusReass_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));
        when(contributionService.isCds15WorkAround(any())).thenReturn(true);
        when(maatCourtDataService.findCorrespondenceState(anyInt(), anyString())).thenReturn(CorrespondenceState.builder().status(CorrespondenceStatus.REASS.getStatus()).build());
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                BigDecimal.valueOf(250), BigDecimal.valueOf(250), BigDecimal.valueOf(250), LocalDate.now(),"Y", MagCourtOutcome.APPEAL_TO_CC);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isOne();
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

    @Test
    void givenActiveIdenticalContributionForReassAndCorrespondenceStatusReass_whenCompareContributionServiceIsInvoked_thenReturnTwo() {
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));
        when(maatCourtDataService.findCorrespondenceState(anyInt(), anyString())).thenReturn(CorrespondenceState.builder().status(CorrespondenceStatus.REASS.getStatus()).build());
        when(contributionService.checkReassessment(anyInt(), anyString())).thenReturn(true);
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                BigDecimal.valueOf(250), BigDecimal.valueOf(250), BigDecimal.valueOf(250), LocalDate.now(),"Y", MagCourtOutcome.APPEAL_TO_CC);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isEqualTo(2);
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

    @Test
    void givenActiveIdenticalContributionForReassAndCorrespondenceStatusNotReass_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));
        when(maatCourtDataService.findCorrespondenceState(anyInt(), anyString())).thenReturn(CorrespondenceState.builder().status(CorrespondenceStatus.CDS15.getStatus()).build());
        when(contributionService.checkReassessment(anyInt(), anyString())).thenReturn(true);
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                BigDecimal.valueOf(250), BigDecimal.valueOf(250), BigDecimal.valueOf(250), LocalDate.now(),"Y", MagCourtOutcome.APPEAL_TO_CC);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isOne();
        verify(maatCourtDataService, times(1)).createCorrespondenceState(any(CorrespondenceState.class), anyString());
    }

}
