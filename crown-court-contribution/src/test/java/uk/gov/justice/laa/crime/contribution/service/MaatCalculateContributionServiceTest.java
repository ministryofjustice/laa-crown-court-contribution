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
import uk.gov.justice.laa.crime.contribution.builder.CalculateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.ContributionSummaryMapper;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.MaatCalculateContributionResponseMapper;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionVariationDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.common.ApiAssessment;
import uk.gov.justice.laa.crime.contribution.model.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentResult;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.TransferStatus;
import uk.gov.justice.laa.crime.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(maatCourtDataService.getRepOrderByRepId(anyInt())).thenReturn(TestModelDataBuilder.getRepOrderDTO());
        when(appealContributionService.calculateAppealContribution(any(CalculateContributionDTO.class))).thenReturn(new ApiMaatCalculateContributionResponse());
        maatCalculateContributionService.calculateContribution(CalculateContributionDTO.builder().repId(120).caseType(CaseType.APPEAL_CC).build());
        verify(appealContributionService, times(1)).calculateAppealContribution(any());
    }

    @Test
    void givenValidContributionAndCompareResultIsLessThanTwo_whenCreateContribsIsInvoked_thenContributionIsReturn() {
        when(compareContributionService.compareContribution(any())).thenReturn(1);
        when(maatCourtDataService.createContribution(any())).thenReturn(TestModelDataBuilder.getContribution());
        Contribution result = maatCalculateContributionService.createContribs(new CalculateContributionDTO());
        assertThat(result).isNotNull();
    }

    @Test
    void givenValidContributionAndCompareResultIsGreaterThanTwo_whenCreateContribsIsInvoked_thenNullIsReturn() {
        when(compareContributionService.compareContribution(any())).thenReturn(3);
        Contribution result = maatCalculateContributionService.createContribs(new CalculateContributionDTO());
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
                        .withAssessmentType(AssessmentType.FULL)
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
                        .withAssessmentType(AssessmentType.INIT)
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
                        .withAssessmentType(AssessmentType.INIT)
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
                        .withAssessmentType(AssessmentType.INIT)
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
                        .withAssessmentType(AssessmentType.HARDSHIP)
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
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withNewWorkReason(NewWorkReason.INF)))
                .build();

        assertThat(MaatCalculateContributionService.getNewWorkReason(calculateContributionDTO))
                .isEqualTo(NewWorkReason.INF);
    }

    @Test
    void givenInitialNewWorkReason_whenGetNewWorkReasonIsInvoked_thenInitialNewWorkReasonIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withNewWorkReason(NewWorkReason.NEW)))
                .build();

        assertThat(MaatCalculateContributionService.getNewWorkReason(calculateContributionDTO))
                .isEqualTo(NewWorkReason.NEW);
    }

    @Test
    void givenFullNewWorkReason_whenGetNewWorkReasonIsInvoked_thenNullIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.FULL)
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
                        .withAssessmentType(AssessmentType.PASSPORT)
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
                        .withAssessmentType(AssessmentType.PASSPORT)
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
                        .withAssessmentType(AssessmentType.PASSPORT)
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
                        .withAssessmentType(AssessmentType.PASSPORT)
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
                        .withAssessmentType(AssessmentType.PASSPORT)
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
                        .withAssessmentType(AssessmentType.PASSPORT)
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
    void givenAValidAnnualIncome_whenGetAnnualDisposableIncomeIsInvoked_thenAnnualIncomeIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;

        BigDecimal actual = MaatCalculateContributionService.getAnnualDisposableIncome(
                null, annualDisposableIncome
        );

        assertThat(actual)
                .isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenAValidAnnualIncomeAfterMagHardship_whenGetAnnualDisposableIncomeIsInvoked_thenAnnualIncomeAfterMagHardshipIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterMagHardship(annualDisposableIncome)
                .build();

        BigDecimal actual = MaatCalculateContributionService.getAnnualDisposableIncome(
                calculateContributionDTO, null
        );

        assertThat(actual)
                .isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenAValidTotalAnnualIncome_whenGetAnnualDisposableIncomeIsInvoked_thenTotalAnnualIncomeIsReturned() {
        BigDecimal annualDisposableIncome = BigDecimal.TEN;
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .totalAnnualDisposableIncome(annualDisposableIncome)
                .build();

        BigDecimal actual = MaatCalculateContributionService.getAnnualDisposableIncome(
                calculateContributionDTO, null
        );

        assertThat(actual)
                .isEqualTo(annualDisposableIncome);
    }

    @Test
    void givenNoIncome_whenGetAnnualDisposableIncomeIsInvoked_thenZeroIsReturned() {
        BigDecimal actual = MaatCalculateContributionService.getAnnualDisposableIncome(
                CalculateContributionDTO.builder().build(), null
        );

        assertThat(actual)
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
                .variation(HardshipReviewDetailType.ACTION.toString())
                .build();
        BigDecimal variationAmount = maatCalculateContributionService.calculateVariationAmount(
                TestModelDataBuilder.REP_ID, contributionVariationDTO);

        assertThat(variationAmount)
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenVariationRuleAsNull_whenCalculateVariationAmountIsInvoked_thenZeroIsReturned() {
        ApiCalculateHardshipByDetailResponse apiCalculateHardshipByDetailResponse = new ApiCalculateHardshipByDetailResponse()
                .withHardshipSummary(BigDecimal.TEN);
        when(crimeHardshipService.calculateHardshipForDetail(any(ApiCalculateHardshipByDetailRequest.class)))
                .thenReturn(apiCalculateHardshipByDetailResponse);
        ContributionVariationDTO contributionVariationDTO = ContributionVariationDTO.builder()
                .variation(HardshipReviewDetailType.FUNDING.toString())
                .build();
        BigDecimal variationAmount = maatCalculateContributionService.calculateVariationAmount(
                TestModelDataBuilder.REP_ID, contributionVariationDTO);

        assertThat(variationAmount)
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenCalcContribsAsN_whenCalcContribsIsInvoked_validResponseIsReturned() {
        ApiMaatCalculateContributionResponse expectedResponse =
                TestModelDataBuilder.getApiMaatCalculateContributionResponse();
        CalculateContributionDTO calculateContributionDTO = setupDataForCalcContribsTests();
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .calcContribs(Constants.N)
                .build();
        when(calculateContributionRequestMapper.map(any(), any(), any(), any()))
                .thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));
        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(TestModelDataBuilder.getCalculateContributionResponse());
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any()))
                .thenReturn(expectedResponse);

        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.calcContribs(
                calculateContributionDTO, contributionResponseDTO
        );

        assertThat(actualResponse)
                .isEqualTo(expectedResponse);
    }

    private CalculateContributionDTO setupDataForCalcContribsTests() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getContributionDTOForCalcContribs();
        when(maatCourtDataService.getContributionCalcParameters(anyString()))
                .thenReturn(new ContributionCalcParametersDTO());
        when(contributionRulesService.getActiveCCOutcome(any()))
                .thenReturn(CrownCourtOutcome.SUCCESSFUL);
        when(contributionRulesService.isContributionRuleApplicable(
                CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL
        )).thenReturn(false);
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
        when(calculateContributionRequestMapper.map(any(), any(), any(), any()))
                .thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));
        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(TestModelDataBuilder.getCalculateContributionResponse());
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any()))
                .thenReturn(expectedResponse);
        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.calcContribs(
                calculateContributionDTO, contributionResponseDTO
        );

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

        when(contributionRulesService.getActiveCCOutcome(any()))
                .thenReturn(CrownCourtOutcome.SUCCESSFUL);
        when(contributionRulesService.isContributionRuleApplicable(
                CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL
        ))
                .thenReturn(false);
        when(maatCourtDataService.getContributionCalcParameters(anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .upliftedIncomePercent(BigDecimal.TEN)
                        .minUpliftedMonthlyAmount(BigDecimal.ONE)
                        .totalMonths(2)
                        .build());
        when(calculateContributionRequestMapper.map(any(), any(), any(), any()))
                .thenReturn(Mockito.mock(ApiCalculateContributionRequest.class));
        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(TestModelDataBuilder.getCalculateContributionResponse()
                        .withMonthlyContributions(BigDecimal.ONE).withUpliftApplied(Constants.Y)
                );
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any()))
                .thenReturn(expectedResponse);

        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.calcContribs(
                calculateContributionDTO, contributionResponseDTO
        );

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
        when(contributionRulesService.getActiveCCOutcome(any()))
                .thenReturn(CrownCourtOutcome.SUCCESSFUL);
        when(contributionRulesService.isContributionRuleApplicable(
                CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL
        ))
                .thenReturn(false);
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
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any()))
                .thenReturn(expectedResponse);
        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.calcContribs(
                calculateContributionDTO, contributionResponseDTO
        );

        assertThat(actualResponse)
                .isEqualTo(expectedResponse);

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
        when(contributionRulesService.getActiveCCOutcome(any()))
                .thenReturn(CrownCourtOutcome.SUCCESSFUL);
        when(contributionRulesService.isContributionRuleApplicable(
                CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL
        ))
                .thenReturn(false);
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
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any()))
                .thenReturn(expectedResponse);

        ApiMaatCalculateContributionResponse actualResponse = maatCalculateContributionService.calcContribs(
                calculateContributionDTO, contributionResponseDTO
        );

        assertThat(actualResponse)
                .isEqualTo(expectedResponse);
    }

    @Test
    void givenDisposableIncomeAfterCrownHardship_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterCrownHardship(BigDecimal.TEN)
                .build();

        BigDecimal result = maatCalculateContributionService.calculateAnnualDisposableIncome(
                calculateContributionDTO,
                CrownCourtOutcome.SUCCESSFUL,
                false
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
                CrownCourtOutcome.SUCCESSFUL,
                false
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
                CrownCourtOutcome.SUCCESSFUL,
                false
        );

        assertThat(result)
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenDisposableIncomeAfterCrownHardshipAndNoVariation_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterCrownHardship(BigDecimal.TEN)
                .magCourtOutcome(MagCourtOutcome.COMMITTED)
                .caseType(CaseType.INDICTABLE)
                .build();

        when(contributionRulesService.getContributionVariation(
                CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL
        )).thenReturn(Optional.empty());

        BigDecimal result = maatCalculateContributionService.calculateAnnualDisposableIncome(
                calculateContributionDTO,
                CrownCourtOutcome.SUCCESSFUL,
                true
        );

        assertThat(result)
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenDisposableIncomeAfterCrownHardshipAndValidVariation_whenCalculateAnnualDisposableIncomeIsInvoked_thenValidIncomeIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .disposableIncomeAfterCrownHardship(BigDecimal.TEN)
                .magCourtOutcome(MagCourtOutcome.COMMITTED)
                .caseType(CaseType.INDICTABLE)
                .build();

        when(contributionRulesService.getContributionVariation(
                CaseType.INDICTABLE, MagCourtOutcome.COMMITTED, CrownCourtOutcome.SUCCESSFUL
        )).thenReturn(Optional.of(ContributionVariationDTO.builder().variation("FUNDING").build()));

        BigDecimal result = maatCalculateContributionService.calculateAnnualDisposableIncome(
                calculateContributionDTO,
                CrownCourtOutcome.SUCCESSFUL,
                true
        );

        assertThat(result)
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void givenTransferRequestedAndIndictableCase_whenIsCreateContributionRequiredIsInvoked_FalseIsReturned() {
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .build();

        boolean result = maatCalculateContributionService.isCreateContributionRequired(
                calculateContributionDTO, null, TransferStatus.REQUESTED
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
                calculateContributionDTO, repOrderDTO, TransferStatus.SENT
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
                calculateContributionDTO, repOrderDTO, TransferStatus.SENT
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
                calculateContributionDTO, repOrderDTO, TransferStatus.SENT
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
                calculateContributionDTO, repOrderDTO, TransferStatus.SENT
        );

        assertThat(result).isTrue();
    }

    @Test
    void givenIndictableCase_whenCalculateContributionIsInvoked_validResponseIsReturned() {
        RepOrderDTO repOrderDTO = TestModelDataBuilder.getRepOrderDTO();
        CalculateContributionDTO calculateContributionDTO = CalculateContributionDTO.builder()
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.FULL)
                        .withResult(AssessmentResult.PASS)
                        .withAssessmentDate(TestModelDataBuilder.TEST_DATE)))
                .build();

        when(maatCourtDataService.getRepOrderByRepId(null))
                .thenReturn(repOrderDTO);
        when(contributionService.checkContribsCondition(any()))
                .thenReturn(ContributionResponseDTO.builder().build());

        ApiMaatCalculateContributionResponse result = maatCalculateContributionService.calculateContribution(
                calculateContributionDTO);

        ApiMaatCalculateContributionResponse maatCalculateContributionResponse = new ApiMaatCalculateContributionResponse();

        assertThat(result)
                .isEqualTo(maatCalculateContributionResponse);
    }

    @Test
    void givenARequestWithDoContribsAsY_whenGetCalculateContributionResponseIsInvoked_thenResponseIsReturned() {
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .doContribs(Constants.Y)
                .build();
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
                .withEffectiveDate(LocalDateTime.now())
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpfrontContributions(BigDecimal.ZERO)
                .withUpliftApplied(Constants.N)
                .withBasedOn("Means");

        when(contributionService.checkContribsCondition(any()))
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
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any()))
                .thenReturn(expectedResponse);

        ApiMaatCalculateContributionResponse actualResponse =
                maatCalculateContributionService.getCalculateContributionResponse(
                        calculateContributionDTO, repOrderDTO
                );

        assertThat(actualResponse)
                .isEqualTo(expectedResponse);
    }

    @Test
    void givenARequestWithDoContribsAsNAndMonthlyContributionsZero_whenGetCalculateContributionResponseIsInvoked_thenResponseIsReturned() {
        RepOrderDTO repOrderDTO = RepOrderDTO.builder()
                .id(1234)
                .build();
        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .doContribs(Constants.Y)
                .build();
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getCalculateContributionDTO();
        ApiMaatCalculateContributionResponse expectedResponse = new ApiMaatCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpfrontContributions(BigDecimal.ZERO)
                .withContributionCap(BigDecimal.ZERO);

        when(contributionService.checkContribsCondition(any()))
                .thenReturn(contributionResponseDTO);

        ApiMaatCalculateContributionResponse actualResponse =
                maatCalculateContributionService.getCalculateContributionResponse(
                        calculateContributionDTO, repOrderDTO
                );

        assertThat(actualResponse)
                .isEqualTo(expectedResponse);
    }

    @Test
    void givenAValidCalculateContributionDTO_whenGetContributionSummariesIsInvoked_thenContributionSummaryListIsReturned() {
        List<ContributionsSummaryDTO> contributionSummaryDTO = List.of(TestModelDataBuilder.getContributionSummaryDTO());
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
    void givenATemplateAndContributionsAreCreated_whenDoContribsIsInvoked_thenProcessActivityFlagIsTrue() {
        CalculateContributionDTO calculateContributionDTO = TestModelDataBuilder.getCalculateContributionDTO();

        ContributionResponseDTO contributionResponseDTO = ContributionResponseDTO.builder()
                .template(1)
                .build();
        ApiMaatCalculateContributionResponse maatCalculateContributionResponse =
                TestModelDataBuilder.getApiMaatCalculateContributionResponse();

        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(new ApiCalculateContributionResponse()
                        .withMonthlyContributions(BigDecimal.TEN)
                );
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any()))
                .thenReturn(maatCalculateContributionResponse);
        when(maatCalculateContributionService.verifyAndCreateContribs(
                calculateContributionDTO, null, maatCalculateContributionResponse, null
        )).thenReturn(new Contribution());
        when(maatCourtDataService.getContributionCalcParameters(anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .totalMonths(2)
                        .build()
                );

        ApiMaatCalculateContributionResponse response = maatCalculateContributionService.doContribs(
                calculateContributionDTO,
                contributionResponseDTO,
                null,
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
        ApiMaatCalculateContributionResponse maatCalculateContributionResponse =
                TestModelDataBuilder.getApiMaatCalculateContributionResponse();

        when(calculateContributionService.calculateContribution(any()))
                .thenReturn(new ApiCalculateContributionResponse()
                        .withMonthlyContributions(BigDecimal.TEN)
                );
        when(maatCalculateContributionResponseMapper.map(any(), any(), any(), any()))
                .thenReturn(maatCalculateContributionResponse);
        when(maatCalculateContributionService.verifyAndCreateContribs(
                calculateContributionDTO, null, maatCalculateContributionResponse, null
        )).thenReturn(null);
        when(maatCourtDataService.getContributionCalcParameters(anyString()))
                .thenReturn(ContributionCalcParametersDTO.builder()
                        .totalMonths(2)
                        .build()
                );

        ApiMaatCalculateContributionResponse response = maatCalculateContributionService.doContribs(
                calculateContributionDTO,
                contributionResponseDTO,
                null,
                null
        );

        assertThat(response.getProcessActivity()).isNull();
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
}


