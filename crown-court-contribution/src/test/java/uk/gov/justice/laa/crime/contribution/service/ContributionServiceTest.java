package uk.gov.justice.laa.crime.contribution.service;

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
import uk.gov.justice.laa.crime.contribution.dto.ContributionRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.repository.CorrespondenceRuleRepository;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.contribution.common.Constants.*;

@ExtendWith(MockitoExtension.class)
class ContributionServiceTest {
    private static final String CONTRIBUTION_NO = "N";
    private static final String CONTRIBUTION_YES = "Y";
    @InjectMocks
    private ContributionService contributionService;
    @Mock
    private CorrespondenceRuleRepository repository;

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

    private static Stream<Arguments> inValidContributionRequest() {
        return Stream.of(
                Arguments.of(new ContributionRequestDTO(PASS, PASS, CaseType.APPEAL_CC, null, FAIL, FAIL,
                        PASS, FAIL, FAIL, PASS, 0, CONTRIBUTION_YES, PASS)),
                Arguments.of(new ContributionRequestDTO(PASS, PASS, CaseType.INDICTABLE, null, PASS, PASS,
                        PASS, FAIL, FULL, PASS, 0, CONTRIBUTION_NO, PASS))
        );
    }

    private static Stream<Arguments> NoContributionRequest() {
        return Stream.of(
                Arguments.of(new ContributionRequestDTO(PASS, PASS, CaseType.INDICTABLE, null, PASS, PASS,
                        PASS, FAIL, FAIL, PASS, 0, CONTRIBUTION_YES, PASS)),

                Arguments.of(new ContributionRequestDTO(PASS, PASS, CaseType.EITHER_WAY, null, PASS, PASS,
                        PASS, FAIL, FAIL, PASS, 0, CONTRIBUTION_YES, PASS)),

                Arguments.of(new ContributionRequestDTO(PASS, PASS, CaseType.CC_ALREADY, null, PASS, PASS,
                        PASS, FAIL, FAIL, PASS, 0, CONTRIBUTION_YES, PASS)),

                Arguments.of(new ContributionRequestDTO(PASS, PASS, CaseType.COMMITAL, LocalDate.now(), PASS, PASS,
                        PASS, FAIL, FAIL, PASS, 0, CONTRIBUTION_YES, PASS)),

                Arguments.of(new ContributionRequestDTO(PASS, PASS, CaseType.APPEAL_CC, null, PASS, PASS,
                        PASS, FAIL, FAIL, PASS, 0, CONTRIBUTION_YES, PASS)),

                Arguments.of(new ContributionRequestDTO(PASS, PASS, CaseType.APPEAL_CC, null, PASS, PASS,
                        PASS, FAIL, FAIL, PASS, 0, CONTRIBUTION_YES, PASS))

        );
    }

    private static Stream<Arguments> YesContributionRequest() {
        return Stream.of(
                Arguments.of(new ContributionRequestDTO(PASS, PASS, CaseType.INDICTABLE, null, PASS, PASS,
                        PASS, FAIL, "INEL", PASS, 1, CONTRIBUTION_YES, PASS))

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

    @ParameterizedTest()
    @MethodSource("inValidContributionRequest")
    void givenAInvalidContributeRequest_whenCheckContribConditionIsInvoked_thenReturnNull(ContributionRequestDTO request) {
        ContributionResponseDTO response = contributionService.checkContribsCondition(request);
        assertThat(response).isNull();
    }

    @ParameterizedTest()
    @MethodSource("NoContributionRequest")
    void givenAValidContributeRequest_whenCheckContribConditionIsInvoked_thenReturnNoContribution(ContributionRequestDTO request) {
        when(repository.getCoteInfo(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(TestModelDataBuilder.getCorrespondenceRuleAndTemplateInfo());
        ContributionResponseDTO response = contributionService.checkContribsCondition(request);
        assertThat(response.getCalcContribution()).isEqualTo(CONTRIBUTION_NO);
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getCorrespondenceType()).isEqualTo("CONTRIBUTION_NOTICE");
        assertThat(response.getCorrespondenceTypeDesc()).isEqualTo("Contribution Notice");
        assertThat(response.getUpliftCote()).isEqualTo(1);
        assertThat(response.getReassessmentCoteId()).isEqualTo(1);
        assertThat(response.getTemplateDesc()).isEqualTo("No contributions required");
    }

    @ParameterizedTest()
    @MethodSource("YesContributionRequest")
    void givenAValidContributeRequest_whenCheckContribConditionIsInvoked_thenReturnYesContribution(ContributionRequestDTO request) {
        when(repository.getCoteInfo(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(TestModelDataBuilder.getCorrespondenceRuleAndTemplateInfo());
        ContributionResponseDTO response = contributionService.checkContribsCondition(request);
        assertThat(response.getDoContribs()).isEqualTo('Y');
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getCorrespondenceType()).isEqualTo("CONTRIBUTION_NOTICE");
        assertThat(response.getCorrespondenceTypeDesc()).isEqualTo("Contribution Notice");
        assertThat(response.getUpliftCote()).isEqualTo(1);
        assertThat(response.getReassessmentCoteId()).isEqualTo(1);
        assertThat(response.getTemplateDesc()).isEqualTo("No contributions required");
    }
}