package uk.gov.justice.laa.crime.contribution.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.ContributionResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompareContributionServiceTest {

    @InjectMocks
    private CompareContributionService compareContributionService;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Test
    void givenNoPreviousContribution_whenShouldCreateContributionIsInvoked_thenReturnTrue() {
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.EITHER_WAY.getCaseTypeString(), null, null, null, null, null);
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();
        Contribution inactiveContribution = TestModelDataBuilder.buildContributionForCompareContributionService();
        inactiveContribution.setActive("N");

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean())).thenReturn(List.of(inactiveContribution));

        boolean result =
                compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @MethodSource("getActiveNonIdenticalContribution")
    void givenActiveNonIdenticalContribution_whenShouldCreateContributionIsInvoked_thenReturnTrue(
            BigDecimal contributionCap,
            BigDecimal upfrontContributions,
            BigDecimal monthlyContributions,
            LocalDate effectiveDate) {
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.COMMITAL.getCaseTypeString(),
                        contributionCap,
                        upfrontContributions,
                        monthlyContributions,
                        effectiveDate,
                        MagCourtOutcome.COMMITTED);
        ContributionResult contributionResult = ContributionResult.builder()
                .totalAnnualDisposableIncome(BigDecimal.valueOf(16000.00))
                .monthlyAmount(monthlyContributions)
                .upfrontAmount(upfrontContributions)
                .contributionCap(contributionCap)
                .totalMonths(5)
                .isUplift(false)
                .basedOn("Means")
                .effectiveDate(effectiveDate)
                .build();

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));

        boolean result =
                compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isTrue();
    }

    @Test
    void
            givenMultipleContributionsExistAndActiveContributionIsNotIdentical_whenShouldCreateContributionIsInvoked_thenReturnTrue() {
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.COMMITAL.getCaseTypeString(),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        LocalDate.now(),
                        MagCourtOutcome.COMMITTED);
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
                        TestModelDataBuilder.buildContributionForCompareContributionService()));

        boolean result =
                compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isTrue();
    }

    @Test
    void givenActiveIdenticalContributionAndMagsOutcomeChanged_whenShouldCreateContributionIsInvoked_thenReturnTrue() {
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.EITHER_WAY.getCaseTypeString(),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        LocalDate.now(),
                        MagCourtOutcome.COMMITTED_FOR_TRIAL);
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();

        when(maatCourtDataService.findContribution(anyInt(), anyBoolean()))
                .thenReturn(List.of(TestModelDataBuilder.buildContributionForCompareContributionService()));

        boolean result =
                compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isTrue();
    }

    @Test
    void
            givenMultipleContributionsExistActiveContributionIsIdenticalAndMagsOutcomeUnchanged_whenShouldCreateContributionIsInvoked_thenReturnFalse() {
        CalculateContributionDTO calculateContributionDTO =
                TestModelDataBuilder.getContributionDTOForCompareContributionService(
                        CaseType.COMMITAL.getCaseTypeString(),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        LocalDate.now(),
                        MagCourtOutcome.COMMITTED);
        calculateContributionDTO.getRepOrderDTO().setMagsOutcome(MagCourtOutcome.COMMITTED.getOutcome());
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
                        TestModelDataBuilder.buildContributionForCompareContributionService()));

        boolean result =
                compareContributionService.shouldCreateContribution(calculateContributionDTO, contributionResult);

        assertThat(result).isFalse();
    }

    private static Stream<Arguments> getActiveNonIdenticalContribution() {
        return Stream.of(
                Arguments.of(
                        BigDecimal.valueOf(500), BigDecimal.valueOf(250), BigDecimal.valueOf(300), LocalDate.now()),
                Arguments.of(
                        BigDecimal.valueOf(250), BigDecimal.valueOf(500), BigDecimal.valueOf(300), LocalDate.now()),
                Arguments.of(
                        BigDecimal.valueOf(250), BigDecimal.valueOf(250), BigDecimal.valueOf(500), LocalDate.now()),
                Arguments.of(
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(250),
                        BigDecimal.valueOf(300),
                        LocalDate.now().minusDays(1)));
    }
}
