package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.builder.CalculateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.ContributionSummaryMapper;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.MaatCalculateContributionResponseMapper;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.*;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.common.ApiAssessment;
import uk.gov.justice.laa.crime.contribution.model.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiMaatCalculateContributionResponse;
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
class MaatCalculateContributionServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private AppealContributionService appealContributionService;

    @InjectMocks
    private MaatCalculateContributionService maatCalculateContributionService;

    @Mock
    private CompareContributionService compareContributionService;
    @Mock
    private CreateContributionRequestMapper createContributionRequestMapper;

    @Mock
    private CrimeHardshipService crimeHardshipService;

    @Mock
    private ContributionRulesService contributionRulesService;

    @Mock
    private ContributionService contributionService;

    @Mock
    private CalculateContributionService calculateContributionService;

    @Mock
    private CalculateContributionRequestMapper calculateContributionRequestMapper;

    @Mock
    private MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper;

    @Mock
    private ContributionSummaryMapper contributionSummaryMapper;

    @Test
    void givenAValidCaseType_whenCalculateContributionIsInvoked_thenShouldNotCalledCalculateContribution() {
        when(maatCourtDataService.getRepOrderByRepId(anyInt(), anyString())).thenReturn(TestModelDataBuilder.getRepOrderDTO());
        when(appealContributionService.calculateAppealContribution(any(CalculateContributionDTO.class), anyString())).thenReturn(new ApiMaatCalculateContributionResponse());
        maatCalculateContributionService.calculateContribution(CalculateContributionDTO.builder().repId(120).caseType(CaseType.APPEAL_CC).build(),
                TestModelDataBuilder.LAA_TRANSACTION_ID);
        verify(appealContributionService, times(1)).calculateAppealContribution(any(), anyString());
    }

    @Test
    void givenValidContributionAndCompareResultIsLessThanTwo_whenCreateContribsIsInvoked_thenContributionIsReturn() {
        when(compareContributionService.compareContribution(any())).thenReturn(1);
        when(maatCourtDataService.createContribution(any(), any())).thenReturn(TestModelDataBuilder.getContribution());
        Contribution result = maatCalculateContributionService.createContribs(new CalculateContributionDTO(), TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(result).isNotNull();
    }

    @Test
    void givenValidContributionAndCompareResultIsGreaterThanTwo_whenCreateContribsIsInvoked_thenNullIsReturn() {
        when(compareContributionService.compareContribution(any())).thenReturn(3);
        Contribution result = maatCalculateContributionService.createContribs(new CalculateContributionDTO(), TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(result).isNull();
    }

    @Test
    void givenNoCommittalDateAndPassportAssessmentDate_whenGetEffectiveDateIsInvoked_thenPassportAssessmentDateIsReturned() {
        LocalDateTime passportAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withAssessmentDate(passportAssessmentDate)))
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate.getMonth()).isEqualTo(passportAssessmentDate.getMonth());
        assertThat(effectiveDate.getYear()).isEqualTo(passportAssessmentDate.getYear());
        assertThat(effectiveDate.getDayOfMonth()).isEqualTo(passportAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenNoCommittalDateAndFullMeansAssessmentDate_whenGetEffectiveDateIsInvoked_thenFullMeansAssessmentDateIsReturned() {
        LocalDateTime fullMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.FULL)
                        .withAssessmentDate(fullMeansAssessmentDate)))
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate.getMonth()).isEqualTo(fullMeansAssessmentDate.getMonth());
        assertThat(effectiveDate.getYear()).isEqualTo(fullMeansAssessmentDate.getYear());
        assertThat(effectiveDate.getDayOfMonth()).isEqualTo(fullMeansAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenNoCommittalDateAndInitMeansAssessmentDate_whenGetEffectiveDateIsInvoked_thenInitMeansAssessmentDateIsReturned() {
        LocalDateTime initMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withAssessmentDate(initMeansAssessmentDate)))
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate.getMonth()).isEqualTo(initMeansAssessmentDate.getMonth());
        assertThat(effectiveDate.getYear()).isEqualTo(initMeansAssessmentDate.getYear());
        assertThat(effectiveDate.getDayOfMonth()).isEqualTo(initMeansAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenCommittalDateAfterAssessmentDate_whenGetEffectiveDateIsInvoked_thenCommittalDateIsReturned() {
        LocalDateTime initMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        LocalDate committalDate = LocalDate.of(2023, 9, 28);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withAssessmentDate(initMeansAssessmentDate)))
                .committalDate(committalDate)
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate).isEqualTo(committalDate);
    }

    @Test
    void givenCommittalDateBeforeAssessmentDate_whenGetEffectiveDateIsInvoked_thenAssessmentDateIsReturned() {
        LocalDateTime initMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        LocalDate committalDate = LocalDate.of(2023, 7, 28);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withAssessmentDate(initMeansAssessmentDate)))
                .committalDate(committalDate)
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate.getMonth()).isEqualTo(initMeansAssessmentDate.getMonth());
        assertThat(effectiveDate.getYear()).isEqualTo(initMeansAssessmentDate.getYear());
        assertThat(effectiveDate.getDayOfMonth()).isEqualTo(initMeansAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenNoAssessments_whenGetEffectiveDateIsInvoked_thenNullIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of())
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);
        assertThat(effectiveDate).isNull();
    }

    @Test
    void givenPassportNewWorkReason_whenGetNewWorkReasonIsInvoked_thenPassportNewWorkReasonIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.INF)))
                .build();
        assertThat(MaatCalculateContributionService.getNewWorkReason(calculateContributionDTO)).isEqualTo(NewWorkReason.INF);
    }

    @Test
    void givenInitialNewWorkReason_whenGetNewWorkReasonIsInvoked_thenInitialNewWorkReasonIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withNewWorkReason(NewWorkReason.NEW)))
                .build();
        assertThat(MaatCalculateContributionService.getNewWorkReason(calculateContributionDTO)).isEqualTo(NewWorkReason.NEW);
    }

    @Test
    void givenFullNewWorkReason_whenGetNewWorkReasonIsInvoked_thenNullIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.FULL)
                        .withNewWorkReason(NewWorkReason.NEW)))
                .build();
        assertThat(MaatCalculateContributionService.getNewWorkReason(calculateContributionDTO)).isNull();
    }

    @Test
    void givenContributionDateNotNull_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionDateIsReturned() {
        LocalDate contributionDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.INF)))
                .effectiveDate(contributionDate)
                .build();
        String effectiveDate = MaatCalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, null, null);
        assertThat(effectiveDate).isEqualTo(contributionDate.toString());
    }

    @Test
    void givenNewWorkReasonAsINF_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenAssessmentDateIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.INF)))
                .build();
        LocalDate assessmentDate = LocalDate.now();
        String effectiveDate = MaatCalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, null, assessmentDate);
        assertThat(effectiveDate).isEqualTo(assessmentDate.toString());
    }

    @Test
    void givenNewWorkReasonAsFMA_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenAssessmentDateIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.FMA)))
                .build();
        LocalDate assessmentDate = LocalDate.now();
        String effectiveDate = MaatCalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, null, assessmentDate);
        assertThat(effectiveDate).isEqualTo(assessmentDate.toString());
    }

    @Test
    void givenNewWorkReasonAsPAIAndMonthlyContributionsAreSmaller_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionEffectiveDateIsReturned() {
        LocalDate effectiveDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.PAI)))
                .effectiveDate(effectiveDate)
                .monthlyContributions(BigDecimal.TEN)
                .build();
        assertThat(MaatCalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, BigDecimal.valueOf(100), null))
                .isEqualTo(effectiveDate.toString());
    }

    @Test
    void givenNewWorkReasonAsPAIAndMonthlyContributionsAreEqual_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionEffectiveDateIsReturned() {
        LocalDate effectiveDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.PAI)))
                .effectiveDate(effectiveDate)
                .monthlyContributions(BigDecimal.TEN)
                .build();
        assertThat(MaatCalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, BigDecimal.TEN, null))
                .isEqualTo(effectiveDate.toString());
    }

    @Test
    void givenNewWorkReasonAsPAIAndMonthlyContributionsAreGreater_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionEffectiveDateIsReturned() {
        LocalDate assessmentDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.PAI)))
                .monthlyContributions(BigDecimal.valueOf(12))
                .build();
        assertThat(MaatCalculateContributionService.getEffectiveDateByNewWorkReason(calculateContributionDTO, BigDecimal.TEN, assessmentDate))
                .isEqualTo(assessmentDate.toString());
    }

    @Test
    void givenAValidAnnualIncome_whenGetAnnualDisposableIncomeIsInvoked_thenAnnualIncomeIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        assertThat(MaatCalculateContributionService.getAnnualDisposableIncome(null, annualDisposableIncome))
                .isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenAValidAnnualIncomeAfterMagHardship_whenGetAnnualDisposableIncomeIsInvoked_thenAnnualIncomeAfterMagHardshipIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterMagHardship(annualDisposableIncome)
                .build();
        assertThat(MaatCalculateContributionService.getAnnualDisposableIncome(calculateContributionDTO, null))
                .isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenAValidTotalAnnualIncome_whenGetAnnualDisposableIncomeIsInvoked_thenTotalAnnualIncomeIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .totalAnnualDisposableIncome(annualDisposableIncome)
                .build();
        assertThat(MaatCalculateContributionService.getAnnualDisposableIncome(calculateContributionDTO, null))
                .isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenNoIncome_whenGetAnnualDisposableIncomeIsInvoked_thenZeroIsReturned() {
        assertThat(MaatCalculateContributionService.getAnnualDisposableIncome(CalculateContributionDTO.builder().build(), null))
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
        BigDecimal variationAmount = maatCalculateContributionService.calculateVariationAmount(
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
        BigDecimal variationAmount = maatCalculateContributionService.calculateVariationAmount(
                TestModelDataBuilder.REP_ID, TestModelDataBuilder.LAA_TRANSACTION_ID, contributionVariationDTO);
        assertThat(variationAmount).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenCalcContribsAsN_whenCalcContribsIsInvoked_validResponseIsReturned() {
        ApiMaatCalculateContributionResponse expectedResponse = TestModelDataBuilder.getApiMaatCalculateContributionResponse();
        CalculateContributionDTO calculateContributionDTO = setupDataForCalcContribsTests();
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.N)
                .build();
        when(calculateContributionRequestMapper.map(any(), any(), any(), any())).thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));
        when(calculateContributionService.calculateContribution(any())).thenReturn(TestModelDataBuilder.getCalculateContributionResponse());
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any())).thenReturn(expectedResponse);

        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.calcContribs(calculateContributionDTO, contributionResponseDTO, TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(actualResponse)
                .isEqualTo(expectedResponse);
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
        ApiMaatCalculateContributionResponse expectedResponse = TestModelDataBuilder.getApiMaatCalculateContributionResponse();
        CalculateContributionDTO calculateContributionDTO = setupDataForCalcContribsTests();
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.N)
                .upliftCote(1)
                .build();
        when(calculateContributionRequestMapper.map(any(), any(), any(), any())).thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));
        when(calculateContributionService.calculateContribution(any())).thenReturn(TestModelDataBuilder.getCalculateContributionResponse());
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any())).thenReturn(expectedResponse);
        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.calcContribs(calculateContributionDTO, contributionResponseDTO, TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(actualResponse)
                .isEqualTo(expectedResponse);
    }

    @Test
    void givenUpliftCoteNotNullAndUpliftAppliedIsPresent_whenCalcContribsIsInvoked_validResponseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getContributionDTOForCalcContribs();
        calculateContributionDTO.setDateUpliftApplied(LocalDate.of(2023, 9, 9));
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.N)
                .upliftCote(1)
                .build();
        ApiMaatCalculateContributionResponse expectedResponse = TestModelDataBuilder.getApiMaatCalculateContributionResponse();
        expectedResponse.setMonthlyContributions(BigDecimal.ONE);
        expectedResponse.setUpfrontContributions(BigDecimal.ZERO);
        expectedResponse.setUpliftApplied(Constants.Y);

        when(contributionRulesService.getActiveCCOutcome(any())).thenReturn(CrownCourtOutcome.SUCCESSFUL);
        when(contributionRulesService.isContributionRuleApplicable(CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL))
                .thenReturn(false);
        when(maatCourtDataService.getContributionCalcParameters(anyString(), anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .upliftedIncomePercent(BigDecimal.TEN)
                        .minUpliftedMonthlyAmount(BigDecimal.ONE)
                        .totalMonths(2)
                        .build());
        when(calculateContributionRequestMapper.map(any(), any(), any(), any())).thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));
        when(calculateContributionService.calculateContribution(any())).thenReturn(TestModelDataBuilder.getCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.ONE).withUpliftApplied(Constants.Y));
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any())).thenReturn(expectedResponse);

        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.calcContribs(calculateContributionDTO, contributionResponseDTO, TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(actualResponse)
                .isEqualTo(expectedResponse);
    }

    @Test
    void givenUpliftCoteNotNullAndUpliftIsRemoved_whenCalcContribsIsInvoked_validResponseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getContributionDTOForCalcContribs();
        calculateContributionDTO.setDateUpliftApplied(LocalDate.of(2023, 1, 9));
        calculateContributionDTO.setDateUpliftRemoved(LocalDate.of(2023, 2, 9));
        calculateContributionDTO.setContributionCap(BigDecimal.TEN);
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.Y)
                .upliftCote(1)
                .build();
        ApiMaatCalculateContributionResponse expectedResponse = TestModelDataBuilder.getApiMaatCalculateContributionResponse();
        expectedResponse.setMonthlyContributions(BigDecimal.ZERO);
        expectedResponse.setUpfrontContributions(BigDecimal.ZERO);
        expectedResponse.setUpliftApplied(Constants.N);
        expectedResponse.setTotalMonths(null);
        expectedResponse.setContributionCap(BigDecimal.TEN);
        expectedResponse.setBasedOn("Means");
        when(contributionRulesService.getActiveCCOutcome(any())).thenReturn(CrownCourtOutcome.SUCCESSFUL);
        when(contributionRulesService.isContributionRuleApplicable(CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL))
                .thenReturn(false);
        when(maatCourtDataService.getContributionCalcParameters(anyString(), anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .upliftedIncomePercent(BigDecimal.TEN)
                        .minUpliftedMonthlyAmount(BigDecimal.ONE)
                        .disposableIncomePercent(BigDecimal.TEN)
                        .minimumMonthlyAmount(BigDecimal.valueOf(100))
                        .upfrontTotalMonths(1)
                        .totalMonths(2)
                        .build());
        when(calculateContributionRequestMapper.map(any(), any(), any(), any())).thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));
        when(calculateContributionService.calculateContribution(any())).thenReturn(TestModelDataBuilder.getCalculateContributionResponse().withBasedOn(Constants.MEANS));
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any())).thenReturn(expectedResponse);
        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.calcContribs(calculateContributionDTO, contributionResponseDTO, TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(actualResponse).isEqualTo(expectedResponse);

    }

    @Test
    void givenUpliftIsRemovedAndAnnualDisposableIncome_whenCalcContribsIsInvoked_validResponseIsReturned() {
        BigDecimal contributionCap = BigDecimal.valueOf(12);
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getContributionDTOForCalcContribs();
        calculateContributionDTO.setDateUpliftApplied(LocalDate.of(2023, 1, 9));
        calculateContributionDTO.setDateUpliftRemoved(LocalDate.of(2023, 2, 9));
        calculateContributionDTO.setContributionCap(contributionCap);
        calculateContributionDTO.setDisposableIncomeAfterCrownHardship(BigDecimal.valueOf(20000));
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.Y)
                .upliftCote(1)
                .build();
        ApiMaatCalculateContributionResponse expectedResponse = TestModelDataBuilder.getApiMaatCalculateContributionResponse();
        expectedResponse.setMonthlyContributions(contributionCap);
        expectedResponse.setUpfrontContributions(contributionCap);
        expectedResponse.setUpliftApplied(Constants.N);
        expectedResponse.setTotalMonths(null);
        expectedResponse.setContributionCap(contributionCap);
        expectedResponse.setBasedOn("Offence Type");
        when(contributionRulesService.getActiveCCOutcome(any())).thenReturn(CrownCourtOutcome.SUCCESSFUL);
        when(contributionRulesService.isContributionRuleApplicable(CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL))
                .thenReturn(false);
        when(maatCourtDataService.getContributionCalcParameters(anyString(), anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .upliftedIncomePercent(BigDecimal.TEN)
                        .minUpliftedMonthlyAmount(BigDecimal.ONE)
                        .disposableIncomePercent(BigDecimal.TEN)
                        .minimumMonthlyAmount(BigDecimal.valueOf(100))
                        .upfrontTotalMonths(1)
                        .totalMonths(2)
                        .build());
        when(calculateContributionRequestMapper.map(any(), any(), any(), any())).thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));
        when(calculateContributionService.calculateContribution(any())).thenReturn(TestModelDataBuilder.getCalculateContributionResponse()
                .withMonthlyContributions(new BigDecimal(12))
                .withBasedOn(Constants.OFFENCE_TYPE)
                .withUpfrontContributions(new BigDecimal(12)));
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any())).thenReturn(expectedResponse);

        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.calcContribs(calculateContributionDTO, contributionResponseDTO, TestModelDataBuilder.LAA_TRANSACTION_ID);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void givenDisposableIncomeAfterCrownHardship_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterCrownHardship(BigDecimal.TEN)
                .build();
        assertThat(maatCalculateContributionService.calculateAnnualDisposableIncome(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, CrownCourtOutcome.SUCCESSFUL, false))
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenTotalAnnualDisposableIncome_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .totalAnnualDisposableIncome(BigDecimal.TEN)
                .build();
        assertThat(maatCalculateContributionService.calculateAnnualDisposableIncome(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, CrownCourtOutcome.SUCCESSFUL, false))
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenNoIncome_whenCalculateAnnualDisposableIncomeIsInvoked_thenZeroIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .build();
        assertThat(maatCalculateContributionService.calculateAnnualDisposableIncome(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, CrownCourtOutcome.SUCCESSFUL, false))
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
        assertThat(maatCalculateContributionService.calculateAnnualDisposableIncome(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, CrownCourtOutcome.SUCCESSFUL, true))
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
        assertThat(maatCalculateContributionService.calculateAnnualDisposableIncome(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, CrownCourtOutcome.SUCCESSFUL, true))
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenContributionSent_whenIsEarlyTransferRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .build();
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpfrontContributions(BigDecimal.ZERO);
        Contribution latestSentContribution = Contribution.builder()
                .monthlyContributions(BigDecimal.ZERO)
                .upfrontContributions(BigDecimal.ZERO)
                .build();
        when(contributionService.hasContributionBeenSent(anyInt(), anyString())).thenReturn(true);
        assertThat(maatCalculateContributionService.isEarlyTransferRequired(calculateContributionDTO, "", response, latestSentContribution)).isTrue();
    }

    @Test
    void givenContributionIsNotSent_whenIsEarlyTransferRequiredIsInvoked_FalseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .build();
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpfrontContributions(BigDecimal.ZERO);
        Contribution latestSentContribution = Contribution.builder()
                .monthlyContributions(BigDecimal.ZERO)
                .upfrontContributions(BigDecimal.ZERO)
                .build();
        when(contributionService.hasContributionBeenSent(anyInt(), anyString())).thenReturn(false);
        assertThat(maatCalculateContributionService.isEarlyTransferRequired(calculateContributionDTO, "", response, latestSentContribution)).isFalse();
    }

    @Test
    void givenCCOutcomeChangedAdMagOutcomeIsSentForTrail_whenIsEarlyTransferRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .magCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL)
                .build();
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpfrontContributions(BigDecimal.ZERO);
        Contribution latestSentContribution = Contribution.builder()
                .monthlyContributions(BigDecimal.ZERO)
                .upfrontContributions(BigDecimal.ZERO)
                .build();
        when(contributionService.hasCCOutcomeChanged(anyInt(), anyString())).thenReturn(true);
        assertThat(maatCalculateContributionService.isEarlyTransferRequired(calculateContributionDTO, "", response, latestSentContribution)).isTrue();
    }

    @Test
    void givenMonthlyContributionsNotEqualAdMagOutcomeIsCommittedForTrail_whenIsEarlyTransferRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .magCourtOutcome(MagCourtOutcome.COMMITTED_FOR_TRIAL)
                .build();
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpfrontContributions(BigDecimal.ZERO);
        Contribution latestSentContribution = Contribution.builder()
                .monthlyContributions(BigDecimal.ONE)
                .upfrontContributions(BigDecimal.ZERO)
                .build();
        assertThat(maatCalculateContributionService.isEarlyTransferRequired(calculateContributionDTO, "", response, latestSentContribution)).isTrue();
    }

    @Test
    void givenUpfrontContributionsNotEqualAdMagOutcomeIsAppealToCC_whenIsEarlyTransferRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .magCourtOutcome(MagCourtOutcome.APPEAL_TO_CC)
                .build();
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpfrontContributions(BigDecimal.ZERO);
        Contribution latestSentContribution = Contribution.builder()
                .monthlyContributions(BigDecimal.ZERO)
                .upfrontContributions(BigDecimal.ONE)
                .build();
        assertThat(maatCalculateContributionService.isEarlyTransferRequired(calculateContributionDTO, "", response, latestSentContribution)).isTrue();
    }

    @Test
    void givenDifferentEffectiveDatesAndMonthlyContributions_whenIsEarlyTransferRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .magCourtOutcome(MagCourtOutcome.APPEAL_TO_CC)
                .build();
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse()
                .withEffectiveDate(LocalDate.now().toString())
                .withMonthlyContributions(BigDecimal.TEN)
                .withUpfrontContributions(BigDecimal.ZERO);
        Contribution latestSentContribution = Contribution.builder()
                .effectiveDate(TestModelDataBuilder.TEST_DATE.toLocalDate())
                .monthlyContributions(BigDecimal.TEN)
                .upfrontContributions(BigDecimal.ZERO)
                .build();
        assertThat(maatCalculateContributionService.isEarlyTransferRequired(calculateContributionDTO, "", response, latestSentContribution)).isTrue();
    }

    @Test
    void givenDifferentEffectiveDatesAndZeroMonthlyContributions_whenIsEarlyTransferRequiredIsInvoked_FalseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .magCourtOutcome(MagCourtOutcome.APPEAL_TO_CC)
                .build();
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse()
                .withEffectiveDate(LocalDate.now().toString())
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpfrontContributions(BigDecimal.ZERO);
        Contribution latestSentContribution = Contribution.builder()
                .effectiveDate(TestModelDataBuilder.TEST_DATE.toLocalDate())
                .monthlyContributions(BigDecimal.ZERO)
                .upfrontContributions(BigDecimal.ZERO)
                .build();
        assertThat(maatCalculateContributionService.isEarlyTransferRequired(calculateContributionDTO, "", response, latestSentContribution)).isFalse();
    }

    @Test
    void givenSameEffectiveDates_whenIsEarlyTransferRequiredIsInvoked_FalseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .magCourtOutcome(MagCourtOutcome.APPEAL_TO_CC)
                .build();
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse()
                .withEffectiveDate(TestModelDataBuilder.TEST_DATE.toLocalDate().toString())
                .withMonthlyContributions(BigDecimal.TEN)
                .withUpfrontContributions(BigDecimal.ZERO);
        Contribution latestSentContribution = Contribution.builder()
                .effectiveDate(TestModelDataBuilder.TEST_DATE.toLocalDate())
                .monthlyContributions(BigDecimal.TEN)
                .upfrontContributions(BigDecimal.ZERO)
                .build();
        assertThat(maatCalculateContributionService.isEarlyTransferRequired(calculateContributionDTO, "", response, latestSentContribution)).isFalse();
    }

    @Test
    void givenTransferRequestedAndAppealCC_whenIsCreateContributionRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.APPEAL_CC)
                .build();
        assertThat(maatCalculateContributionService.isCreateContributionRequired(calculateContributionDTO, false, null, TransferStatus.REQUESTED)).isTrue();
    }

    @Test
    void givenTransferRequestedAndIndictableCase_whenIsCreateContributionRequiredIsInvoked_FalseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .build();
        assertThat(maatCalculateContributionService.isCreateContributionRequired(calculateContributionDTO, false, null, TransferStatus.REQUESTED)).isFalse();
    }

    @Test
    void givenTransferSentAndAppealCC_whenIsCreateContributionRequiredIsInvoked_FalseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.APPEAL_CC)
                .build();
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        when(contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.APPEAL_CC, null)).thenReturn(false);
        when(contributionService.hasCCOutcomeChanged(repOrderDTO.getId(), null)).thenReturn(false);
        when(contributionService.isCds15WorkAround(repOrderDTO)).thenReturn(false);
        assertThat(maatCalculateContributionService.isCreateContributionRequired(calculateContributionDTO, false, repOrderDTO, TransferStatus.SENT)).isFalse();
    }

    @Test
    void givenTransferSentAndApplicationStatusChanged_whenIsCreateContributionRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .build();
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        when(contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, null)).thenReturn(true);
        assertThat(maatCalculateContributionService.isCreateContributionRequired(calculateContributionDTO, false, repOrderDTO, TransferStatus.SENT)).isTrue();
    }

    @Test
    void givenReassessmentAndTransferRequested_whenIsCreateContributionRequiredIsInvoked_TrueIsReturned() {
        assertThat(maatCalculateContributionService.isCreateContributionRequired(null, true, null, TransferStatus.REQUESTED)).isTrue();
    }

    @Test
    void givenReassessmentAndTransferSent_whenIsCreateContributionRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.APPEAL_CC)
                .build();
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        when(contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.APPEAL_CC, null)).thenReturn(false);
        when(contributionService.hasCCOutcomeChanged(repOrderDTO.getId(), null)).thenReturn(false);
        when(contributionService.isCds15WorkAround(repOrderDTO)).thenReturn(false);
        assertThat(maatCalculateContributionService.isCreateContributionRequired(calculateContributionDTO, true, repOrderDTO, TransferStatus.SENT)).isTrue();
    }

    @Test
    void givenTransferSentAndCCOutcomeChanged_whenIsCreateContributionRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .build();
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        when(contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, null)).thenReturn(false);
        when(contributionService.hasCCOutcomeChanged(repOrderDTO.getId(), null)).thenReturn(true);
        assertThat(maatCalculateContributionService.isCreateContributionRequired(calculateContributionDTO, true, repOrderDTO, TransferStatus.SENT)).isTrue();
    }

    @Test
    void givenTransferSentAndCds15Workaround_whenIsCreateContributionRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .build();
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        when(contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, null)).thenReturn(false);
        when(contributionService.hasCCOutcomeChanged(repOrderDTO.getId(), null)).thenReturn(false);
        when(contributionService.isCds15WorkAround(repOrderDTO)).thenReturn(true);
        assertThat(maatCalculateContributionService.isCreateContributionRequired(calculateContributionDTO, true, repOrderDTO, TransferStatus.SENT)).isTrue();
    }

    @Test
    void givenIndictableCase_whenCalculateContributionIsInvoked_validResponseIsReturned() {
        RepOrderDTO repOrderDTO = TestModelDataBuilder.getRepOrderDTO();
        when(maatCourtDataService.getRepOrderByRepId(null, TestModelDataBuilder.LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);
        when(contributionService.checkReassessment(repOrderDTO, TestModelDataBuilder.LAA_TRANSACTION_ID)).thenReturn(true);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.FULL)
                        .withResult(AssessmentResult.PASS)
                        .withAssessmentDate(TestModelDataBuilder.TEST_DATE)))
                .build();
        when(contributionService.checkContribsCondition(any())).thenReturn(ContributionResponseDTO.builder().build());
        ApiMaatCalculateContributionResponse maatCalculateContributionResponse = new ApiMaatCalculateContributionResponse();
        assertThat(maatCalculateContributionService.calculateContribution(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID)).isEqualTo(maatCalculateContributionResponse);
    }

    @Test
    void givenARequestWithDoContribsAsY_whenGetCalculateContributionResponseIsInvoked_thenResponseIsReturned() {
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        when(contributionService.checkReassessment(repOrderDTO, TestModelDataBuilder.LAA_TRANSACTION_ID)).thenReturn(true);
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder().doContribs(Constants.Y).build();
        when(contributionService.checkContribsCondition(any())).thenReturn(contributionResponseDTO);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withResult(AssessmentResult.PASS)
                        .withAssessmentDate(TestModelDataBuilder.TEST_DATE)))
                .effectiveDate(LocalDate.now())
                .monthlyContributions(BigDecimal.TEN)
                .build();
        ApiMaatCalculateContributionResponse expectedResponse = new ApiMaatCalculateContributionResponse()
                .withEffectiveDate(LocalDate.now().toString())
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpfrontContributions(BigDecimal.ZERO)
                .withUpliftApplied(Constants.N)
                .withBasedOn("Means");
        when(maatCourtDataService.getContributionCalcParameters(any(), any())).thenReturn(ContributionCalcParametersDTO.builder()
                .disposableIncomePercent(BigDecimal.TEN)
                .minimumMonthlyAmount(BigDecimal.valueOf(100))
                .upfrontTotalMonths(2)
                .totalMonths(2)
                .build());
        when(maatCourtDataService.findLatestSentContribution(any(), any())).thenReturn(Contribution.builder()
                .monthlyContributions(BigDecimal.TEN)
                .upfrontContributions(BigDecimal.ONE)
                .build());
        when(calculateContributionRequestMapper.map(any(), any(), any(), any())).thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));
        when(calculateContributionService.calculateContribution(any())).thenReturn(TestModelDataBuilder.getCalculateContributionResponse().withBasedOn(Constants.MEANS));
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any())).thenReturn(expectedResponse);

        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.getCalculateContributionResponse(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, repOrderDTO);
        assertThat(actualResponse)
                .isEqualTo(expectedResponse);
    }

    @Test
    void givenARequestWithDoContribsAsNAndMonthlyContributionsZero_whenGetCalculateContributionResponseIsInvoked_thenResponseIsReturned() {
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        when(contributionService.checkReassessment(repOrderDTO, TestModelDataBuilder.LAA_TRANSACTION_ID)).thenReturn(true);
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder().doContribs(Constants.Y).build();
        when(contributionService.checkContribsCondition(any())).thenReturn(contributionResponseDTO);
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getCalculateContributionDTO();
        ApiMaatCalculateContributionResponse expectedResponse = new ApiMaatCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpfrontContributions(BigDecimal.ZERO)
                .withContributionCap(BigDecimal.ZERO);

        when(maatCourtDataService.findLatestSentContribution(any(), any())).thenReturn(Contribution.builder()
                .monthlyContributions(BigDecimal.ZERO)
                .upfrontContributions(BigDecimal.ONE)
                .build());

        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.getCalculateContributionResponse(calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID, repOrderDTO);
        assertThat(actualResponse)
                .isEqualTo(expectedResponse);
    }

    @Test
    void givenAValidCalculateContributionDTO_whenGetContributionSummariesIsInvoked_thenContributionSummaryListIsReturned() {
        List<ContributionsSummaryDTO> contributionSummaryDTO = List.of(TestModelDataBuilder.getContributionSummaryDTO());
        when(maatCourtDataService.getContributionsSummary(any(), any())).thenReturn(contributionSummaryDTO);
        ApiContributionSummary contributionSummary = new ApiContributionSummary();
        when(contributionSummaryMapper.map(any())).thenReturn(contributionSummary);
        List<ApiContributionSummary> response = maatCalculateContributionService.getContributionSummaries(TestModelDataBuilder.REP_ID, TestModelDataBuilder.LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionsSummary(any(), any());
        assertThat(response).isEqualTo(List.of(contributionSummary));

    }

    @Test
    void givenNoContributionSummaries_whenGetContributionSummariesIsInvoked_thenEmptyListIsReturned() {
        when(maatCourtDataService.getContributionsSummary(any(), any())).thenReturn(null);
        List<ApiContributionSummary> response = maatCalculateContributionService.getContributionSummaries(TestModelDataBuilder.REP_ID, TestModelDataBuilder.LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionsSummary(any(), any());
        assertThat(response).isEqualTo(List.of());

    }

    @Test
    void givenATemplateAndContributionsAreCreated_whenDoContribsIsInvoked_thenProcessActivityFlagIsTrue() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getCalculateContributionDTO();

        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .template(1)
                .build();
        when(calculateContributionService.calculateContribution(any())).thenReturn(new ApiCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.TEN));
        ApiMaatCalculateContributionResponse maatCalculateContributionResponse = TestModelDataBuilder.getApiMaatCalculateContributionResponse();
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any())).thenReturn(maatCalculateContributionResponse);
        when(maatCourtDataService.findLatestSentContribution(any(), any())).thenReturn(Contribution.builder()
                .monthlyContributions(BigDecimal.ZERO)
                .upfrontContributions(BigDecimal.ONE)
                .build());
        when(maatCalculateContributionService.verifyAndCreateContribs(
                calculateContributionDTO, "", true, null, maatCalculateContributionResponse, null
        )).thenReturn(new Contribution());
        when(maatCourtDataService.getContributionCalcParameters(anyString(), anyString())).thenReturn(
                ContributionCalcParametersDTO.builder()
                        .totalMonths(2).build());

        ApiMaatCalculateContributionResponse response = maatCalculateContributionService.doContribs(
                calculateContributionDTO,
                "",
                contributionResponseDTO,
                null,
                true,
                null
        );
        assertThat(response.getProcessActivity()).isTrue();
    }

    @Test
    void givenATemplateAndContributionsAreNotCreated_whenDoContribsIsInvoked_thenProcessActivityFlagIsNull() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getCalculateContributionDTO();

        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .template(1)
                .build();
        when(calculateContributionService.calculateContribution(any())).thenReturn(new ApiCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.TEN));
        ApiMaatCalculateContributionResponse maatCalculateContributionResponse = TestModelDataBuilder.getApiMaatCalculateContributionResponse();
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any())).thenReturn(maatCalculateContributionResponse);
        when(maatCourtDataService.findLatestSentContribution(any(), any())).thenReturn(Contribution.builder()
                .monthlyContributions(BigDecimal.ZERO)
                .upfrontContributions(BigDecimal.ONE)
                .build());
        when(maatCalculateContributionService.verifyAndCreateContribs(
                calculateContributionDTO, "", true, null, maatCalculateContributionResponse, null
        )).thenReturn(null);
        when(maatCourtDataService.getContributionCalcParameters(anyString(), anyString())).thenReturn(
                ContributionCalcParametersDTO.builder()
                        .totalMonths(2).build());

        ApiMaatCalculateContributionResponse response = maatCalculateContributionService.doContribs(
                calculateContributionDTO,
                "",
                contributionResponseDTO,
                null,
                true,
                null
        );
        assertThat(response.getProcessActivity()).isNull();
    }

    @Test
    void givenValidContributionId_whenGetCurrentContributionIsInvoked_validResponseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .contributionId(TestModelDataBuilder.CONTRIBUTION_ID)
                .build();
        when(maatCourtDataService.findContribution(any(), any(), any())).thenReturn(List.of(Contribution.builder()
                .id(TestModelDataBuilder.CONTRIBUTION_ID).build()));
        Contribution contribution = maatCalculateContributionService.getCurrentContribution(
                calculateContributionDTO, TestModelDataBuilder.LAA_TRANSACTION_ID);
        assertThat(contribution.getId()).isEqualTo(TestModelDataBuilder.CONTRIBUTION_ID);
    }

    @Test
    void givenEarlyTransferRequired_whenRequestEarlyTransferIsInvoked_thenUpdateContributionIsCalled() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .magCourtOutcome(MagCourtOutcome.APPEAL_TO_CC)
                .build();
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse()
                .withEffectiveDate(LocalDate.now().toString())
                .withMonthlyContributions(BigDecimal.TEN)
                .withUpfrontContributions(BigDecimal.ZERO);
        Contribution latestSentContribution = Contribution.builder()
                .effectiveDate(TestModelDataBuilder.TEST_DATE.toLocalDate())
                .monthlyContributions(BigDecimal.TEN)
                .upfrontContributions(BigDecimal.ZERO)
                .build();
        when(maatCourtDataService.findLatestSentContribution(any(), any())).thenReturn(latestSentContribution);
        maatCalculateContributionService.requestEarlyTransfer(calculateContributionDTO, "", response, new Contribution());
        verify(maatCourtDataService, times(1)).updateContribution(any(), anyString());
    }
}


