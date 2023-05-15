package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentResponseDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class ContributionServiceTest {

    @InjectMocks
    private ContributionService contributionService;

    @Test
    void giveAEmptyPassportResult_whenGetAssessmentResultIsInvoked_thenReturnCorrectResponse() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(null);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getIojResult()).isEqualTo(Constants.PASS);
        assertThat(response.getMeansResult()).isEqualTo(Constants.FULL);

    }

    @Test
    void giveAEmptyPassportAndFullResult_whenGetAssessmentResultIsInvoked_thenReturnCorrectResponse() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(null);
        request.setFullResult(null);
        request.setDecisionResult(null);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getIojResult()).isEqualTo(Constants.PASS);
        assertThat(response.getMeansResult()).isEqualTo(Constants.INIT.concat(Constants.PASS));

    }

    @Test
    void giveAEmptyPassportAndInitResult_whenGetAssessmentResultIsInvoked_thenReturnCorrectResponse() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(null);
        request.setFullResult(null);
        request.setDecisionResult(null);
        request.setInitResult(null);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getIojResult()).isEqualTo(Constants.PASS);
        assertThat(response.getMeansResult()).isEqualTo(Constants.NONE);

    }

    @Test
    void givenAPassportResultAsPass_whenGetAssessmentResultIsInvoked_thenReturnPassportMeansResult() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.PASSPORT);
    }

    @Test
    void givenAPassportResultAsFail_whenGetAssessmentResultIsInvoked_thenReturnFailport() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(Constants.FAIL);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.FAILPORT);
    }

    @Test
    void givenAFailContinuePassportResultAndPassInitResult_whenGetAssessmentResultIsInvoked_thenReturnPass() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.PASS);
    }

    @Test
    void givenAFailContinuePassportResultAndPassFullResult_whenGetAssessmentResultIsInvoked_thenReturnPass() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.FAIL);
        request.setFullResult(Constants.PASS);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.PASS);
    }

    @Test
    void givenAFailContinuePassportAndPassHardShip_whenGetAssessmentResultIsInvoked_thenReturnPass() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.FAIL);
        request.setFullResult(Constants.FAIL);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.PASS);
    }

    @Test
    void givenAFailContinuePassportAndInitResultIsPass_whenGetAssessmentResultIsInvoked_thenReturnFail() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.INIT);
        request.setFullResult(Constants.FAIL);
        request.setHardshipResult(Constants.FAIL);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isNull();
    }

    @Test
    void givenAFailContinuePassportAndFailResult_whenGetAssessmentResultIsInvoked_thenReturnFail() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.FAIL);
        request.setFullResult(Constants.FAIL);
        request.setHardshipResult(Constants.FAIL);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.FAIL);
    }

    @Test
    void givenAFailContinuePassportAndEmptyHardship_whenGetAssessmentResultIsInvoked_thenReturnFail() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.FAIL);
        request.setFullResult(Constants.FAIL);
        request.setHardshipResult(null);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.FAIL);
    }

    @Test
    void givenAFailContinuePassportAndFullInitResult_whenGetAssessmentResultIsInvoked_thenReturnFail() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.FULL);
        request.setFullResult(Constants.FAIL);
        request.setHardshipResult(Constants.FAIL);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.FAIL);
    }

    @Test
    void givenAFailContinuePassportAndNullHardship_whenGetAssessmentResultIsInvoked_thenReturnFail() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.FULL);
        request.setFullResult(Constants.FAIL);
        request.setHardshipResult(null);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.FAIL);
    }

    @Test
    void givenAInitResultIsHardshipAndFailResult_whenGetAssessmentResultIsInvoked_thenReturnFail() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.HARDSHIP_APPLICATION);
        request.setFullResult(Constants.FAIL);
        request.setHardshipResult(Constants.FAIL);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.FAIL);
    }

    @Test
    void givenAInitResultIsHardshipAndEmptyHardshipResult_whenGetAssessmentResultIsInvoked_thenReturnFail() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.HARDSHIP_APPLICATION);
        request.setFullResult(Constants.FAIL);
        request.setHardshipResult(null);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isEqualTo(Constants.FAIL);
    }

    @Test
    void givenAFailContinuePassportAndFullResultIsFull_whenGetAssessmentResultIsInvoked_thenEmptyIsReturn() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.HARDSHIP_APPLICATION);
        request.setFullResult(Constants.FULL);
        request.setHardshipResult(Constants.TEMP);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isNull();
    }

    @Test
    void givenAFailContinuePassportAndAssessmentResultIsPass_whenGetAssessmentResultIsInvoked_thenEmptyIsReturn() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.INIT);
        request.setFullResult(Constants.INIT);
        request.setHardshipResult(Constants.INIT);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isNull();
    }

    @Test
    void givenAFailContinuePassportAndEmptyHardship_whenGetAssessmentResultIsInvoked_thenEmptyIsReturn() {
        AssessmentRequestDTO request = TestModelDataBuilder.getAssessmentRequestDTO();
        request.setPassportResult(TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE);
        request.setInitResult(Constants.INIT);
        request.setFullResult(Constants.INIT);
        request.setHardshipResult(null);
        AssessmentResponseDTO response = contributionService.getAssessmentResult(request);
        assertThat(response.getMeansResult()).isNull();
    }
}