package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentResponseDTO;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class ContributionServiceTest {
    @InjectMocks
    private ContributionService contributionService;

    private static Stream<Arguments> getAssessmentRequestForIojResult() {
        return Stream.of(
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, null,
                        Constants.PASS, Constants.FULL, Constants.PASS), Constants.PASS),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, null, null, Constants.PASS, null, Constants.PASS), Constants.PASS)
        );
    }

    private static Stream<Arguments> getAssessmentRequestForMeansResult() {
        return Stream.of(
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, null,
                        Constants.PASS, Constants.FULL, Constants.PASS), Constants.FULL),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, null, null,
                        Constants.PASS, null, Constants.PASS), Constants.INIT.concat(Constants.PASS)),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, Constants.PASS,
                        Constants.PASS, Constants.FULL, Constants.PASS), Constants.PASSPORT),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, Constants.FAIL,
                        Constants.PASS, Constants.FULL, Constants.PASS), Constants.FAILPORT),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        Constants.FAIL, Constants.FULL, Constants.PASS), Constants.PASS),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        Constants.FAIL, Constants.FAIL, Constants.PASS), Constants.PASS),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        Constants.FAIL, Constants.FAIL, Constants.FAIL), Constants.FAIL),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        Constants.FAIL, Constants.FAIL, null), Constants.FAIL),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        Constants.FULL, Constants.FAIL, Constants.FAIL), Constants.FAIL),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        Constants.FULL, Constants.FAIL, Constants.FAIL), Constants.FAIL),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        Constants.HARDSHIP_APPLICATION, Constants.FAIL, null), Constants.FAIL)

        );
    }

    private static Stream<Arguments> getAssessmentRequestForNullMeansResult() {
        return Stream.of(
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        Constants.INIT, Constants.FAIL, Constants.FAIL), Constants.FAIL),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        Constants.INIT, Constants.INIT, Constants.INIT)),
                Arguments.of(new AssessmentRequestDTO(Constants.PASS, Constants.PASS, TestModelDataBuilder.PASSPORT_RESULT_FAIL_CONTINUE,
                        Constants.INIT, Constants.INIT, null))
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

}