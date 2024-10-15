package uk.gov.justice.laa.crime.contribution.service;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.common.model.contribution.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.builder.CalculateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.ContributionSummaryMapper;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.MaatCalculateContributionResponseMapper;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.ContributionResult;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.NewWorkReason;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder.COMMITTAL_DATE;
import static uk.gov.justice.laa.crime.enums.contribution.AssessmentType.FULL;
import static uk.gov.justice.laa.crime.enums.contribution.AssessmentType.HARDSHIP;
import static uk.gov.justice.laa.crime.enums.contribution.AssessmentType.INIT;
import static uk.gov.justice.laa.crime.enums.contribution.AssessmentType.PASSPORT;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class MaatCalculateContributionServiceTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private AppealContributionService appealContributionService;

    @InjectMocks
    private MaatCalculateContributionService maatCalculateContributionService;

    @Mock
    private CompareContributionService compareContributionService;

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
    private ContributionSummaryMapper contributionSummaryMapper;

    @Mock
    private CreateContributionRequestMapper contributionRequestMapper;
    @Mock
    private MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper;

    @Test
    void givenAValidCaseType_whenCalculateContributionIsInvoked_thenShouldNotCalledCalculateContribution() {
        when(maatCourtDataService.getRepOrderByRepId(anyInt()))
                .thenReturn(TestModelDataBuilder.getRepOrderDTO());
        when(appealContributionService.calculateAppealContribution(any(CalculateContributionDTO.class)))
                .thenReturn(new ApiMaatCalculateContributionResponse());
        maatCalculateContributionService.calculateContribution(
                CalculateContributionDTO.builder()
                        .repId(120)
                        .caseType(CaseType.APPEAL_CC)
                        .build()
        );
        verify(appealContributionService, times(1)).calculateAppealContribution(any());
    }

    @Test
    void givenValidContributionAndShouldCreateIsTrue_whenCreateContributionsIsInvoked_thenContributionIsReturn() {
        when(compareContributionService.shouldCreateContribution(
                any(CalculateContributionDTO.class), any(ContributionResult.class))
        ).thenReturn(true);
        when(maatCourtDataService.createContribution(any()))
                .thenReturn(TestModelDataBuilder.getContribution());

        Contribution result = maatCalculateContributionService.createContributions(new CalculateContributionDTO(),
                ContributionResult.builder().build()
        );

        verify(maatCourtDataService, times(1))
                .createContribution(any(CreateContributionRequest.class));
        assertThat(result).isNotNull();
    }

    @Test
    void givenValidContributionAndShouldCreateIsFalse_whenCreateContributionsIsInvoked_thenNullIsReturn() {
        when(compareContributionService.shouldCreateContribution(
                any(CalculateContributionDTO.class), any(ContributionResult.class))
        ).thenReturn(false);

        Contribution result = maatCalculateContributionService.createContributions(
                new CalculateContributionDTO(), ContributionResult.builder().build()
        );

        verifyNoInteractions(maatCourtDataService);
        assertThat(result).isNull();
    }

    @Test
    void givenNoCommittalDateAndPassportAssessmentDate_whenGetEffectiveDateIsInvoked_thenPassportAssessmentDateIsReturned() {
        LocalDateTime passportAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(PASSPORT)
                        .withAssessmentDate(passportAssessmentDate)))
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);

        softly.assertThat(effectiveDate.getMonth())
                .isEqualTo(passportAssessmentDate.getMonth());
        softly.assertThat(effectiveDate.getYear())
                .isEqualTo(passportAssessmentDate.getYear());
        softly.assertThat(effectiveDate.getDayOfMonth())
                .isEqualTo(passportAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenNoCommittalDateAndFullMeansAssessmentDate_whenGetEffectiveDateIsInvoked_thenFullMeansAssessmentDateIsReturned() {
        LocalDateTime fullMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(FULL)
                        .withAssessmentDate(fullMeansAssessmentDate)))
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);

        softly.assertThat(effectiveDate.getMonth())
                .isEqualTo(fullMeansAssessmentDate.getMonth());
        softly.assertThat(effectiveDate.getYear())
                .isEqualTo(fullMeansAssessmentDate.getYear());
        softly.assertThat(effectiveDate.getDayOfMonth())
                .isEqualTo(fullMeansAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenNoCommittalDateAndInitMeansAssessmentDate_whenGetEffectiveDateIsInvoked_thenInitMeansAssessmentDateIsReturned() {
        LocalDateTime initMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(INIT)
                        .withAssessmentDate(initMeansAssessmentDate)))
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);

        softly.assertThat(effectiveDate.getMonth())
                .isEqualTo(initMeansAssessmentDate.getMonth());
        softly.assertThat(effectiveDate.getYear())
                .isEqualTo(initMeansAssessmentDate.getYear());
        softly.assertThat(effectiveDate.getDayOfMonth())
                .isEqualTo(initMeansAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenCommittalDateAfterAssessmentDate_whenGetEffectiveDateIsInvoked_thenCommittalDateIsReturned() {
        LocalDateTime initMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        LocalDate committalDate = LocalDate.of(2023, 9, 28);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(INIT)
                        .withAssessmentDate(initMeansAssessmentDate)))
                .committalDate(committalDate)
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);

        assertThat(effectiveDate)
                .isEqualTo(committalDate);
    }

    @Test
    void givenCommittalDateBeforeAssessmentDate_whenGetEffectiveDateIsInvoked_thenAssessmentDateIsReturned() {
        LocalDateTime initMeansAssessmentDate = LocalDateTime.of(2023, 8, 28, 12, 12, 12);
        LocalDate committalDate = LocalDate.of(2023, 7, 28);
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(INIT)
                        .withAssessmentDate(initMeansAssessmentDate)))
                .committalDate(committalDate)
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);

        softly.assertThat(effectiveDate.getMonth())
                .isEqualTo(initMeansAssessmentDate.getMonth());
        softly.assertThat(effectiveDate.getYear())
                .isEqualTo(initMeansAssessmentDate.getYear());
        softly.assertThat(effectiveDate.getDayOfMonth())
                .isEqualTo(initMeansAssessmentDate.getDayOfMonth());
    }

    @Test
    void givenNoValidAssessments_whenGetEffectiveDateIsInvoked_thenNullIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(HARDSHIP)
                        .withAssessmentDate(TestModelDataBuilder.TEST_DATE)))
                .build();
        LocalDate effectiveDate = MaatCalculateContributionService.getEffectiveDate(calculateContributionDTO);

        assertThat(effectiveDate)
                .isNull();
    }

    @Test
    void givenPassportNewWorkReason_whenGetNewWorkReasonIsInvoked_thenPassportNewWorkReasonIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(PASSPORT)
                        .withNewWorkReason(NewWorkReason.INF)))
                .build();

        assertThat(MaatCalculateContributionService.getNewWorkReason(calculateContributionDTO))
                .isEqualTo(NewWorkReason.INF);
    }

    @Test
    void givenInitialNewWorkReason_whenGetNewWorkReasonIsInvoked_thenInitialNewWorkReasonIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(INIT)
                        .withNewWorkReason(NewWorkReason.NEW)))
                .build();

        assertThat(MaatCalculateContributionService.getNewWorkReason(calculateContributionDTO))
                .isEqualTo(NewWorkReason.NEW);
    }

    @Test
    void givenFullNewWorkReason_whenGetNewWorkReasonIsInvoked_thenNullIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(FULL)
                        .withNewWorkReason(NewWorkReason.NEW)))
                .build();

        assertThat(MaatCalculateContributionService.getNewWorkReason(calculateContributionDTO))
                .isNull();
    }

    @Test
    void givenContributionDateNotNull_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionDateIsReturned() {
        LocalDate contributionDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(PASSPORT)
                        .withNewWorkReason(NewWorkReason.INF)))
                .effectiveDate(contributionDate)
                .build();
        String effectiveDate = MaatCalculateContributionService.getEffectiveDateByNewWorkReason(
                calculateContributionDTO, null, null
        );

        assertThat(effectiveDate)
                .isEqualTo(contributionDate.toString());
    }

    @Test
    void givenNewWorkReasonAsINF_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenAssessmentDateIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(PASSPORT)
                        .withNewWorkReason(NewWorkReason.INF)))
                .build();
        LocalDate assessmentDate = LocalDate.now();
        String effectiveDate = MaatCalculateContributionService.getEffectiveDateByNewWorkReason(
                calculateContributionDTO, null, assessmentDate
        );

        assertThat(effectiveDate)
                .isEqualTo(assessmentDate.toString());
    }

    @Test
    void givenNewWorkReasonAsFMA_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenAssessmentDateIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(PASSPORT)
                        .withNewWorkReason(NewWorkReason.FMA)))
                .build();
        LocalDate assessmentDate = LocalDate.now();
        String effectiveDate = MaatCalculateContributionService.getEffectiveDateByNewWorkReason(
                calculateContributionDTO, null, assessmentDate
        );

        assertThat(effectiveDate)
                .isEqualTo(assessmentDate.toString());
    }

    @Test
    void givenNewWorkReasonAsPAIAndMonthlyContributionsAreSmaller_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionEffectiveDateIsReturned() {
        LocalDate effectiveDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(PASSPORT)
                        .withNewWorkReason(NewWorkReason.PAI)))
                .effectiveDate(effectiveDate)
                .monthlyContributions(BigDecimal.TEN)
                .build();

        String actual = MaatCalculateContributionService.getEffectiveDateByNewWorkReason(
                calculateContributionDTO, BigDecimal.valueOf(100), null
        );

        assertThat(actual)
                .isEqualTo(effectiveDate.toString());
    }

    @Test
    void givenNewWorkReasonAsPAIAndMonthlyContributionsAreEqual_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionEffectiveDateIsReturned() {
        LocalDate effectiveDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(PASSPORT)
                        .withNewWorkReason(NewWorkReason.PAI)))
                .effectiveDate(effectiveDate)
                .monthlyContributions(BigDecimal.TEN)
                .build();

        String actual = MaatCalculateContributionService.getEffectiveDateByNewWorkReason(
                calculateContributionDTO, BigDecimal.TEN, null
        );

        assertThat(actual)
                .isEqualTo(effectiveDate.toString());
    }

    @Test
    void givenNewWorkReasonAsPAIAndMonthlyContributionsAreGreater_whenGetEffectiveDateByNewWorkReasonIsInvoked_thenContributionEffectiveDateIsReturned() {
        LocalDate assessmentDate = LocalDate.now();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(PASSPORT)
                        .withNewWorkReason(NewWorkReason.PAI)))
                .monthlyContributions(BigDecimal.valueOf(12))
                .build();

        String actual = MaatCalculateContributionService.getEffectiveDateByNewWorkReason(
                calculateContributionDTO, BigDecimal.TEN, assessmentDate
        );

        assertThat(actual)
                .isEqualTo(assessmentDate.toString());
    }

    @Test
    void givenAValidAnnualIncomeAfterMagHardship_whenGetAnnualDisposableIncomeIsInvoked_thenAnnualIncomeAfterMagHardshipIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterMagHardship(annualDisposableIncome)
                .build();

        BigDecimal actual = MaatCalculateContributionService.getAnnualDisposableIncome(calculateContributionDTO);

        assertThat(actual).isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenAValidTotalAnnualIncome_whenGetAnnualDisposableIncomeIsInvoked_thenTotalAnnualIncomeIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .totalAnnualDisposableIncome(annualDisposableIncome)
                .build();

        BigDecimal actual = MaatCalculateContributionService.getAnnualDisposableIncome(calculateContributionDTO);

        assertThat(actual).isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenNoIncome_whenGetAnnualDisposableIncomeIsInvoked_thenZeroIsReturned() {
        BigDecimal actual =
                MaatCalculateContributionService.getAnnualDisposableIncome(CalculateContributionDTO.builder().build());

        assertThat(actual).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenValidVariationAmount_whenCalculateVariationAmountIsInvoked_thenVariationAmountIsReturned() {
        ApiCalculateHardshipByDetailResponse apiCalculateHardshipByDetailResponse =
                new ApiCalculateHardshipByDetailResponse()
                        .withHardshipSummary(BigDecimal.TEN);
        when(crimeHardshipService.calculateHardshipForDetail(any(ApiCalculateHardshipByDetailRequest.class)))
                .thenReturn(apiCalculateHardshipByDetailResponse);
        BigDecimal variationAmount = maatCalculateContributionService.calculateVariationAmount(
                TestModelDataBuilder.REP_ID);

        assertThat(variationAmount).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenCalcContributionsAsN_whenCalcContributionsIsInvoked_validResponseIsReturned() {

        ContributionResult expected = ContributionResult.builder()
                .totalAnnualDisposableIncome(BigDecimal.ZERO)
                .monthlyAmount(BigDecimal.ZERO)
                .isUplift(false)
                .effectiveDate(COMMITTAL_DATE)
                .upfrontAmount(BigDecimal.ZERO)
                .totalMonths(0)
                .build();


        CalculateContributionDTO calculateContributionDTO = setupDataForCalculateContributionsTests();
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.N)
                .build();

        when(calculateContributionRequestMapper.map(any(), any(), any(), any()))
                .thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));

        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(TestModelDataBuilder.getCalculateContributionResponse());

        ContributionResult result = maatCalculateContributionService.calculateContributions(
                calculateContributionDTO, contributionResponseDTO
        );

        assertThat(result)
                .isEqualTo(expected);
    }

    private CalculateContributionDTO setupDataForCalculateContributionsTests() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getContributionDTOForCalcContribs();
        when(maatCourtDataService.getContributionCalcParameters(anyString()))
                .thenReturn(new ContributionCalcParametersDTO());
        when(contributionRulesService.getActiveCCOutcome(any()))
                .thenReturn(CrownCourtOutcome.SUCCESSFUL);
        return calculateContributionDTO;
    }

    @Test
    void givenUpliftCoteNotNullAndCalcContributionsAsN_whenCalcContributionsIsInvoked_validResponseIsReturned() {

        ContributionResult expectedResult =
                ContributionResult.builder()
                        .totalAnnualDisposableIncome(BigDecimal.ZERO)
                        .monthlyAmount(BigDecimal.ZERO)
                        .isUplift(false)
                        .effectiveDate(COMMITTAL_DATE)
                        .upfrontAmount(BigDecimal.ZERO)
                        .totalMonths(0)
                        .build();

        ContributionResponseDTO contributionResponseDTO =
                ContributionResponseDTO.builder()
                        .calcContribs(Constants.N)
                        .upliftCote(1)
                        .build();

        CalculateContributionDTO calculateContributionDTO = setupDataForCalculateContributionsTests();

        when(calculateContributionRequestMapper.map(any(), any(), any(), any()))
                .thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));

        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(TestModelDataBuilder.getCalculateContributionResponse());

        ContributionResult result = maatCalculateContributionService.calculateContributions(
                calculateContributionDTO, contributionResponseDTO
        );

        assertThat(result)
                .isEqualTo(expectedResult);
    }

    @Test
    void givenUpliftCoteNotNullAndUpliftAppliedIsPresent_whenCalcContributionsIsInvoked_validResponseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getContributionDTOForCalcContribs();
        calculateContributionDTO.setDateUpliftApplied(LocalDate.of(2023, 9, 9));
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.N)
                .upliftCote(1)
                .build();

        ApiMaatCalculateContributionResponse expectedResponse =
                TestModelDataBuilder.getApiMaatCalculateContributionResponse();

        expectedResponse.setMonthlyContributions(BigDecimal.ONE);
        expectedResponse.setUpfrontContributions(BigDecimal.ZERO);
        expectedResponse.setUpliftApplied(Constants.Y);

        ContributionResult expectedResult =
                ContributionResult.builder()
                        .totalAnnualDisposableIncome(BigDecimal.ZERO)
                        .monthlyAmount(BigDecimal.ONE)
                        .isUplift(true)
                        .effectiveDate(COMMITTAL_DATE)
                        .upfrontAmount(BigDecimal.ZERO)
                        .totalMonths(0)
                        .build();

        when(contributionRulesService.getActiveCCOutcome(any()))
                .thenReturn(CrownCourtOutcome.SUCCESSFUL);

        when(maatCourtDataService.getContributionCalcParameters(anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .upliftedIncomePercent(BigDecimal.TEN)
                        .minUpliftedMonthlyAmount(BigDecimal.ONE)
                        .totalMonths(2)
                        .build()
                );
        when(calculateContributionRequestMapper.map(any(), any(), any(), any()))
                .thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));

        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(TestModelDataBuilder.getCalculateContributionResponse()
                        .withMonthlyContributions(BigDecimal.ONE)
                        .withUpliftApplied(Constants.Y)
                );

        ContributionResult result = maatCalculateContributionService.calculateContributions(
                calculateContributionDTO, contributionResponseDTO
        );

        assertThat(result)
                .isEqualTo(expectedResult);
    }

    @Test
    void givenUpliftCoteNotNullAndUpliftIsRemoved_whenCalcContributionsIsInvoked_validResponseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getContributionDTOForCalcContribs();
        calculateContributionDTO.setDateUpliftApplied(LocalDate.of(2023, 1, 9));
        calculateContributionDTO.setDateUpliftRemoved(LocalDate.of(2023, 2, 9));
        calculateContributionDTO.setContributionCap(BigDecimal.TEN);

        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.Y)
                .upliftCote(1)
                .build();

        ApiMaatCalculateContributionResponse expectedResponse =
                TestModelDataBuilder.getApiMaatCalculateContributionResponse();

        expectedResponse.setMonthlyContributions(BigDecimal.ZERO);
        expectedResponse.setUpfrontContributions(BigDecimal.ZERO);
        expectedResponse.setUpliftApplied(Constants.N);
        expectedResponse.setTotalMonths(null);
        expectedResponse.setContributionCap(BigDecimal.TEN);
        expectedResponse.setBasedOn("Means");

        ContributionResult expectedResult =
                ContributionResult.builder()
                        .totalAnnualDisposableIncome(BigDecimal.ZERO)
                        .monthlyAmount(BigDecimal.ZERO)
                        .isUplift(false)
                        .effectiveDate(COMMITTAL_DATE)
                        .upfrontAmount(BigDecimal.ZERO)
                        .contributionCap(BigDecimal.TEN)
                        .basedOn("Means")
                        .totalMonths(2)
                        .build();

        when(contributionRulesService.getActiveCCOutcome(any()))
                .thenReturn(CrownCourtOutcome.SUCCESSFUL);

        when(maatCourtDataService.getContributionCalcParameters(anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .upliftedIncomePercent(BigDecimal.TEN)
                        .minUpliftedMonthlyAmount(BigDecimal.ONE)
                        .disposableIncomePercent(BigDecimal.TEN)
                        .minimumMonthlyAmount(BigDecimal.valueOf(100))
                        .upfrontTotalMonths(1)
                        .totalMonths(2)
                        .build());

        when(calculateContributionRequestMapper.map(any(), any(), any(), any()))
                .thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));

        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(TestModelDataBuilder.getCalculateContributionResponse().withBasedOn(Constants.MEANS));

        ContributionResult result = maatCalculateContributionService.calculateContributions(
                calculateContributionDTO, contributionResponseDTO
        );

        assertThat(result)
                .isEqualTo(expectedResult);

    }

    @Test
    void givenUpliftIsRemovedAndAnnualDisposableIncome_whenCalcContributionsIsInvoked_validResponseIsReturned() {
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

        ApiMaatCalculateContributionResponse expectedResponse =
                TestModelDataBuilder.getApiMaatCalculateContributionResponse();

        expectedResponse.setMonthlyContributions(contributionCap);
        expectedResponse.setUpfrontContributions(contributionCap);
        expectedResponse.setUpliftApplied(Constants.N);
        expectedResponse.setTotalMonths(2);
        expectedResponse.setContributionCap(contributionCap);
        expectedResponse.setBasedOn("Offence Type");

        ContributionResult expectedResult =
                ContributionResult.builder()
                        .totalAnnualDisposableIncome(BigDecimal.valueOf(20000))
                        .monthlyAmount(contributionCap)
                        .isUplift(false)
                        .effectiveDate(COMMITTAL_DATE)
                        .upfrontAmount(contributionCap)
                        .contributionCap(contributionCap)
                        .basedOn("Offence Type")
                        .totalMonths(2)
                        .build();

        when(contributionRulesService.getActiveCCOutcome(any()))
                .thenReturn(CrownCourtOutcome.SUCCESSFUL);

        when(maatCourtDataService.getContributionCalcParameters(anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .upliftedIncomePercent(BigDecimal.TEN)
                        .minUpliftedMonthlyAmount(BigDecimal.ONE)
                        .disposableIncomePercent(BigDecimal.TEN)
                        .minimumMonthlyAmount(BigDecimal.valueOf(100))
                        .upfrontTotalMonths(1)
                        .totalMonths(2)
                        .build());

        when(calculateContributionRequestMapper.map(any(), any(), any(), any()))
                .thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));

        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(TestModelDataBuilder.getCalculateContributionResponse()
                        .withMonthlyContributions(new BigDecimal(12))
                        .withBasedOn(Constants.OFFENCE_TYPE)
                        .withUpfrontContributions(new BigDecimal(12))
                );

        ContributionResult result = maatCalculateContributionService.calculateContributions(
                calculateContributionDTO, contributionResponseDTO
        );

        assertThat(result)
                .isEqualTo(expectedResult);
    }

    @Test
    void givenDisposableIncomeAfterCrownHardship_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterCrownHardship(BigDecimal.TEN)
                .build();

        BigDecimal result = maatCalculateContributionService.calculateAnnualDisposableIncome(
                calculateContributionDTO,
                CrownCourtOutcome.SUCCESSFUL
        );

        assertThat(result)
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenTotalAnnualDisposableIncome_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .totalAnnualDisposableIncome(BigDecimal.TEN)
                .build();

        BigDecimal result = maatCalculateContributionService.calculateAnnualDisposableIncome(
                calculateContributionDTO,
                CrownCourtOutcome.SUCCESSFUL
        );

        assertThat(result)
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenNoIncome_whenCalculateAnnualDisposableIncomeIsInvoked_thenZeroIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .build();

        BigDecimal result = maatCalculateContributionService.calculateAnnualDisposableIncome(
                calculateContributionDTO,
                CrownCourtOutcome.SUCCESSFUL
        );

        assertThat(result)
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenDisposableIncomeAfterCrownHardshipAndNoVariation_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterCrownHardship(BigDecimal.TEN)
                .caseType(CaseType.INDICTABLE)
                .build();

        BigDecimal result = maatCalculateContributionService.calculateAnnualDisposableIncome(
                calculateContributionDTO,
                CrownCourtOutcome.SUCCESSFUL
        );

        assertThat(result).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenTotalAnnualDisposableIncomeAndNoVariation_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .totalAnnualDisposableIncome(BigDecimal.TEN)
                .caseType(CaseType.INDICTABLE)
                .build();

        BigDecimal result = maatCalculateContributionService.calculateAnnualDisposableIncome(
                calculateContributionDTO,
                CrownCourtOutcome.SUCCESSFUL
        );

        assertThat(result).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenNoCalculatedDisposableIncomeAndNoVariation_whenCalculateAnnualDisposableIncomeIsInvoked_thenZeroIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .build();

        BigDecimal result = maatCalculateContributionService.calculateAnnualDisposableIncome(
                calculateContributionDTO,
                CrownCourtOutcome.SUCCESSFUL
        );

        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenDisposableIncomeAfterMagsHardshipAndValidVariation_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterMagHardship(BigDecimal.TEN)
                .magCourtOutcome(MagCourtOutcome.COMMITTED)
                .caseType(CaseType.EITHER_WAY)
                .build();

        when(crimeHardshipService.calculateHardshipForDetail(any(ApiCalculateHardshipByDetailRequest.class)))
                .thenReturn(new ApiCalculateHardshipByDetailResponse().withHardshipSummary(BigDecimal.ONE));
        when(contributionRulesService.isContributionRuleApplicable(CaseType.EITHER_WAY, MagCourtOutcome.COMMITTED, null))
                .thenReturn(true);

        BigDecimal result = maatCalculateContributionService.calculateAnnualDisposableIncome(
                calculateContributionDTO,
                null);

        assertThat(result).isEqualTo(BigDecimal.valueOf(11));
    }

    @Test
    void givenTransferRequestedAndIndictableCase_whenIsCreateContributionRequiredIsInvoked_FalseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .build();

        boolean result = maatCalculateContributionService.isCreateContributionRequired(
                calculateContributionDTO, TestModelDataBuilder.getRepOrderDTO()
        );

        assertThat(result).isFalse();
    }

    @Test
    void givenTransferSentAndAppealCC_whenIsCreateContributionRequiredIsInvoked_FalseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.APPEAL_CC)
                .build();
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();

        when(contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.APPEAL_CC, null))
                .thenReturn(false);
        when(contributionService.hasCCOutcomeChanged(repOrderDTO.getId()))
                .thenReturn(false);
        when(contributionService.isCds15WorkAround(repOrderDTO))
                .thenReturn(false);

        boolean result = maatCalculateContributionService.isCreateContributionRequired(
                calculateContributionDTO, repOrderDTO
        );

        assertThat(result).isFalse();
    }

    @Test
    void givenTransferSentAndApplicationStatusChanged_whenIsCreateContributionRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .build();
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        when(contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, null))
                .thenReturn(true);

        boolean result = maatCalculateContributionService.isCreateContributionRequired(
                calculateContributionDTO, repOrderDTO
        );

        assertThat(result).isTrue();
    }

    @Test
    void givenTransferSentAndCCOutcomeChanged_whenIsCreateContributionRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .build();
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();

        when(contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, null))
                .thenReturn(false);
        when(contributionService.hasCCOutcomeChanged(repOrderDTO.getId()))
                .thenReturn(true);

        boolean result = maatCalculateContributionService.isCreateContributionRequired(
                calculateContributionDTO, repOrderDTO
        );

        assertThat(result).isTrue();
    }

    @Test
    void givenTransferSentAndCds15Workaround_whenIsCreateContributionRequiredIsInvoked_TrueIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .build();
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();

        when(contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, null))
                .thenReturn(false);
        when(contributionService.hasCCOutcomeChanged(repOrderDTO.getId()))
                .thenReturn(false);
        when(contributionService.isCds15WorkAround(repOrderDTO))
                .thenReturn(true);

        boolean result = maatCalculateContributionService.isCreateContributionRequired(
                calculateContributionDTO, repOrderDTO
        );

        assertThat(result).isTrue();
    }

    @Test
    void givenIndictableCase_whenCalculateContributionIsInvoked_validResponseIsReturned() {
        RepOrderDTO repOrderDTO = TestModelDataBuilder.getRepOrderDTO();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(FULL)
                        .withResult(AssessmentResult.PASS)
                        .withAssessmentDate(TestModelDataBuilder.TEST_DATE)))
                .build();

        when(maatCourtDataService.getRepOrderByRepId(null))
                .thenReturn(repOrderDTO);
        when(contributionService.checkContributionsCondition(any()))
                .thenReturn(ContributionResponseDTO.builder().build());

        ApiMaatCalculateContributionResponse result = maatCalculateContributionService.calculateContribution(
                calculateContributionDTO);

        ApiMaatCalculateContributionResponse maatCalculateContributionResponse =
                new ApiMaatCalculateContributionResponse();

        assertThat(result)
                .isEqualTo(maatCalculateContributionResponse);
    }

    @Test
    void givenARequestWithPerformContributionsAsY_whenGetCalculateContributionResponseIsInvoked_thenResponseIsReturned() {
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .doContribs(Constants.Y)
                .build();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(INIT)
                        .withResult(AssessmentResult.PASS)
                        .withAssessmentDate(TestModelDataBuilder.TEST_DATE)))
                .effectiveDate(LocalDate.now())
                .monthlyContributions(BigDecimal.TEN)
                .build();

        when(contributionService.checkContributionsCondition(any()))
                .thenReturn(contributionResponseDTO);
        when(maatCourtDataService.getContributionCalcParameters(any()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .disposableIncomePercent(BigDecimal.TEN)
                        .minimumMonthlyAmount(BigDecimal.valueOf(100))
                        .upfrontTotalMonths(2)
                        .totalMonths(2)
                        .build()
                );
        when(calculateContributionRequestMapper.map(any(), any(), any(), any()))
                .thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));

        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(TestModelDataBuilder.getCalculateContributionResponse()
                        .withBasedOn(Constants.MEANS)
                );

        maatCalculateContributionService.getCalculateContributionResponse(calculateContributionDTO, repOrderDTO);

        ContributionResult contributionResult = ContributionResult.builder()
                .totalAnnualDisposableIncome(BigDecimal.ZERO)
                .monthlyAmount(BigDecimal.ZERO)
                .upfrontAmount(BigDecimal.ZERO)
                .totalMonths(2)
                .isUplift(false)
                .basedOn("Means")
                .effectiveDate(LocalDate.now())
                .build();

        verify(maatCalculateContributionResponseMapper, times(1))
                .map(contributionResult, null, contributionResponseDTO);
    }

    @Test
    void givenARequestWithPerformContributionsAsNAndMonthlyContributionsZero_whenGetCalculateContributionResponseIsInvoked_thenResponseIsReturned() {
        RepOrderDTO repOrderDTO =
                RepOrderDTO.builder()
                        .id(1234)
                        .build();
        ContributionResponseDTO contributionResponseDTO =
                ContributionResponseDTO.builder()
                        .doContribs(Constants.Y)
                        .build();
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getCalculateContributionDTO();

        when(contributionService.checkContributionsCondition(any()))
                .thenReturn(contributionResponseDTO);

        maatCalculateContributionService.getCalculateContributionResponse(calculateContributionDTO, repOrderDTO);

        ContributionResult contributionResult = ContributionResult.builder()
                .monthlyAmount(BigDecimal.ZERO)
                .contributionCap(BigDecimal.ZERO)
                .upfrontAmount(BigDecimal.ZERO)
                .build();
        verify(maatCalculateContributionResponseMapper, times(1))
                .map(contributionResult, null, contributionResponseDTO);
    }

    @Test
    void givenAValidCalculateContributionDTO_whenGetContributionSummariesIsInvoked_thenContributionSummaryListIsReturned() {
        List<ContributionsSummaryDTO> contributionSummaryDTO =
                List.of(TestModelDataBuilder.getContributionSummaryDTO());
        ApiContributionSummary contributionSummary = new ApiContributionSummary();

        when(contributionSummaryMapper.map(any()))
                .thenReturn(contributionSummary);
        when(maatCourtDataService.getContributionsSummary(any()))
                .thenReturn(contributionSummaryDTO);

        List<ApiContributionSummary> response =
                maatCalculateContributionService.getContributionSummaries(
                        TestModelDataBuilder.REP_ID
                );

        assertThat(response)
                .isEqualTo(List.of(contributionSummary));

    }

    @Test
    void givenNoContributionSummaries_whenGetContributionSummariesIsInvoked_thenEmptyListIsReturned() {
        when(maatCourtDataService.getContributionsSummary(any()))
                .thenReturn(null);

        List<ApiContributionSummary> response =
                maatCalculateContributionService.getContributionSummaries(
                        TestModelDataBuilder.REP_ID
                );

        assertThat(response)
                .isEqualTo(List.of());

    }

    @Test
    void givenATemplateAndContributionsAreCreated_whenPerformContributionsIsInvoked_thenProcessActivityFlagIsTrue() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getCalculateContributionDTO();
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .id(1)
                .build();

        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(new ApiCalculateContributionResponse()
                        .withMonthlyContributions(BigDecimal.TEN)
                );

        when(compareContributionService.shouldCreateContribution(
                any(CalculateContributionDTO.class), any(ContributionResult.class))
        ).thenReturn(false);

        when(maatCourtDataService.getContributionCalcParameters(anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .totalMonths(2)
                        .build()
                );
        when(maatCalculateContributionResponseMapper.map(any(), any(), any()))
                .thenReturn(new ApiMaatCalculateContributionResponse().withProcessActivity(true));

        ApiMaatCalculateContributionResponse response =
                maatCalculateContributionService.performContributions(
                        calculateContributionDTO,
                        contributionResponseDTO,
                        null,
                        null
                );

        assertThat(response.getProcessActivity()).isTrue();
    }

    @Test
    void givenValidContributionId_whenGetCurrentContributionIsInvoked_validResponseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .contributionId(TestModelDataBuilder.CONTRIBUTION_ID)
                .build();
        when(maatCourtDataService.findContribution(any(), any()))
                .thenReturn(List.of(
                                Contribution.builder()
                                        .id(TestModelDataBuilder.CONTRIBUTION_ID)
                                        .build()
                        )
                );
        Contribution contribution = maatCalculateContributionService.getCurrentContribution(
                calculateContributionDTO);

        assertThat(contribution.getId())
                .isEqualTo(TestModelDataBuilder.CONTRIBUTION_ID);
    }

    @Test
    void givenChangeInMonthlyContributions_whenVerifyAndCreateContributionsIsInvoked_thenNewContributionIsCreated() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getCalculateContributionDTO();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.getRepOrderDTO();
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();
        Contribution contribution = TestModelDataBuilder.getContribution();

        when(compareContributionService.shouldCreateContribution(any(CalculateContributionDTO.class), any(ContributionResult.class)))
                .thenReturn(true);
        when(contributionRequestMapper.map(any(CalculateContributionDTO.class), any(ContributionResult.class)))
                .thenReturn(new CreateContributionRequest());
        when(maatCourtDataService.createContribution(any(CreateContributionRequest.class)))
                .thenReturn(contribution);

        Contribution contributionResponse = maatCalculateContributionService.verifyAndCreateContributions(
                calculateContributionDTO,
                repOrderDTO,
                contributionResult
        );

        assertThat(contributionResponse).isEqualTo(contribution);
        verify(maatCourtDataService).createContribution(any(CreateContributionRequest.class));
    }

    @Test
    void givenNoChangeInMonthlyContributionsOrEffectiveDate_whenVerifyAndCreateContributionsIsInvoked_thenNoContributionIsCreated() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getCalculateContributionDTO();
        calculateContributionDTO.setMonthlyContributions(BigDecimal.valueOf(250.00));
        ContributionResult contributionResult = TestModelDataBuilder.getContributionResult();

        Contribution contributionResponse = maatCalculateContributionService.verifyAndCreateContributions(
                calculateContributionDTO,
                null,
                contributionResult
        );

        assertThat(contributionResponse).isNull();
        verifyNoInteractions(maatCourtDataService);
    }

    @Test
    void givenEmptyCrownCourtOutcomeList_whenGetCrownCourtOutcomeIsInvoked_NullReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .crownCourtOutcomeList(Collections.emptyList())
                .build();
        assertThat(MaatCalculateContributionService.getCrownCourtOutcome(calculateContributionDTO)).isNull();
    }

    @Test
    void givenNullApiCrownCourtOutcome_whenGetCrownCourtOutcomeIsInvoked_NullReturned() {
        List<ApiCrownCourtOutcome> crownCourtOutcomeList = new ArrayList<>();
        crownCourtOutcomeList.add(null);
        crownCourtOutcomeList.add(new ApiCrownCourtOutcome());
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .crownCourtOutcomeList(crownCourtOutcomeList)
                .build();
        assertThat(MaatCalculateContributionService.getCrownCourtOutcome(calculateContributionDTO)).isNull();
    }

    @Test
    void givenNullCrownCourtOutcomeEnum_whenGetCrownCourtOutcomeIsInvoked_NullReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .crownCourtOutcomeList(List.of(new ApiCrownCourtOutcome()))
                .build();
        assertThat(MaatCalculateContributionService.getCrownCourtOutcome(calculateContributionDTO)).isNull();
    }

    @Test
    void givenValidCrownCourtOutcome_whenGetCrownCourtOutcomeIsInvoked_CrownOutComeReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .crownCourtOutcomeList(List.of(new ApiCrownCourtOutcome().withOutcome(CrownCourtOutcome.ABANDONED)))
                .build();
        assertThat(MaatCalculateContributionService.getCrownCourtOutcome(calculateContributionDTO))
                .isEqualTo(CrownCourtOutcome.ABANDONED.getCode());
    }
}


