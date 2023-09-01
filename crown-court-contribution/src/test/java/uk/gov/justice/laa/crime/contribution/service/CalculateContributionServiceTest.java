package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.builder.CalculateContributionResponseBuilder;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionVariationDTO;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    @Mock
    private CreateContributionRequestMapper createContributionRequestMapper;

    @Mock
    private CrimeHardshipService crimeHardshipService;

    @Mock
    private ContributionRulesService contributionRulesService;

    @Test
    void givenAValidCaseType_whenCalculateContributionIsInvoked_thenShouldNotCalledCalculateContribution() {
        when(maatCourtDataService.getRepOrderByRepId(anyInt(), anyString())).thenReturn(TestModelDataBuilder.getRepOrderDTO());
        when(appealContributionService.calculateAppealContribution(any(CalculateContributionDTO.class), anyString())).thenReturn(new CalculateContributionResponse());
        calculateContributionService.calculateContribution(CalculateContributionDTO.builder().repId(120).caseType(CaseType.APPEAL_CC).build(),
                TestModelDataBuilder.LAA_TRANSACTION_ID);
        verify(appealContributionService, times(1)).calculateAppealContribution(any(), anyString());
    }

    @Test
    void givenValidContributionAndCompareResultIsLessThanTwo_whenCreateContribsIsInvoked_thenContributionIsReturn() {
        when(compareContributionService.compareContribution(any())).thenReturn(1);
        when(maatCourtDataService.createContribution(any(), any())).thenReturn(TestModelDataBuilder.getContribution());
        Contribution result = calculateContributionService.createContribs(new CalculateContributionDTO(), TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(result).isNotNull();
    }

    @Test
    void givenValidContributionAndCompareResultIsGreaterThanTwo_whenCreateContribsIsInvoked_thenNullIsReturn() {
        when(compareContributionService.compareContribution(any())).thenReturn(3);
        Contribution result = calculateContributionService.createContribs(new CalculateContributionDTO(), TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(result).isNull();
    }

    @Test
    void givenNoCommittalDateAndPassportAssessmentDate_whenGetEffectiveDateIsInvoked_thenPassportAssessmentDateIsReturned() {
        LocalDateTime passportAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withAssessmentDate(passportAssessmentDate)))
                .build();
        LocalDate effectiveDate = CalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate.getMonth()).isEqualTo(passportAssessmentDate.getMonth());
        assertThat(effectiveDate.getYear()).isEqualTo(passportAssessmentDate.getYear());
        assertThat(effectiveDate.getDayOfMonth()).isEqualTo(passportAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenNoCommittalDateAndFullMeansAssessmentDate_whenGetEffectiveDateIsInvoked_thenFullMeansAssessmentDateIsReturned() {
        LocalDateTime fullMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.FULL)
                        .withAssessmentDate(fullMeansAssessmentDate)))
                .build();
        LocalDate effectiveDate = CalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate.getMonth()).isEqualTo(fullMeansAssessmentDate.getMonth());
        assertThat(effectiveDate.getYear()).isEqualTo(fullMeansAssessmentDate.getYear());
        assertThat(effectiveDate.getDayOfMonth()).isEqualTo(fullMeansAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenNoCommittalDateAndInitMeansAssessmentDate_whenGetEffectiveDateIsInvoked_thenInitMeansAssessmentDateIsReturned() {
        LocalDateTime initMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withAssessmentDate(initMeansAssessmentDate)))
                .build();
        LocalDate effectiveDate = CalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate.getMonth()).isEqualTo(initMeansAssessmentDate.getMonth());
        assertThat(effectiveDate.getYear()).isEqualTo(initMeansAssessmentDate.getYear());
        assertThat(effectiveDate.getDayOfMonth()).isEqualTo(initMeansAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenCommittalDateAfterAssessmentDate_whenGetEffectiveDateIsInvoked_thenCommittalDateIsReturned() {
        LocalDateTime initMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        LocalDate committalDate = LocalDate.of(2023, 9, 28);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withAssessmentDate(initMeansAssessmentDate)))
                .committalDate(committalDate)
                .build();
        LocalDate effectiveDate = CalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate).isEqualTo(committalDate);
    }

    @Test
    void givenCommittalDateBeforeAssessmentDate_whenGetEffectiveDateIsInvoked_thenAssessmentDateIsReturned() {
        LocalDateTime initMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        LocalDate committalDate = LocalDate.of(2023, 7, 28);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withAssessmentDate(initMeansAssessmentDate)))
                .committalDate(committalDate)
                .build();
        LocalDate effectiveDate = CalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate.getMonth()).isEqualTo(initMeansAssessmentDate.getMonth());
        assertThat(effectiveDate.getYear()).isEqualTo(initMeansAssessmentDate.getYear());
        assertThat(effectiveDate.getDayOfMonth()).isEqualTo(initMeansAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenMonthlyContributionsGreater_whenCalculateUpliftedMonthlyAmountIsInvoked_thenMonthlyContributionsIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minUpliftedMonthlyAmount = BigDecimal.valueOf(80);
        BigDecimal monthlyContributions = BigDecimal.valueOf(83);
        ContributionCalcParametersDTO contributionCalcParametersDTO = ContributionCalcParametersDTO.builder()
                .upliftedIncomePercent(BigDecimal.TEN)
                .minUpliftedMonthlyAmount(minUpliftedMonthlyAmount)
                .build();
        assertThat(CalculateContributionService.calculateUpliftedMonthlyAmount(annualDisposableIncome, contributionCalcParametersDTO))
                .isEqualTo(monthlyContributions);
    }

    @Test
    void givenMonthlyContributionsSmaller_whenCalculateUpliftedMonthlyAmountIsInvoked_thenMinUpliftMonthlyAmountIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minUpliftedMonthlyAmount = BigDecimal.valueOf(100);
        ContributionCalcParametersDTO contributionCalcParametersDTO = ContributionCalcParametersDTO.builder()
                .upliftedIncomePercent(BigDecimal.TEN)
                .minUpliftedMonthlyAmount(minUpliftedMonthlyAmount)
                .build();
        assertThat(CalculateContributionService.calculateUpliftedMonthlyAmount(annualDisposableIncome, contributionCalcParametersDTO))
                .isEqualTo(minUpliftedMonthlyAmount);
    }

    @Test
    void givenMonthlyContributionsSmaller_whenCalculateDisposableContributionIsInvoked_thenZeroIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(100);
        ContributionCalcParametersDTO contributionCalcParametersDTO = ContributionCalcParametersDTO.builder()
                .disposableIncomePercent(BigDecimal.TEN)
                .minimumMonthlyAmount(minimumMonthlyAmount)
                .build();
        assertThat(CalculateContributionService.calculateDisposableContribution(annualDisposableIncome, contributionCalcParametersDTO))
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenMonthlyContributionsLessThanZero_whenCalculateDisposableContributionIsInvoked_thenZeroIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(100);
        ContributionCalcParametersDTO contributionCalcParametersDTO = ContributionCalcParametersDTO.builder()
                .disposableIncomePercent(BigDecimal.TEN)
                .minimumMonthlyAmount(minimumMonthlyAmount)
                .build();
        assertThat(CalculateContributionService.calculateDisposableContribution(annualDisposableIncome, contributionCalcParametersDTO))
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenMonthlyContributionsGreater_whenCalculateDisposableContributionIsInvoked_thenMonthlyContributionsIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.valueOf(10000);
        BigDecimal minimumMonthlyAmount = BigDecimal.valueOf(80);
        BigDecimal monthlyContributions = BigDecimal.valueOf(83);
        ContributionCalcParametersDTO contributionCalcParametersDTO = ContributionCalcParametersDTO.builder()
                .disposableIncomePercent(BigDecimal.TEN)
                .minimumMonthlyAmount(minimumMonthlyAmount)
                .build();
        assertThat(CalculateContributionService.calculateDisposableContribution(annualDisposableIncome, contributionCalcParametersDTO))
                .isEqualTo(monthlyContributions);
    }

    @Test
    void givenUpfrontContributionGreater_whenCalculateUpfrontContributionsIsInvoked_thenContributionCapIsReturned() {
        BigDecimal contributionCap = BigDecimal.valueOf(80);
        BigDecimal monthlyContributions = BigDecimal.valueOf(83);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .monthlyContributions(monthlyContributions)
                .contributionCap(contributionCap)
                .build();
        ContributionCalcParametersDTO contributionCalcParametersDTO = ContributionCalcParametersDTO.builder()
                .upfrontTotalMonths(2)
                .build();
        assertThat(CalculateContributionService.calculateUpfrontContributions(calculateContributionDTO, contributionCalcParametersDTO))
                .isEqualTo(contributionCap);
    }

    @Test
    void givenUpfrontContributionSmaller_whenCalculateUpfrontContributionsIsInvoked_thenUpfrontContributionIsReturned() {
        BigDecimal contributionCap = BigDecimal.valueOf(80);
        BigDecimal monthlyContributions = BigDecimal.valueOf(75);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .monthlyContributions(monthlyContributions)
                .contributionCap(contributionCap)
                .build();
        ContributionCalcParametersDTO contributionCalcParametersDTO = ContributionCalcParametersDTO.builder()
                .upfrontTotalMonths(1)
                .build();
        assertThat(CalculateContributionService.calculateUpfrontContributions(calculateContributionDTO, contributionCalcParametersDTO))
                .isEqualTo(monthlyContributions);
    }

    @Test
    void givenPassportNewWorkReason_whenGetNewWorkReasonIsInvoked_thenPassportNewWorkReasonIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.INF)))
                .build();
        assertThat(CalculateContributionService.getNewWorkReason(calculateContributionDTO)).isEqualTo(NewWorkReason.INF);
    }

    @Test
    void givenInitialNewWorkReason_whenGetNewWorkReasonIsInvoked_thenInitialNewWorkReasonIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withNewWorkReason(NewWorkReason.NEW)))
                .build();
        assertThat(CalculateContributionService.getNewWorkReason(calculateContributionDTO)).isEqualTo(NewWorkReason.NEW);
    }

    @Test
    void givenFullNewWorkReason_whenGetNewWorkReasonIsInvoked_thenNullIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.FULL)
                        .withNewWorkReason(NewWorkReason.NEW)))
                .build();
        assertThat(CalculateContributionService.getNewWorkReason(calculateContributionDTO)).isNull();
    }

    @Test
    void givenContributionDateNotNull_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionDateIsReturned() {
        LocalDate contributionDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.INF)))
                .effectiveDate(contributionDate)
                .build();
        String effectiveDate = CalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, null, null);
        assertThat(effectiveDate).isEqualTo(contributionDate.toString());
    }

    @Test
    void givenNewWorkReasonAsINF_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenAssessmentDateIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.INF)))
                .build();
        LocalDate assessmentDate = LocalDate.now();
        String effectiveDate = CalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, null, assessmentDate);
        assertThat(effectiveDate).isEqualTo(assessmentDate.toString());
    }

    @Test
    void givenNewWorkReasonAsFMA_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenAssessmentDateIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.FMA)))
                .build();
        LocalDate assessmentDate = LocalDate.now();
        String effectiveDate = CalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, null, assessmentDate);
        assertThat(effectiveDate).isEqualTo(assessmentDate.toString());
    }

    @Test
    void givenNewWorkReasonAsPAIAndMonthlyContributionsAreSmaller_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionEffectiveDateIsReturned() {
        LocalDate effectiveDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.PAI)))
                .effectiveDate(effectiveDate)
                .monthlyContributions(BigDecimal.TEN)
                .build();
        assertThat(CalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, BigDecimal.valueOf(100), null))
                .isEqualTo(effectiveDate.toString());
    }

    @Test
    void givenNewWorkReasonAsPAIAndMonthlyContributionsAreEqual_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionEffectiveDateIsReturned() {
        LocalDate effectiveDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.PAI)))
                .effectiveDate(effectiveDate)
                .monthlyContributions(BigDecimal.TEN)
                .build();
        assertThat(CalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, BigDecimal.TEN, null))
                .isEqualTo(effectiveDate.toString());
    }

    @Test
    void givenNewWorkReasonAsPAIAndMonthlyContributionsAreGreater_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionEffectiveDateIsReturned() {
        LocalDate assessmentDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new Assessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.PAI)))
                .monthlyContributions(BigDecimal.valueOf(12))
                .build();
        assertThat(CalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, BigDecimal.TEN, assessmentDate))
                .isEqualTo(assessmentDate.toString());
    }

    @Test
    void givenAValidAnnualIncome_whenGetAnnualDisposableIncomeIsInvoked_thenAnnualIncomeIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        assertThat(CalculateContributionService.getAnnualDisposableIncome(null, annualDisposableIncome))
                .isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenAValidAnnualIncomeAfterMagHardship_whenGetAnnualDisposableIncomeIsInvoked_thenAnnualIncomeAfterMagHardshipIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterMagHardship(annualDisposableIncome)
                .build();
        assertThat(CalculateContributionService.getAnnualDisposableIncome(calculateContributionDTO, null))
                .isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenAValidTotalAnnualIncome_whenGetAnnualDisposableIncomeIsInvoked_thenTotalAnnualIncomeIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .totalAnnualDisposableIncome(annualDisposableIncome)
                .build();
        assertThat(CalculateContributionService.getAnnualDisposableIncome(calculateContributionDTO, null))
                .isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenNoIncome_whenGetAnnualDisposableIncomeIsInvoked_thenZeroIsReturned() {
        assertThat(CalculateContributionService.getAnnualDisposableIncome(CalculateContributionDTO.builder().build(), null))
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenValidVariationAmount_whenCalculateVariationAmountIsInvoked_thenVariationAmountIsReturned() {
        ApiCalculateHardshipByDetailResponse apiCalculateHardshipByDetailResponse = new ApiCalculateHardshipByDetailResponse()
                .withHardshipSummary(BigDecimal.TEN);
        when(crimeHardshipService.calculateHardshipForDetail(any(ApiCalculateHardshipByDetailRequest.class)))
                .thenReturn(apiCalculateHardshipByDetailResponse);
        ContributionVariationDTO contributionVariationDTO = ContributionVariationDTO.builder()
                .variationRule("+")
                .build();
        BigDecimal variationAmount = calculateContributionService.calculateVariationAmount(
                TestModelDataBuilder.REP_ID, TestModelDataBuilder.LAA_TRANSACTION_ID, contributionVariationDTO);
        assertThat(variationAmount).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenVariationRuleAsNull_whenCalculateVariationAmountIsInvoked_thenZeroIsReturned() {
        ApiCalculateHardshipByDetailResponse apiCalculateHardshipByDetailResponse = new ApiCalculateHardshipByDetailResponse()
                .withHardshipSummary(BigDecimal.TEN);
        when(crimeHardshipService.calculateHardshipForDetail(any(ApiCalculateHardshipByDetailRequest.class)))
                .thenReturn(apiCalculateHardshipByDetailResponse);
        ContributionVariationDTO contributionVariationDTO = ContributionVariationDTO.builder()
                .build();
        BigDecimal variationAmount = calculateContributionService.calculateVariationAmount(
                TestModelDataBuilder.REP_ID, TestModelDataBuilder.LAA_TRANSACTION_ID, contributionVariationDTO);
        assertThat(variationAmount).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenCalcContribsAsN_whenCalcContribsIsInvoked_validResponseIsReturned() {
        CalculateContributionResponse calculateContributionResponse = TestModelDataBuilder.getCalculateContributionResponse();
        CalculateContributionDTO calculateContributionDTO = setupDataForCalcContribsTests();
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.N)
                .build();
        assertThat(calculateContributionService.calcContribs(calculateContributionDTO, contributionResponseDTO, TestModelDataBuilder.LAA_TRANSACTION_ID))
                .isEqualTo(calculateContributionResponse);
    }

    private CalculateContributionDTO setupDataForCalcContribsTests() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getContributionDTOForCalcContribs();
        when(maatCourtDataService.getContributionCalcParameters(anyString(), anyString())).thenReturn(new ContributionCalcParametersDTO());
        when(contributionRulesService.getActiveCCOutcome(any())).thenReturn(CrownCourtOutcome.SUCCESSFUL);
        when(contributionRulesService.isContributionRuleApplicable(CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL))
                .thenReturn(false);
        return calculateContributionDTO;
    }

    @Test
    void givenUpliftCoteNotNullAndCalcContribsAsN_whenCalcContribsIsInvoked_validResponseIsReturned() {
        CalculateContributionResponse calculateContributionResponse = TestModelDataBuilder.getCalculateContributionResponse();
        CalculateContributionDTO calculateContributionDTO = setupDataForCalcContribsTests();
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.N)
                .upliftCote(1)
                .build();
        assertThat(calculateContributionService.calcContribs(calculateContributionDTO, contributionResponseDTO, TestModelDataBuilder.LAA_TRANSACTION_ID))
                .isEqualTo(calculateContributionResponse);
    }

    @Test
    void givenUpliftCoteNotNullAndUpliftAppliedIsPresent_whenCalcContribsIsInvoked_validResponseIsReturned() {
        CalculateContributionResponse calculateContributionResponse = TestModelDataBuilder.getCalculateContributionResponse();
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getContributionDTOForCalcContribs();
        when(contributionRulesService.getActiveCCOutcome(any())).thenReturn(CrownCourtOutcome.SUCCESSFUL);
        when(contributionRulesService.isContributionRuleApplicable(CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL))
                .thenReturn(false);
        when(maatCourtDataService.getContributionCalcParameters(anyString(), anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .upliftedIncomePercent(BigDecimal.TEN)
                        .minUpliftedMonthlyAmount(BigDecimal.ONE)
                        .build());
        calculateContributionDTO.setDateUpliftApplied(LocalDate.of(2023, 9, 9));
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.N)
                .upliftCote(1)
                .build();
        calculateContributionResponse.setMonthlyContributions(BigDecimal.ONE);
        calculateContributionResponse.setUpfrontContributions(null);
        calculateContributionResponse.setUpliftApplied(Constants.Y);
        calculateContributionResponse.setTotalMonths(null);
        assertThat(calculateContributionService.calcContribs(calculateContributionDTO, contributionResponseDTO, TestModelDataBuilder.LAA_TRANSACTION_ID))
                .isEqualTo(calculateContributionResponse);
    }

    @Test
    void givenDisposableIncomeAfterCrownHardship_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterCrownHardship(BigDecimal.TEN)
                .build();
        assertThat(calculateContributionService.calculateAnnualDisposableIncome(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, CrownCourtOutcome.SUCCESSFUL, false))
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenTotalAnnualDisposableIncome_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .totalAnnualDisposableIncome(BigDecimal.TEN)
                .build();
        assertThat(calculateContributionService.calculateAnnualDisposableIncome(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, CrownCourtOutcome.SUCCESSFUL, false))
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenNoIncome_whenCalculateAnnualDisposableIncomeIsInvoked_thenZeroIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .build();
        assertThat(calculateContributionService.calculateAnnualDisposableIncome(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, CrownCourtOutcome.SUCCESSFUL, false))
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenDisposableIncomeAfterCrownHardshipAndNoVariation_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterCrownHardship(BigDecimal.TEN)
                .magCourtOutcome(MagCourtOutcome.COMMITTED)
                .caseType(CaseType.INDICTABLE)
                .build();
        when(contributionRulesService.getContributionVariation(CaseType.INDICTABLE, MagCourtOutcome.COMMITTED,
                CrownCourtOutcome.SUCCESSFUL)).thenReturn(Optional.empty());
        assertThat(calculateContributionService.calculateAnnualDisposableIncome(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, CrownCourtOutcome.SUCCESSFUL, true))
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenDisposableIncomeAfterCrownHardshipAndValidVariation_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterCrownHardship(BigDecimal.TEN)
                .magCourtOutcome(MagCourtOutcome.COMMITTED)
                .caseType(CaseType.INDICTABLE)
                .build();
        when(contributionRulesService.getContributionVariation(CaseType.INDICTABLE, MagCourtOutcome.COMMITTED,
                CrownCourtOutcome.SUCCESSFUL)).thenReturn(Optional.of(ContributionVariationDTO.builder().build()));
        assertThat(calculateContributionService.calculateAnnualDisposableIncome(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, CrownCourtOutcome.SUCCESSFUL, true))
                .isEqualTo(BigDecimal.TEN);
    }

}