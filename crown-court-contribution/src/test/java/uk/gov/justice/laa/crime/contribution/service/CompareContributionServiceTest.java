package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.MagCourtOutcome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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
    }

    @Test
    void givenNoPreviousContributionAndCds15WorkAroundIsTrue_whenCompareContributionServiceIsInvoked_thenReturnZero() {
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                null, null, null, null, "N", null);
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of());
        when(contributionService.isCds15WorkAround(any())).thenReturn(true);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isZero();
    }

    @Test
    void givenNoPreviousContributionAndIsReassesment_whenCompareContributionServiceIsInvoked_thenReturnZero() {
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                null, null, null, null, "N", null);
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of());
        when(contributionService.checkReassessment(anyInt(), anyString())).thenReturn(true);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isZero();
    }

    @Test
    void givenActiveContributionAndHasMagCourtOutcome_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyString(), anyBoolean())).thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));
        when(contributionService.hasMessageOutcomeChanged(anyString(), any())).thenReturn(true);
        ContributionDTO contributionDTO = TestModelDataBuilder.getContributionDTOForCaseStatus(CaseType.COMMITAL.getCaseTypeString(),
                BigDecimal.valueOf(250), BigDecimal.valueOf(250), BigDecimal.valueOf(250), LocalDate.now(),"Y", MagCourtOutcome.APPEAL_TO_CC);
        int result = compareContributionService.compareContribution(contributionDTO);
        assertThat(result).isOne();
    }


}
