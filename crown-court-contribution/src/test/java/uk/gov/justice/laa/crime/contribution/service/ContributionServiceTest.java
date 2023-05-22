package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static uk.gov.justice.laa.crime.contribution.common.Constants.*;
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

}