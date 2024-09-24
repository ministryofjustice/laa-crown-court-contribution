package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.ContributionResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.contribution.CorrespondenceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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
    void givenNoPreviousContributionAndCaseTypeIsAppealCC_whenCompareContributionServiceIsInvoked_thenReturnZero() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of());
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.APPEAL_CC.getCaseTypeString(),
                        null,
                        null,
                        null,
                        null,
                        null
                );
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();

        int result = compareContributionService.compareContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isZero();
        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(anyInt(), eq(CorrespondenceStatus.APPEAL_CC));
    }

    @Test
    void givenNoPreviousContributionAndCds15WorkAroundIsTrue_whenCompareContributionServiceIsInvoked_thenReturnZero() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of());
        when(contributionService.isCds15WorkAround(any()))
                .thenReturn(true);
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.COMMITAL.getCaseTypeString(),
                        null,
                        null,
                        null,
                        null,
                        null
                );
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();

        int result = compareContributionService.compareContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isZero();
        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(anyInt(), eq(CorrespondenceStatus.CDS15));
    }

    @Test
    void givenActiveNotIdenticalContribution_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.COMMITAL.getCaseTypeString(),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        LocalDate.now(),
                        MagCourtOutcome.APPEAL_TO_CC
                );
        ContributionResult contributionResult = ContributionResult.builder()
                .totalAnnualDisposableIncome(BigDecimal.valueOf(16000.00))
                .monthlyAmount(BigDecimal.valueOf(300.00))
                .upfrontAmount(BigDecimal.valueOf(250.00))
                .contributionCap(BigDecimal.valueOf(250.00))
                .totalMonths(5)
                .isUplift(false)
                .basedOn("Means")
                .effectiveDate(LocalDate.now())
                .build();

        int result = compareContributionService.compareContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isOne();
        verify(maatCourtDataService, times(0))
                .updateCorrespondenceState(anyInt(), any(CorrespondenceStatus.class));
    }

    @Test
    void givenActiveIdenticalContributionAndHasMagCourtOutcome_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(contributionService.hasMessageOutcomeChanged(anyString(), any()))
                .thenReturn(true);
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.COMMITAL.getCaseTypeString(),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        LocalDate.now(),
                        MagCourtOutcome.APPEAL_TO_CC
                );
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();

        int result = compareContributionService.compareContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isOne();
        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(anyInt(), eq(CorrespondenceStatus.APPEAL_CC));
    }

    @Test
    void givenActiveIdenticalContributionWithCaseTypeAndCorrespondenceStatusAppealCC_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(maatCourtDataService.findCorrespondenceState(anyInt())).
                thenReturn(CorrespondenceStatus.APPEAL_CC);
        when(contributionService.hasMessageOutcomeChanged(anyString(), any()))
                .thenReturn(false);
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.APPEAL_CC.getCaseTypeString(),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        LocalDate.now(),
                        MagCourtOutcome.APPEAL_TO_CC
                );
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();

        int result = compareContributionService.compareContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isOne();
        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(anyInt(), eq(CorrespondenceStatus.APPEAL_CC));
    }

    @Test
    void givenActiveIdenticalContributionWithCorrespondenceStatusAppealCC_whenCompareContributionServiceIsInvoked_thenReturnTwo() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(maatCourtDataService.findCorrespondenceState(anyInt())).
                thenReturn(CorrespondenceStatus.APPEAL_CC);
        when(contributionService.hasMessageOutcomeChanged(anyString(), any()))
                .thenReturn(false);
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.COMMITAL.getCaseTypeString(),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        LocalDate.now(),
                        MagCourtOutcome.APPEAL_TO_CC
                );
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();

        int result = compareContributionService.compareContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isEqualTo(2);
        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(anyInt(), eq(CorrespondenceStatus.NONE));
    }

    @Test
    void givenActiveIdenticalContributionForCds15WorkAroundAndCorrespondenceStatusCds15_whenCompareContributionServiceIsInvoked_thenReturnTwo() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(maatCourtDataService.findCorrespondenceState(anyInt()))
                .thenReturn(CorrespondenceStatus.CDS15);
        when(contributionService.hasMessageOutcomeChanged(anyString(), any()))
                .thenReturn(false);
        when(contributionService.isCds15WorkAround(any()))
                .thenReturn(true);
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.COMMITAL.getCaseTypeString(),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        LocalDate.now(),
                        MagCourtOutcome.APPEAL_TO_CC
                );
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();

        int result = compareContributionService.compareContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isEqualTo(2);
        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(anyInt(), eq(CorrespondenceStatus.NONE));
    }

    @Test
    void givenActiveIdenticalContributionForCds15WorkAroundAndCorrespondenceStatusReass_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(maatCourtDataService.findCorrespondenceState(anyInt()))
                .thenReturn(CorrespondenceStatus.REASS);
        when(contributionService.hasMessageOutcomeChanged(anyString(), any()))
                .thenReturn(false);
        when(contributionService.isCds15WorkAround(any()))
                .thenReturn(true);
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.COMMITAL.getCaseTypeString(),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        LocalDate.now(),
                        MagCourtOutcome.APPEAL_TO_CC
                );
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();

        int result = compareContributionService.compareContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isOne();
        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(anyInt(), eq(CorrespondenceStatus.CDS15));
    }

}
