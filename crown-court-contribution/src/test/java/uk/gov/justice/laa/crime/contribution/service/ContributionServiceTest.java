package uk.gov.justice.laa.crime.contribution.service;

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
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.repository.CorrespondenceRuleRepository;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
class ContributionServiceTest {
    private static final String CONTRIBUTION_NO = "N";
    private static final String CONTRIBUTION_YES = "Y";
    private static final String RORS_STATUS = "rors-status";
    private static final String RORS_STATUS_CURR = "CURR";
    private static final String LAA_TRANSACTION_ID = "laaTransactionId";
    private static final LocalDateTime dateCreated = LocalDateTime.parse("2023-07-10T15:01:25");

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
                .thenReturn(TestModelDataBuilder.getCorrespondenceRuleAndTemplateInfo());

        ContributionResponseDTO response = contributionService.checkContribsCondition(request);

        assertThat(response.getId())
                .isEqualTo(1);
        assertThat(response.getCorrespondenceType())
                .isEqualTo("CONTRIBUTION_NOTICE");
        assertThat(response.getCorrespondenceTypeDesc())
                .isEqualTo("Contribution Notice");
        assertThat(response.getUpliftCote())
                .isEqualTo(1);
        assertThat(response.getReassessmentCoteId())
                .isEqualTo(1);
        assertThat(response.getTemplateDesc())
                .isEqualTo("No contributions required");
    }

    @ParameterizedTest()
    @MethodSource("contributionRequest")
    void givenAValidContributeRequest_whenCheckContribConditionIsInvoked_thenReturnYesContribution(
            ContributionRequestDTO request) {

        when(repository.getCoteInfo(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(TestModelDataBuilder.getCorrespondenceRuleAndTemplateInfo());

        ContributionResponseDTO response = contributionService.checkContribsCondition(request);

        assertThat(response.getDoContribs())
                .isEqualTo("Y");
        assertThat(response.getId())
                .isEqualTo(1);
        assertThat(response.getCorrespondenceType())
                .isEqualTo("CONTRIBUTION_NOTICE");
        assertThat(response.getCorrespondenceTypeDesc())
                .isEqualTo("Contribution Notice");
        assertThat(response.getUpliftCote())
                .isEqualTo(1);
        assertThat(response.getReassessmentCoteId())
                .isEqualTo(1);
        assertThat(response.getTemplateDesc())
                .isEqualTo("No contributions required");
    }

    @ParameterizedTest()
    @MethodSource("contributionRequest")
    void givenAValidContributeRequestAndEmptyCorrespondence_whenCheckContribConditionIsInvoked_thenReturnYesContribution(
            ContributionRequestDTO request) {

        when(repository.getCoteInfo(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(TestModelDataBuilder.getEmptyCorrespondenceRuleAndTemplateInfo());

        ContributionResponseDTO response = contributionService.checkContribsCondition(request);

        assertThat(response.getDoContribs())
                .isEqualTo("Y");
        assertThat(response.getId())
                .isEqualTo(1);
        assertThat(response.getCorrespondenceType())
                .isEmpty();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithFinReassessmentTrueAndContribCountAs1_thenReassessmentTrueIsReturned() {
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(1L);

        RepOrderDTO repOrderDTO = TestModelDataBuilder.getRepOrderDTO();
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, LAA_TRANSACTION_ID);

        assertThat(isReassessment)
                .isTrue();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithFinReassessmentFalseAndContribCountAs0_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(0L);
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, LAA_TRANSACTION_ID);

        assertThat(isReassessment)
                .isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithFinReassessmentFalseAndContribCountAs1_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(1L);
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, LAA_TRANSACTION_ID);

        assertThat(isReassessment)
                .isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithPassportReassessmentTrueAndContribCountAs1_thenReassessmentTrueIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setDateCreated(dateCreated);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(1L);
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, LAA_TRANSACTION_ID);

        assertThat(isReassessment)
                .isTrue();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithPassportReassessmentTrueAndContribCountAs0_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setDateCreated(dateCreated);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(0L);
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, LAA_TRANSACTION_ID);

        assertThat(isReassessment)
                .isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithPassportReassessmentFalseAndContribCountAs0_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("N");
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(0L);
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, LAA_TRANSACTION_ID);

        assertThat(isReassessment)
                .isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithPassportAssessmentAsNull_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setPassportAssessments(null);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(1L);
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, LAA_TRANSACTION_ID);

        assertThat(isReassessment)
                .isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithFinancialAssessmentAsNull_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setFinancialAssessments(null);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(1L);
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);

        assertThat(isReassessment)
                .isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithFinancialAssessmentDateCreatedAsNull_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getFinancialAssessments().get(0).setDateCreated(null);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(1L);
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, LAA_TRANSACTION_ID);

        assertThat(isReassessment)
                .isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithPassportAssessmentDateCreatedAsNull_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setDateCreated(null);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(1L);
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, LAA_TRANSACTION_ID);

        assertThat(isReassessment)
                .isFalse();
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
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(null);
        boolean hasCCOutcomeChanged = contributionService.hasCCOutcomeChanged(REP_ID, LAA_TRANSACTION_ID);

        assertThat(hasCCOutcomeChanged)
                .isFalse();

    }

    @Test
    void givenValidRepIdAndEmptyCCOutcome_whenHasCCOutcomeChangedIsInvoked_thenFalseIsReturn() {
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(REP_ID, LAA_TRANSACTION_ID))
                .thenReturn(Collections.emptyList());
        boolean hasCCOutcomeChanged = contributionService.hasCCOutcomeChanged(REP_ID, LAA_TRANSACTION_ID);

        assertThat(hasCCOutcomeChanged)
                .isFalse();

    }

    @Test
    void givenValidRepId_whenHasCCOutcomeChangedIsInvoked_thenTrueIsReturn() {
        List<RepOrderCCOutcomeDTO> outcomeList = new ArrayList<>();
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12346, CrownCourtOutcome.PART_CONVICTED.getCode()));
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12345, CrownCourtOutcome.ABANDONED.getCode()));
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(any(), any()))
                .thenReturn(outcomeList);
        boolean hasCCOutcomeChanged = contributionService.hasCCOutcomeChanged(REP_ID, LAA_TRANSACTION_ID);

        assertThat(hasCCOutcomeChanged)
                .isTrue();

    }

    @Test
    void givenValidRepIdAndEmptyOutcome_whenHasCCOutcomeChangedIsInvoked_thenFalseIsReturn() {
        List<RepOrderCCOutcomeDTO> outcomeList = new ArrayList<>();
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12346, CrownCourtOutcome.PART_CONVICTED.getCode()));
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12345, null));
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(any(), any()))
                .thenReturn(outcomeList);
        boolean hasCCOutcomeChanged = contributionService.hasCCOutcomeChanged(REP_ID, LAA_TRANSACTION_ID);

        assertThat(hasCCOutcomeChanged)
                .isFalse();

    }

    @Test
    void givenValidRepIdAndOutcomeIsAquitted_whenHasCCOutcomeChangedIsInvoked_thenFalseIsReturn() {
        List<RepOrderCCOutcomeDTO> outcomeList = new ArrayList<>();
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12346, CrownCourtOutcome.PART_CONVICTED.getCode()));
        outcomeList.add(TestModelDataBuilder.getRepOrderCCOutcomeDTO(12345, CrownCourtOutcome.AQUITTED.getCode()));
        when(maatCourtDataService.getRepOrderCCOutcomeByRepId(any(), any()))
                .thenReturn(outcomeList);
        boolean hasCCOutcomeChanged = contributionService.hasCCOutcomeChanged(REP_ID, LAA_TRANSACTION_ID);

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
    void givenValidRepIdEmptyContribution_whenHasContributionBeenSentIsInvoked_thenFalseIsReturn() {
        when(maatCourtDataService.findContribution(any(), any(), any()))
                .thenReturn(null);
        boolean hasContributionBeenSent = contributionService.hasContributionBeenSent(REP_ID, LAA_TRANSACTION_ID);

        assertThat(hasContributionBeenSent)
                .isFalse();
    }

    @Test
    void givenValidRepIdEmptyTransferStatus_whenHasContributionBeenSentIsInvoked_thenFalseIsReturn() {
        when(maatCourtDataService.findContribution(any(), any(), any()))
                .thenReturn(List.of(TestModelDataBuilder.buildContribution()));
        boolean hasContributionBeenSent = contributionService.hasContributionBeenSent(REP_ID, LAA_TRANSACTION_ID);

        assertThat(hasContributionBeenSent)
                .isFalse();
    }

    @Test
    void givenAInvalidTransferStatus_whenHasContributionBeenSentIsInvoked_thenFalseIsReturn() {
        Contribution contribution = TestModelDataBuilder.buildContribution();
        contribution.setTransferStatus(TransferStatus.MANUAL);
        contribution.setMonthlyContributions(BigDecimal.ONE);
        when(maatCourtDataService.findContribution(any(), any(), any()))
                .thenReturn(List.of(contribution));
        boolean hasContributionBeenSent = contributionService.hasContributionBeenSent(REP_ID, LAA_TRANSACTION_ID);

        assertThat(hasContributionBeenSent)
                .isFalse();
    }

    @Test
    void givenAIValidContribution_whenHasContributionBeenSentIsInvoked_thenTrueIsReturn() {
        Contribution contribution = TestModelDataBuilder.buildContribution();
        contribution.setTransferStatus(TransferStatus.SENT);
        contribution.setMonthlyContributions(BigDecimal.ONE);
        when(maatCourtDataService.findContribution(any(), any(), any()))
                .thenReturn(List.of(contribution));
        boolean hasContributionBeenSent = contributionService.hasContributionBeenSent(REP_ID, LAA_TRANSACTION_ID);

        assertThat(hasContributionBeenSent)
                .isTrue();
    }
}

