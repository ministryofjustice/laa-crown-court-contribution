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
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentResponseDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.PassportAssessmentResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.commons.common.Constants.LAA_TRANSACTION_ID;
import static uk.gov.justice.laa.crime.contribution.common.Constants.*;
import static uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder.REP_ID;
import static uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder.getRepOrderDTO;

@ExtendWith(MockitoExtension.class)
class ContributionServiceTest {
    private static final LocalDateTime dateCreated = LocalDateTime.parse("2023-07-10T15:01:25");
    @InjectMocks
    private ContributionService contributionService;
    @Mock
    private MaatCourtDataService maatCourtDataService;

    private static Stream<Arguments> getAssessmentRequestForIojResult() {
        return Stream.of(
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, null,
                        PASS, FULL, PASS), PASS),
                Arguments.of(new AssessmentRequestDTO(PASS, null, null, PASS, null, PASS), PASS)
        );
    }

    private static Stream<Arguments> getAssessmentRequestForMeansResult() {
        return Stream.of(
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, null,
                        PASS, FULL, PASS), FULL),
                Arguments.of(new AssessmentRequestDTO(PASS, null, null,
                        PASS, null, PASS), INIT.concat(PASS)),
                Arguments.of(new AssessmentRequestDTO(PASS, null, null,
                        null, null, PASS), NONE),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, PASS,
                        PASS, FULL, PASS), PASSPORT),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, FAIL,
                        PASS, FULL, PASS), FAILPORT),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        PASS, PASS, PASS), PASS),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        FAIL, PASS, PASS), PASS),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        FAIL, FAIL, PASS), PASS),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        FAIL, FAIL, FAIL), FAIL),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        FAIL, FAIL, null), FAIL),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        FULL, FAIL, FAIL), FAIL),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        FULL, FAIL, null), FAIL),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        HARDSHIP_APPLICATION, FAIL, FAIL), FAIL),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        HARDSHIP_APPLICATION, FAIL, null), FAIL)

        );
    }

    private static Stream<Arguments> getAssessmentRequestForNullMeansResult() {
        return Stream.of(
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        INIT, FAIL, FAIL)),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        HARDSHIP_APPLICATION, FAIL, TEMP)),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        INIT, INIT, INIT)),
                Arguments.of(new AssessmentRequestDTO(PASS, PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        INIT, INIT, null))
        );
    }

    @ParameterizedTest()
    @MethodSource("getAssessmentRequestForIojResult")
    void givenAValidAssessmentRequest_whenGetAssessmentResultIsInvoked_thenReturnCorrectIojResultResponse(AssessmentRequestDTO request, String expectedResult) {
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getIojResult()).isEqualTo(expectedResult);
    }

    @ParameterizedTest()
    @MethodSource("getAssessmentRequestForMeansResult")
    void givenAValidAssessmentRequest_whenGetAssessmentResultIsInvoked_thenReturnCorrectMeansResultResponse(AssessmentRequestDTO request, String expectedResult) {
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(expectedResult);
    }

    @ParameterizedTest()
    @MethodSource("getAssessmentRequestForNullMeansResult")
    void givenAValidAssessmentRequest_whenGetAssessmentResultIsInvoked_thenReturnEmptyMeansResult(AssessmentRequestDTO request) {
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isNull();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithFinReassessmentTrueAndContribCountAs1_thenReassessmentTrueIsReturned() {
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID)).thenReturn(1L);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(getRepOrderDTO(REP_ID));

        boolean isReassessment = contributionService.checkReassessment(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID);
        assertThat(isReassessment).isTrue();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithFinReassessmentFalseAndContribCountAs0_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID)).thenReturn(0L);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);

        boolean isReassessment = contributionService.checkReassessment(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID);
        assertThat(isReassessment).isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithFinReassessmentFalseAndContribCountAs1_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getFinancialAssessments().get(0).setReplaced("N");
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID)).thenReturn(1L);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);

        boolean isReassessment = contributionService.checkReassessment(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID);
        assertThat(isReassessment).isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithPassportReassessmentTrueAndContribCountAs1_thenReassessmentTrueIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setDateCreated(dateCreated);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID)).thenReturn(1L);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);

        boolean isReassessment = contributionService.checkReassessment(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID);
        assertThat(isReassessment).isTrue();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithPassportReassessmentTrueAndContribCountAs0_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setDateCreated(dateCreated);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID)).thenReturn(0L);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);

        boolean isReassessment = contributionService.checkReassessment(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID);
        assertThat(isReassessment).isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithPassportReassessmentFalseAndContribCountAs0_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("N");
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID)).thenReturn(0L);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);

        boolean isReassessment = contributionService.checkReassessment(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID);
        assertThat(isReassessment).isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithPassportAssessmentAsNull_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setPassportAssessments(null);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID)).thenReturn(1L);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);

        boolean isReassessment = contributionService.checkReassessment(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID);
        assertThat(isReassessment).isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithFinancialAssessmentAsNull_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setFinancialAssessments(null);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID)).thenReturn(1L);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);

        boolean isReassessment = contributionService.checkReassessment(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID);
        assertThat(isReassessment).isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithFinancialAssessmentDateCreatedAsNull_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getFinancialAssessments().get(0).setDateCreated(null);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID)).thenReturn(1L);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);

        boolean isReassessment = contributionService.checkReassessment(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID);
        assertThat(isReassessment).isFalse();
    }

    @Test
    void givenAValidRepId_whenCheckReassessmentIsInvokedWithPassportAssessmentDateCreatedAsNull_thenReassessmentFalseIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setDateCreated(null);
        when(maatCourtDataService.getContributionCount(REP_ID, LAA_TRANSACTION_ID)).thenReturn(1L);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);

        boolean isReassessment = contributionService.checkReassessment(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getContributionCount(REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataService).getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID);
        assertThat(isReassessment).isFalse();
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
    void givenRepOrderWithReplacedPassportAssessment_whenGetPassportAssessmentResultIsInvoked_thenValidResultIsReturned() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.getPassportAssessments().get(0).setReplaced("Y");
        repOrderDTO.getPassportAssessments().get(0).setResult(PassportAssessmentResult.PASS.getResult());

        assertThat(ContributionService.getPassportAssessmentResult(repOrderDTO)).isEqualTo(PassportAssessmentResult.PASS.getResult());
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

        assertThat(ContributionService.getInitialAssessmentResult(repOrderDTO)).isEqualTo(InitAssessmentResult.PASS.getResult());
    }

    @Test
    void givenInvalidRepId_whenHasMessageOutcomeChangedIsInvoked_thenFalseIsReturn() {
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(null);
        boolean hasMsgOutcomeChanged = contributionService.hasMessageOutcomeChanged(REP_ID, LAA_TRANSACTION_ID, "outcome");
        assertThat(hasMsgOutcomeChanged).isFalse();

    }

    @Test
    void givenValidRepIdAndOutcomeIsNotMatch_whenHasMessageOutcomeChangedIsInvoked_thenFalseIsReturn() {
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(getRepOrderDTO(REP_ID));
        boolean hasMsgOutcomeChanged = contributionService.hasMessageOutcomeChanged(REP_ID, LAA_TRANSACTION_ID, "outcomeMessage");
        assertThat(hasMsgOutcomeChanged).isFalse();

    }

    @Test
    void givenValidRepIdAndOutcomeIsNull_whenHasMessageOutcomeChangedIsInvoked_thenFalseIsReturn() {
        RepOrderDTO repOrderDTO = getRepOrderDTO(REP_ID);
        repOrderDTO.setMagsOutcome(null);
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(repOrderDTO);
        boolean hasMsgOutcomeChanged = contributionService.hasMessageOutcomeChanged(REP_ID, LAA_TRANSACTION_ID, "outcomeMessage");
        assertThat(hasMsgOutcomeChanged).isFalse();

    }

    @Test
    void givenValidRepId_whenHasMessageOutcomeChangedIsInvoked_thenTrueIsReturn() {
        when(maatCourtDataService.getRepOrderByRepId(REP_ID, LAA_TRANSACTION_ID)).thenReturn(getRepOrderDTO(REP_ID));
        boolean hasMsgOutcomeChanged = contributionService.hasMessageOutcomeChanged(REP_ID, LAA_TRANSACTION_ID, "outcome");
        assertThat(hasMsgOutcomeChanged).isTrue();

    }

}
