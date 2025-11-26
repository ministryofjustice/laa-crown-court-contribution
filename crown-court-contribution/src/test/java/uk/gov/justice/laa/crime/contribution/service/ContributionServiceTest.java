package uk.gov.justice.laa.crime.contribution.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.contribution.common.Constants.FAIL;
import static uk.gov.justice.laa.crime.contribution.common.Constants.FAILPORT;
import static uk.gov.justice.laa.crime.contribution.common.Constants.FULL;
import static uk.gov.justice.laa.crime.contribution.common.Constants.HARDSHIP_APPLICATION;
import static uk.gov.justice.laa.crime.contribution.common.Constants.INEL;
import static uk.gov.justice.laa.crime.contribution.common.Constants.INIT;
import static uk.gov.justice.laa.crime.contribution.common.Constants.NONE;
import static uk.gov.justice.laa.crime.contribution.common.Constants.PASS;
import static uk.gov.justice.laa.crime.contribution.common.Constants.PASSPORT;
import static uk.gov.justice.laa.crime.contribution.common.Constants.TEMP;
import static uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder.REP_ID;
import static uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder.getRepOrderDTO;

import uk.gov.justice.laa.crime.contribution.builder.ContributionResponseDTOMapper;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.repository.CorrespondenceRuleRepository;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.MeansAssessmentResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.enums.PassportAssessmentResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class ContributionServiceTest {
    private static final String CONTRIBUTION_NO = "N";
    private static final String CONTRIBUTION_YES = "Y";
    private static final String RORS_STATUS = "rors-status";
    private static final String RORS_STATUS_CURR = "CURR";

    @InjectSoftAssertions
    private SoftAssertions softly;

    @InjectMocks
    private ContributionService contributionService;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private CorrespondenceRuleRepository repository;

    @Mock
    private ContributionResponseDTOMapper contributionResponseDTOMapper;

    private static Stream<Arguments> getAssessmentRequestForMeansResult() {
        return Stream.of(
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, null, FULL, INEL, PASS), INEL),
                Arguments.of(new AssessmentRequestDTO(PASS, null, null, PASS, null, PASS), INIT.concat(PASS)),
                Arguments.of(new AssessmentRequestDTO(PASS, null, null, null, null, PASS), NONE),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, PASS, PASS, FULL, PASS), PASSPORT),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, FAIL, PASS, FULL, PASS), FAILPORT),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE, PASS, PASS, PASS),
                        PASS),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE, FAIL, PASS, PASS),
                        PASS),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE, FAIL, FAIL, PASS),
                        PASS),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE, FAIL, FAIL, FAIL),
                        FAIL),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE, FAIL, FAIL, null),
                        FAIL),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE, FULL, FAIL, FAIL),
                        FAIL),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE, FULL, FAIL, null),
                        FAIL),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                HARDSHIP_APPLICATION,
                                FAIL,
                                FAIL),
                        FAIL),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                HARDSHIP_APPLICATION,
                                FAIL,
                                null),
                        FAIL));
    }

    private static Stream<Arguments> getAssessmentRequestForNullMeansResult() {
        return Stream.of(
                Arguments.of(new AssessmentRequestDTO(
                        PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE, INIT, FAIL, FAIL)),
                Arguments.of(new AssessmentRequestDTO(
                        PASS,
                        PASS,
                        TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        HARDSHIP_APPLICATION,
                        FAIL,
                        TEMP)),
                Arguments.of(new AssessmentRequestDTO(
                        PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE, INIT, INIT, INIT)),
                Arguments.of(new AssessmentRequestDTO(
                        PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE, INIT, INIT, null)));
    }

    private static Stream<Arguments> inValidContributionRequest() {
        return Stream.of(
                Arguments.of(new ContributionRequestDTO(
                        PASS,
                        PASS,
                        CaseType.APPEAL_CC,
                        null,
                        FAIL,
                        FAIL,
                        PASS,
                        FAIL,
                        FAIL,
                        PASS,
                        BigDecimal.ZERO,
                        CONTRIBUTION_YES,
                        PASS)),
                Arguments.of(new ContributionRequestDTO(
                        PASS,
                        PASS,
                        CaseType.INDICTABLE,
                        null,
                        PASS,
                        PASS,
                        PASS,
                        FAIL,
                        FULL,
                        PASS,
                        BigDecimal.ZERO,
                        CONTRIBUTION_NO,
                        PASS)));
    }

    private static Stream<Arguments> contributionRequest() {
        return Stream.of(
                Arguments.of(new ContributionRequestDTO(
                        PASS,
                        PASS,
                        CaseType.INDICTABLE,
                        null,
                        PASS,
                        PASS,
                        PASS,
                        FAIL,
                        "INEL",
                        PASS,
                        BigDecimal.ONE,
                        CONTRIBUTION_YES,
                        PASS)),
                Arguments.of(new ContributionRequestDTO(
                        PASS,
                        PASS,
                        CaseType.INDICTABLE,
                        null,
                        PASS,
                        PASS,
                        PASS,
                        FAIL,
                        FULL,
                        PASS,
                        BigDecimal.ONE,
                        CONTRIBUTION_YES,
                        PASS)),
                Arguments.of(new ContributionRequestDTO(
                        PASS,
                        PASS,
                        CaseType.INDICTABLE,
                        null,
                        PASS,
                        PASS,
                        PASS,
                        FAIL,
                        FAIL,
                        PASS,
                        BigDecimal.ZERO,
                        CONTRIBUTION_YES,
                        PASS)),
                Arguments.of(new ContributionRequestDTO(
                        PASS,
                        PASS,
                        CaseType.EITHER_WAY,
                        null,
                        PASS,
                        PASS,
                        PASS,
                        FAIL,
                        FAIL,
                        PASS,
                        BigDecimal.ZERO,
                        CONTRIBUTION_YES,
                        PASS)),
                Arguments.of(new ContributionRequestDTO(
                        PASS,
                        PASS,
                        CaseType.CC_ALREADY,
                        null,
                        PASS,
                        PASS,
                        PASS,
                        FAIL,
                        FAIL,
                        PASS,
                        BigDecimal.ZERO,
                        CONTRIBUTION_YES,
                        PASS)),
                Arguments.of(new ContributionRequestDTO(
                        PASS,
                        PASS,
                        CaseType.COMMITAL,
                        LocalDate.now(),
                        PASS,
                        PASS,
                        PASS,
                        FAIL,
                        FAIL,
                        PASS,
                        BigDecimal.ZERO,
                        CONTRIBUTION_YES,
                        PASS)),
                Arguments.of(new ContributionRequestDTO(
                        PASS,
                        PASS,
                        CaseType.APPEAL_CC,
                        null,
                        PASS,
                        PASS,
                        PASS,
                        FAIL,
                        FAIL,
                        PASS,
                        BigDecimal.ZERO,
                        CONTRIBUTION_YES,
                        PASS)),
                Arguments.of(new ContributionRequestDTO(
                        PASS,
                        PASS,
                        CaseType.APPEAL_CC,
                        null,
                        FAIL,
                        PASS,
                        PASS,
                        FAIL,
                        FAIL,
                        PASS,
                        BigDecimal.ZERO,
                        CONTRIBUTION_YES,
                        PASS)));
    }

    @ParameterizedTest()
    @MethodSource("getAssessmentRequestForMeansResult")
    void givenAValidAssessmentRequest_whenGetMeansAssessmentResultIsInvoked_thenReturnCorrectMeansResultResponse(
            AssessmentRequestDTO request, String expectedResult) {
        MeansAssessmentResult result = contributionService.getMeansAssessmentResult(request);
        assertThat(result.getResult()).isEqualTo(expectedResult);
    }

    @ParameterizedTest()
    @MethodSource("getAssessmentRequestForNullMeansResult")
    void givenAValidAssessmentRequest_whenGetMeansAssessmentResultIsInvoked_thenReturnEmptyMeansResult(
            AssessmentRequestDTO request) {
        MeansAssessmentResult result = contributionService.getMeansAssessmentResult(request);
        assertThat(result).isNull();
    }

    @ParameterizedTest()
    @MethodSource("inValidContributionRequest")
    void givenAInvalidContributeRequest_whenCheckContribConditionIsInvoked_thenReturnNull(
            ContributionRequestDTO request) {
        ContributionResponseDTO response = contributionService.checkContributionsCondition(request);

        assertThat(response.getId()).isNull();
    }

    @ParameterizedTest()
    @MethodSource("contributionRequest")
    void givenAValidContributeRequest_whenCheckContribConditionIsInvoked_thenReturnValidContribution(
            ContributionRequestDTO request) {

        when(repository.getCoteInfo(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(TestModelDataBuilder.getCorrespondenceRuleAndTemplateInfo()));

        contributionService.checkContributionsCondition(request);
        verify(contributionResponseDTOMapper).map(any(), any());
    }

    @Test
    void givenAValidContributionRequest_whenCheckContribConditionIsInvoked_thenReturnNoContribution() {
        ContributionRequestDTO request = ContributionRequestDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .initResult("FULL")
                .fullResult("PASS")
                .magCourtOutcome("SENT FOR TRIAL")
                .crownCourtOutcome("")
                .iojResult("PASS")
                .build();

        when(repository.getCoteInfo(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        ContributionResponseDTO response = contributionService.checkContributionsCondition(request);

        softly.assertThat(response.getDoContribs()).isEqualTo("N");
        softly.assertThat(response.getCalcContribs()).isEqualTo("N");
    }

    @Test
    void givenAValidRepId_whenCds15WorkAroundIsInvokedWithFailPassportAssessment_thenTrueIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("Y");
        repOrderDTO.getPassportAssessments().get(0).setResult(PassportAssessmentResult.FAIL.getResult());
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        repOrderDTO.getFinancialAssessments().get(0).setInitResult(InitAssessmentResult.PASS.getResult());

        assertThat(contributionService.isCds15WorkAround(repOrderDTO)).isTrue();
    }

    @Test
    void givenAValidRepId_whenCds15WorkAroundIsInvokedWithPassPassportAssessment_thenFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("Y");
        repOrderDTO.getPassportAssessments().get(0).setResult(PassportAssessmentResult.PASS.getResult());
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        repOrderDTO.getFinancialAssessments().get(0).setInitResult(InitAssessmentResult.PASS.getResult());

        assertThat(contributionService.isCds15WorkAround(repOrderDTO)).isFalse();
    }

    @Test
    void givenAValidRepId_whenCds15WorkAroundIsInvokedWithFailedInitialAssessment_thenFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("Y");
        repOrderDTO.getPassportAssessments().get(0).setResult(PassportAssessmentResult.PASS.getResult());
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        repOrderDTO.getFinancialAssessments().get(0).setInitResult(InitAssessmentResult.FAIL.getResult());

        assertThat(contributionService.isCds15WorkAround(repOrderDTO)).isFalse();
    }

    @Test
    void givenAValidRepId_whenCds15WorkAroundIsInvokedWithFailedAssessments_thenFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("Y");
        repOrderDTO.getPassportAssessments().get(0).setResult(PassportAssessmentResult.FAIL.getResult());
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        repOrderDTO.getFinancialAssessments().get(0).setInitResult(InitAssessmentResult.FAIL.getResult());

        assertThat(contributionService.isCds15WorkAround(repOrderDTO)).isFalse();
    }

    @Test
    void givenRepOrderWithNoPassportAssessments_whenGetPassportAssessmentResultIsInvoked_thenNullIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setPassportAssessments(new ArrayList<>());

        assertThat(ContributionService.getPassportAssessmentResult(repOrderDTO)).isNull();
    }

    @Test
    void
            givenRepOrderWithReplacedPassportAssessment_whenGetPassportAssessmentResultIsInvoked_thenValidResultIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("Y");
        repOrderDTO.getPassportAssessments().get(0).setResult(PassportAssessmentResult.PASS.getResult());

        assertThat(ContributionService.getPassportAssessmentResult(repOrderDTO))
                .isEqualTo(PassportAssessmentResult.PASS.getResult());
    }

    @Test
    void givenRepOrderWithNoFinancialAssessments_whenGetInitialAssessmentResultIsInvoked_thenNullIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setFinancialAssessments(new ArrayList<>());

        assertThat(ContributionService.getInitialAssessmentResult(repOrderDTO)).isNull();
    }

    @Test
    void givenRepOrderWithFinancialAssessments_whenGetInitialAssessmentResultIsInvoked_thenValidResultIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        repOrderDTO.getFinancialAssessments().get(0).setInitResult(InitAssessmentResult.PASS.getResult());

        assertThat(ContributionService.getInitialAssessmentResult(repOrderDTO))
                .isEqualTo(InitAssessmentResult.PASS.getResult());
    }

    @ParameterizedTest
    @MethodSource("ccOutcomeScenarios")
    void givenVariousOutcomes_whenHasCCOutcomeChangedIsInvoked_thenResultMatchesExpectation(
            List<RepOrderCCOutcomeDTO> outcomes, boolean expected) {
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(REP_ID)).thenReturn(outcomes);
        boolean result = contributionService.hasCCOutcomeChanged(REP_ID);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> ccOutcomeScenarios() {
        return Stream.of(
                // --- Empty outcome lists ---
                Arguments.of(List.of(), false),
                Arguments.of(Collections.emptyList(), false),
                // --- Lowest ID has a null outcome ---
                Arguments.of(
                        List.of(
                                TestModelDataBuilder.getRepOrderCCOutcomeDTO(
                                        12346, CrownCourtOutcome.PART_CONVICTED.getCode()),
                                TestModelDataBuilder.getRepOrderCCOutcomeDTO(12345, null)),
                        false),
                // --- Lowest ID is ACQUITTED ---
                Arguments.of(
                        List.of(
                                TestModelDataBuilder.getRepOrderCCOutcomeDTO(
                                        12346, CrownCourtOutcome.PART_CONVICTED.getCode()),
                                TestModelDataBuilder.getRepOrderCCOutcomeDTO(
                                        12345, CrownCourtOutcome.AQUITTED.getCode())),
                        false),
                // --- Lowest ID has a different (non-ACQUITTED) outcome → true ---
                Arguments.of(
                        List.of(
                                TestModelDataBuilder.getRepOrderCCOutcomeDTO(
                                        12346, CrownCourtOutcome.PART_CONVICTED.getCode()),
                                TestModelDataBuilder.getRepOrderCCOutcomeDTO(
                                        12345, CrownCourtOutcome.ABANDONED.getCode())),
                        true));
    }

    @Test
    void givenValidRepIdAndRorsStatusDoNotMatch_whenHasApplicationStatusChangedIsInvoked_thenTrueIsReturn() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setRorsStatus(RORS_STATUS_CURR);
        boolean hasApplicationStatusChanged =
                contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, RORS_STATUS);

        assertThat(hasApplicationStatusChanged).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidStatusChangeScenarios")
    void givenVariousInputs_whenHasApplicationStatusChangedIsInvoked_thenReturnsFalse(
            RepOrderDTO repOrderDTO, CaseType caseType, String status) {
        boolean result = contributionService.hasApplicationStatusChanged(repOrderDTO, caseType, status);
        assertThat(result).isFalse();
    }

    private static Stream<Arguments> invalidStatusChangeScenarios() {
        return Stream.of(
                // repOrderDTO has matching RORS status → false
                Arguments.of(getRepOrderDTO(REP_ID), CaseType.INDICTABLE, RORS_STATUS),
                // repOrderDTO has null RORS status → false
                Arguments.of(withNullRorsStatus(getRepOrderDTO(REP_ID)), CaseType.INDICTABLE, RORS_STATUS),
                // caseType not INDICTABLE → false
                Arguments.of(getRepOrderDTO(REP_ID), CaseType.EITHER_WAY, RORS_STATUS),
                // repOrderDTO is null → false
                Arguments.of(null, CaseType.INDICTABLE, RORS_STATUS));
    }

    private static RepOrderDTO withNullRorsStatus(RepOrderDTO dto) {
        dto.setRorsStatus(null);
        return dto;
    }
}
