package uk.gov.justice.laa.crime.contribution.service;

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
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.*;
import uk.gov.justice.laa.crime.common.model.contribution.ApiContributionTransferRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.UpdateContributionRequest;
import uk.gov.justice.laa.crime.contribution.repository.CorrespondenceRuleRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.contribution.common.Constants.*;
import static uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder.REP_ID;
import static uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder.getRepOrderDTO;

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
    private CompareContributionService compareContributionService;

    private static Stream<Arguments> getAssessmentRequestForIojResult() {
        return Stream.of(
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                null,
                                PASS,
                                FULL,
                                PASS
                        ),
                        PASS
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                null,
                                null,
                                PASS,
                                null,
                                PASS
                        ),
                        PASS
                )
        );
    }

    private static Stream<Arguments> getAssessmentRequestForMeansResult() {
        return Stream.of(
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                null,
                                PASS,
                                FULL,
                                PASS
                        ),
                        FULL
                ),
                Arguments.of(
                        new AssessmentRequestDTO
                                (PASS,
                                        null,
                                        null,
                                        PASS,
                                        null,
                                        PASS
                                ),
                        INIT.concat(PASS)
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                null,
                                null,
                                null,
                                null,
                                PASS
                        ),
                        NONE
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                PASS,
                                PASS,
                                FULL,
                                PASS
                        ),
                        PASSPORT
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                FAIL,
                                PASS,
                                FULL,
                                PASS
                        ),
                        FAILPORT
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                PASS,
                                PASS,
                                PASS
                        ),
                        PASS
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                FAIL,
                                PASS,
                                PASS
                        ),
                        PASS
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                FAIL,
                                FAIL,
                                PASS
                        ),
                        PASS
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                FAIL,
                                FAIL,
                                FAIL
                        ),
                        FAIL
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                FAIL,
                                FAIL,
                                null
                        ),
                        FAIL
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                FULL,
                                FAIL,
                                FAIL
                        ),
                        FAIL
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                FULL,
                                FAIL,
                                null
                        ),
                        FAIL
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                HARDSHIP_APPLICATION,
                                FAIL,
                                FAIL
                        ),
                        FAIL
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                HARDSHIP_APPLICATION,
                                FAIL,
                                null
                        ),
                        FAIL
                )
        );
    }

    private static Stream<Arguments> getAssessmentRequestForNullMeansResult() {
        return Stream.of(
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                INIT,
                                FAIL,
                                FAIL
                        )
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                HARDSHIP_APPLICATION,
                                FAIL,
                                TEMP
                        )
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                INIT,
                                INIT,
                                INIT
                        )
                ),
                Arguments.of(
                        new AssessmentRequestDTO(
                                PASS,
                                PASS,
                                TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                                INIT,
                                INIT,
                                null
                        )
                )
        );
    }

    private static Stream<Arguments> inValidContributionRequest() {
        return Stream.of(
                Arguments.of(
                        new ContributionRequestDTO(
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
                                PASS
                        )
                ),
                Arguments.of(
                        new ContributionRequestDTO(
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
                                PASS
                        )
                )
        );
    }

    private static Stream<Arguments> NoContributionRequest() {
        return Stream.of(
                Arguments.of(
                        new ContributionRequestDTO(
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
                                PASS
                        )
                ),
                Arguments.of(
                        new ContributionRequestDTO(
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
                                PASS
                        )
                ),
                Arguments.of(
                        new ContributionRequestDTO(PASS,
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
                                PASS
                        )
                ),
                Arguments.of(
                        new ContributionRequestDTO(
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
                                PASS
                        )
                ),
                Arguments.of(
                        new ContributionRequestDTO(
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
                                PASS
                        )
                ),
                Arguments.of(
                        new ContributionRequestDTO(
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
                                PASS
                        )
                )
        );
    }

    private static Stream<Arguments> contributionRequest() {
        return Stream.of(
                Arguments.of(
                        new ContributionRequestDTO(PASS,
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
                                PASS
                        )
                ),
                Arguments.of(
                        new ContributionRequestDTO(
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
                                PASS
                        )
                )
        );
    }

    @ParameterizedTest()
    @MethodSource("getAssessmentRequestForIojResult")
    void givenAValidAssessmentRequest_whenGetAssessmentResultIsInvoked_thenReturnCorrectIojResultResponse(
            AssessmentRequestDTO request, String expectedResult) {

        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);

        assertThat(response.getIojResult())
                .isEqualTo(expectedResult);
    }

    @ParameterizedTest()
    @MethodSource("getAssessmentRequestForMeansResult")
    void givenAValidAssessmentRequest_whenGetAssessmentResultIsInvoked_thenReturnCorrectMeansResultResponse(
            AssessmentRequestDTO request, String expectedResult) {

        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);

        assertThat(response.getMeansResult())
                .isEqualTo(expectedResult);
    }

    @ParameterizedTest()
    @MethodSource("getAssessmentRequestForNullMeansResult")
    void givenAValidAssessmentRequest_whenGetAssessmentResultIsInvoked_thenReturnEmptyMeansResult(
            AssessmentRequestDTO request) {

        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);

        assertThat(response.getMeansResult())
                .isNull();
    }

    @ParameterizedTest()
    @MethodSource("inValidContributionRequest")
    void givenAInvalidContributeRequest_whenCheckContribConditionIsInvoked_thenReturnNull(ContributionRequestDTO request) {
        ContributionResponseDTO response = contributionService.checkContribsCondition(request);

        assertThat(response.getId())
                .isNull();
    }

    @ParameterizedTest()
    @MethodSource("NoContributionRequest")
    void givenAValidContributeRequest_whenCheckContribConditionIsInvoked_thenReturnNoContribution(
            ContributionRequestDTO request) {

        when(repository.getCoteInfo(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(TestModelDataBuilder.getCorrespondenceRuleAndTemplateInfo()));

        ContributionResponseDTO response = contributionService.checkContribsCondition(request);

        softly.assertThat(response.getId())
                .isEqualTo(1);
        softly.assertThat(response.getCorrespondenceType())
                .isEqualTo("CONTRIBUTION_NOTICE");
        softly.assertThat(response.getCorrespondenceTypeDesc())
                .isEqualTo("Contribution Notice");
        softly.assertThat(response.getUpliftCote())
                .isEqualTo(1);
        softly.assertThat(response.getReassessmentCoteId())
                .isEqualTo(1);
        softly.assertThat(response.getTemplateDesc())
                .isEqualTo("No contributions required");
    }

    @ParameterizedTest()
    @MethodSource("contributionRequest")
    void givenAValidContributeRequest_whenCheckContribConditionIsInvoked_thenReturnYesContribution(
            ContributionRequestDTO request) {

        when(repository.getCoteInfo(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(TestModelDataBuilder.getCorrespondenceRuleAndTemplateInfo()));

        ContributionResponseDTO response = contributionService.checkContribsCondition(request);

        softly.assertThat(response.getDoContribs())
                .isEqualTo("Y");
        softly.assertThat(response.getId())
                .isEqualTo(1);
        softly.assertThat(response.getCorrespondenceType())
                .isEqualTo("CONTRIBUTION_NOTICE");
        softly.assertThat(response.getCorrespondenceTypeDesc())
                .isEqualTo("Contribution Notice");
        softly.assertThat(response.getUpliftCote())
                .isEqualTo(1);
        softly.assertThat(response.getReassessmentCoteId())
                .isEqualTo(1);
        softly.assertThat(response.getTemplateDesc())
                .isEqualTo("No contributions required");
    }

    @ParameterizedTest()
    @MethodSource("contributionRequest")
    void givenAValidContributeRequestAndEmptyCorrespondence_whenCheckContribConditionIsInvoked_thenReturnYesContribution(
            ContributionRequestDTO request) {

        when(repository.getCoteInfo(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(TestModelDataBuilder.getEmptyCorrespondenceRuleAndTemplateInfo()));

        ContributionResponseDTO response = contributionService.checkContribsCondition(request);

        softly.assertThat(response.getDoContribs())
                .isEqualTo("Y");
        softly.assertThat(response.getId())
                .isEqualTo(1);
        softly.assertThat(response.getCorrespondenceType())
                .isEmpty();
    }

    @Test
    void givenAValidContributionRequest_whenCheckContribConditionIsInvoked_thenReturnNoContribution() {
        ContributionRequestDTO request  = ContributionRequestDTO.builder()
                .caseType(CaseType.INDICTABLE)
                .initResult("FULL")
                .fullResult("PASS")
                .magCourtOutcome("SENT FOR TRIAL")
                .crownCourtOutcome("")
                .iojResult("PASS")
                .build();

        when(repository.getCoteInfo(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        ContributionResponseDTO response = contributionService.checkContribsCondition(request);

        softly.assertThat(response.getDoContribs())
                .isEqualTo("N");
        softly.assertThat(response.getCalcContribs())
                .isEqualTo("N");
    }

    @Test
    void givenAValidRepId_whenCds15WorkAroundIsInvokedWithFailPassportAssessment_thenTrueIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("Y");
        repOrderDTO.getPassportAssessments().get(0).setResult(PassportAssessmentResult.FAIL.getResult());
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        repOrderDTO.getFinancialAssessments().get(0).setInitResult(InitAssessmentResult.PASS.getResult());

        assertThat(contributionService.isCds15WorkAround(repOrderDTO))
                .isTrue();
    }

    @Test
    void givenAValidRepId_whenCds15WorkAroundIsInvokedWithPassPassportAssessment_thenFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("Y");
        repOrderDTO.getPassportAssessments().get(0).setResult(PassportAssessmentResult.PASS.getResult());
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        repOrderDTO.getFinancialAssessments().get(0).setInitResult(InitAssessmentResult.PASS.getResult());

        assertThat(contributionService.isCds15WorkAround(repOrderDTO))
                .isFalse();
    }

    @Test
    void givenAValidRepId_whenCds15WorkAroundIsInvokedWithFailedInitialAssessment_thenFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("Y");
        repOrderDTO.getPassportAssessments().get(0).setResult(PassportAssessmentResult.PASS.getResult());
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        repOrderDTO.getFinancialAssessments().get(0).setInitResult(InitAssessmentResult.FAIL.getResult());

        assertThat(contributionService.isCds15WorkAround(repOrderDTO))
                .isFalse();
    }

    @Test
    void givenAValidRepId_whenCds15WorkAroundIsInvokedWithFailedAssessments_thenFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("Y");
        repOrderDTO.getPassportAssessments().get(0).setResult(PassportAssessmentResult.FAIL.getResult());
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        repOrderDTO.getFinancialAssessments().get(0).setInitResult(InitAssessmentResult.FAIL.getResult());

        assertThat(contributionService.isCds15WorkAround(repOrderDTO))
                .isFalse();
    }

    @Test
    void givenRepOrderWithNoPassportAssessments_whenGetPassportAssessmentResultIsInvoked_thenNullIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setPassportAssessments(new ArrayList<>());

        assertThat(ContributionService.getPassportAssessmentResult(repOrderDTO))
                .isNull();
    }

    @Test
    void givenRepOrderWithReplacedPassportAssessment_whenGetPassportAssessmentResultIsInvoked_thenValidResultIsReturned() {
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

        assertThat(ContributionService.getInitialAssessmentResult(repOrderDTO))
                .isNull();
    }

    @Test
    void givenRepOrderWithFinancialAssessments_whenGetInitialAssessmentResultIsInvoked_thenValidResultIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        repOrderDTO.getFinancialAssessments().get(0).setInitResult(InitAssessmentResult.PASS.getResult());

        assertThat(ContributionService.getInitialAssessmentResult(repOrderDTO))
                .isEqualTo(InitAssessmentResult.PASS.getResult());
    }

    @Test
    void givenInvalidRepId_whenHasMessageOutcomeChangedIsInvoked_thenFalseIsReturn() {
        boolean hasMsgOutcomeChanged = contributionService.hasMessageOutcomeChanged("outcome", null);

        assertThat(hasMsgOutcomeChanged)
                .isFalse();

    }

    @Test
    void givenValidRepIdAndOutcomeIsNotMatch_whenHasMessageOutcomeChangedIsInvoked_thenFalseIsReturn() {
        boolean hasMsgOutcomeChanged = contributionService.hasMessageOutcomeChanged("outcome", getRepOrderDTO(REP_ID));

        assertThat(hasMsgOutcomeChanged)
                .isFalse();

    }

    @Test
    void givenValidRepIdAndOutcomeIsNull_whenHasMessageOutcomeChangedIsInvoked_thenTrueIsReturn() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setMagsOutcome(null);
        boolean hasMsgOutcomeChanged = contributionService.hasMessageOutcomeChanged("outcome", repOrderDTO);

        assertThat(hasMsgOutcomeChanged)
                .isTrue();

    }

    @Test
    void givenValidRepId_whenHasMessageOutcomeChangedIsInvoked_thenTrueIsReturn() {
        boolean hasMsgOutcomeChanged =
                contributionService.hasMessageOutcomeChanged("outcomeMessage", getRepOrderDTO(REP_ID));

        assertThat(hasMsgOutcomeChanged)
                .isTrue();

    }

    @Test
    void givenValidRepIdAndNullCCOutcome_whenHasCCOutcomeChangedIsInvoked_thenFalseIsReturn() {
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(REP_ID))
                .thenReturn(null);
        boolean hasCCOutcomeChanged = contributionService.hasCCOutcomeChanged(REP_ID);

        assertThat(hasCCOutcomeChanged)
                .isFalse();

    }

    @Test
    void givenValidRepIdAndEmptyCCOutcome_whenHasCCOutcomeChangedIsInvoked_thenFalseIsReturn() {
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(REP_ID))
                .thenReturn(Collections.emptyList());
        boolean hasCCOutcomeChanged = contributionService.hasCCOutcomeChanged(REP_ID);

        assertThat(hasCCOutcomeChanged)
                .isFalse();

    }

    @Test
    void givenValidRepId_whenHasCCOutcomeChangedIsInvoked_thenTrueIsReturn() {
        List<RepOrderCCOutcomeDTO> outcomeList = new ArrayList<>();
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12346, CrownCourtOutcome.PART_CONVICTED.getCode()));
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12345, CrownCourtOutcome.ABANDONED.getCode()));
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(any()))
                .thenReturn(outcomeList);
        boolean hasCCOutcomeChanged = contributionService.hasCCOutcomeChanged(REP_ID);

        assertThat(hasCCOutcomeChanged)
                .isTrue();

    }

    @Test
    void givenValidRepIdAndEmptyOutcome_whenHasCCOutcomeChangedIsInvoked_thenFalseIsReturn() {
        List<RepOrderCCOutcomeDTO> outcomeList = new ArrayList<>();
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12346, CrownCourtOutcome.PART_CONVICTED.getCode()));
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12345, null));
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(any()))
                .thenReturn(outcomeList);
        boolean hasCCOutcomeChanged = contributionService.hasCCOutcomeChanged(REP_ID);

        assertThat(hasCCOutcomeChanged)
                .isFalse();

    }

    @Test
    void givenValidRepIdAndOutcomeIsAquitted_whenHasCCOutcomeChangedIsInvoked_thenFalseIsReturn() {
        List<RepOrderCCOutcomeDTO> outcomeList = new ArrayList<>();
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12346, CrownCourtOutcome.PART_CONVICTED.getCode()));
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12345, CrownCourtOutcome.AQUITTED.getCode()));
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(any()))
                .thenReturn(outcomeList);
        boolean hasCCOutcomeChanged = contributionService.hasCCOutcomeChanged(REP_ID);

        assertThat(hasCCOutcomeChanged)
                .isFalse();

    }


    @Test
    void givenValidRepIdAndCaseTypeDoNotMatch_whenHasApplicationStatusChangedIsInvoked_thenFalseIsReturn() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        boolean hasApplicationStatusChanged =
                contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.APPEAL_CC, RORS_STATUS);

        assertThat(hasApplicationStatusChanged)
                .isFalse();
    }

    @Test
    void givenValidRepIdAndRorsStatusMatch_whenHasApplicationStatusChangedIsInvoked_thenFalseIsReturn() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        boolean hasApplicationStatusChanged =
                contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, RORS_STATUS);

        assertThat(hasApplicationStatusChanged)
                .isFalse();
    }

    @Test
    void givenValidRepIdAndRorsStatusDoNotMatch_whenHasApplicationStatusChangedIsInvoked_thenTrueIsReturn() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setRorsStatus(RORS_STATUS_CURR);
        boolean hasApplicationStatusChanged =
                contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, RORS_STATUS);

        assertThat(hasApplicationStatusChanged)
                .isTrue();
    }

    @Test
    void givenInvalidRepId_whenHasApplicationStatusChangedIsInvoked_thenFalseIsReturn() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        boolean hasApplicationStatusChanged =
                contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, RORS_STATUS);

        assertThat(hasApplicationStatusChanged)
                .isFalse();
    }

    @Test
    void givenValidRepIdAndRorsStatusIsNull_whenHasApplicationStatusChangedIsInvoked_thenFalseIsReturn() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setRorsStatus(null);
        boolean hasApplicationStatusChanged =
                contributionService.hasApplicationStatusChanged(repOrderDTO, CaseType.INDICTABLE, RORS_STATUS);

        assertThat(hasApplicationStatusChanged)
                .isFalse();
    }

    @Test
    void givenContributionTransferRequest_whenTransferRequestIsInvoked_thenCourtDataAPIIsCalled() {
        ApiContributionTransferRequest data = new ApiContributionTransferRequest()
                .withUserModified("mock-u")
                .withContributionId(1000);

        contributionService.requestTransfer(data);
        verify(maatCourtDataService).updateContribution(any(UpdateContributionRequest.class));
    }
}

