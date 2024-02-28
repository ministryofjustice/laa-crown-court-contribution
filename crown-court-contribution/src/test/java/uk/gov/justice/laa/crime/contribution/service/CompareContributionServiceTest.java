package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.CorrespondenceState;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CorrespondenceStatus;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

        int result = compareContributionService.compareContribution(calculateContributionDTO);

        assertThat(result).isZero();

        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(any(CorrespondenceState.class));
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

        int result = compareContributionService.compareContribution(calculateContributionDTO);

        assertThat(result).isZero();

        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(any(CorrespondenceState.class));
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

        int result = compareContributionService.compareContribution(calculateContributionDTO);

        assertThat(result).isOne();

        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(any(CorrespondenceState.class));
    }

    @Test
    void givenActiveIdenticalContributionWithCaseTypeAndCorrespondenceStatusAppealCC_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(maatCourtDataService.findCorrespondenceState(anyInt())).
                thenReturn(CorrespondenceState.builder()
                        .status(CorrespondenceStatus.APPEAL_CC.getStatus())
                        .build()
                );

        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.APPEAL_CC.getCaseTypeString(),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        LocalDate.now(),
                        MagCourtOutcome.APPEAL_TO_CC
                );
        int result = compareContributionService.compareContribution(calculateContributionDTO);

        assertThat(result).isOne();

        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(any(CorrespondenceState.class));
    }

    @Test
    void givenActiveIdenticalContributionWithCorrespondenceStatusAppealCC_whenCompareContributionServiceIsInvoked_thenReturnTwo() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(maatCourtDataService.findCorrespondenceState(anyInt())).
                thenReturn(CorrespondenceState.builder()
                        .status(CorrespondenceStatus.APPEAL_CC.getStatus())
                        .build()
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
        int result = compareContributionService.compareContribution(calculateContributionDTO);

        assertThat(result).isEqualTo(2);

        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(any(CorrespondenceState.class));
    }

    @Test
    void givenActiveIdenticalContributionForCds15WorkAroundAndCorrespondenceStatusCds15_whenCompareContributionServiceIsInvoked_thenReturnTwo() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(contributionService.isCds15WorkAround(any()))
                .thenReturn(true);
        when(maatCourtDataService.findCorrespondenceState(anyInt()))
                .thenReturn(CorrespondenceState.builder()
                        .status(CorrespondenceStatus.CDS15.getStatus())
                        .build()
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
        int result = compareContributionService.compareContribution(calculateContributionDTO);

        assertThat(result).isEqualTo(2);

        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(any(CorrespondenceState.class));
    }

    @Test
    void givenActiveIdenticalContributionForCds15WorkAroundAndCorrespondenceStatusReass_whenCompareContributionServiceIsInvoked_thenReturnOne() {
        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(contributionService.isCds15WorkAround(any()))
                .thenReturn(true);
        when(maatCourtDataService.findCorrespondenceState(anyInt())).
                thenReturn(CorrespondenceState.builder()
                        .status(CorrespondenceStatus.REASS.getStatus())
                        .build()
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

        int result = compareContributionService.compareContribution(calculateContributionDTO);

        assertThat(result).isOne();

        verify(maatCourtDataService, times(1))
                .updateCorrespondenceState(any(CorrespondenceState.class));
    }

}
