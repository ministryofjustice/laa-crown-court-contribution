package uk.gov.justice.laa.crime.contribution.builder;

import static uk.gov.justice.laa.crime.contribution.common.Constants.FULL;
import static uk.gov.justice.laa.crime.contribution.common.Constants.PASS;

import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionRequestDTO;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class AssessmentRequestDTOBuilderTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenAValidContributionRequest_whenBuildIsInvoked_thenReturnAssessmentRequest() {
        ContributionRequestDTO contributionRequestDTO = TestModelDataBuilder.getContributionRequestDTO();
        AssessmentRequestDTO assessmentRequest = AssessmentRequestDTOBuilder.build(contributionRequestDTO);
        softly.assertThat(assessmentRequest.getIojResult()).isEqualTo(PASS);
        softly.assertThat(assessmentRequest.getDecisionResult()).isEqualTo(PASS);
        softly.assertThat(assessmentRequest.getInitResult()).isEqualTo(PASS);
        softly.assertThat(assessmentRequest.getFullResult()).isEqualTo(FULL);
        softly.assertThat(assessmentRequest.getHardshipResult()).isEqualTo(PASS);
        softly.assertThat(assessmentRequest.getPassportResult()).isEqualTo(PASS);
        softly.assertAll();
    }
}
