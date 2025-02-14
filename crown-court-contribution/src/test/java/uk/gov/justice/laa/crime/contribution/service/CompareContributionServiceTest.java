package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.ContributionResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    void givenNoPreviousContribution_whenShouldCreateContributionIsInvoked_thenReturnTrue() {
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
        Contribution inactiveContribution = TestModelDataBuilder.buildContributionForCompareContributionService();
        inactiveContribution.setActive("N");

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(inactiveContribution));

        boolean result = compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isTrue();
    }

    @Test
    void givenActiveNotIdenticalContribution_whenShouldCreateContributionIsInvoked_thenReturnTrue() {
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

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
            .thenReturn(List.of(
                    TestModelDataBuilder.buildContributionForCompareContributionService()
                )
            );

        boolean result = compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isTrue();
    }

    @Test
    void givenMultipleContributionsExistAndActiveContributionIsNotIdentical_whenShouldCreateContributionIsInvoked_thenReturnTrue() {
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

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                        TestModelDataBuilder.buildInactiveContributionForCompareContributionService(),
                        TestModelDataBuilder.buildContributionForCompareContributionService()
                    )
                );

        boolean result = compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isTrue();
    }

    @Test
    void givenMultipleContributionsExistAndActiveContributionIsIdentical_whenShouldCreateContributionIsInvoked_thenReturnFalse() {
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
            .monthlyAmount(BigDecimal.valueOf(250.00))
            .upfrontAmount(BigDecimal.valueOf(250.00))
            .contributionCap(BigDecimal.valueOf(250.00))
            .totalMonths(5)
            .isUplift(false)
            .basedOn("Means")
            .effectiveDate(LocalDate.now())
            .build();

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
            .thenReturn(List.of(
                    TestModelDataBuilder.buildInactiveContributionForCompareContributionService(),
                    TestModelDataBuilder.buildContributionForCompareContributionService()
                )
            );

        boolean result = compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isFalse();
    }

    @Test
    void givenActiveIdenticalContributionAndMagsOutcomeChanged_whenShouldCreateContributionIsInvoked_thenReturnTrue() {
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

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(contributionService.hasMessageOutcomeChanged(anyString(), any()))
                .thenReturn(true);

        boolean result = compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isTrue();
    }

    @Test
    void givenActiveIdenticalContributionWithCaseTypeAppealCC_whenShouldCreateContributionIsInvoked_thenReturnTrue() {
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

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(contributionService.hasMessageOutcomeChanged(anyString(), any()))
                .thenReturn(false);

        boolean result = compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isTrue();
    }

    @Test
    void givenActiveIdenticalContributionNonAppealCCWithoutMagsOutcomeChange_whenShouldCreateContributionIsInvoked_thenReturnFalse() {
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

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(
                                TestModelDataBuilder.buildContributionForCompareContributionService()
                        )
                );
        when(contributionService.hasMessageOutcomeChanged(anyString(), any()))
                .thenReturn(false);

        boolean result = compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isFalse();
    }
}
