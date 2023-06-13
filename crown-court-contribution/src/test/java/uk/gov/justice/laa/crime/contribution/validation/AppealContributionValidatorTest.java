package uk.gov.justice.laa.crime.contribution.validation;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.exeption.ValidationException;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.Assessment;
import uk.gov.justice.laa.crime.contribution.model.LastOutcome;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AppealContributionValidatorTest {

    private static AppealContributionValidator appealContributionValidator = new AppealContributionValidator();

    @Test
    void givenValidRequest_whenValidateIsInvoked_thenNoExceptionIsRaised() {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();

        assertThat(appealContributionValidator.validate(appealContributionRequest)).isEmpty();
    }

    @Test
    void givenIncorrectOutcomeDateSet_whenValidateIsInvoked_thenValidationExceptionIsRaised() {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        LastOutcome lastOutcome = TestModelDataBuilder.buildLastOutcome();
        lastOutcome.setDateSet(LocalDateTime.now().plusDays(1));
        appealContributionRequest.setLastOutcome(lastOutcome);

        assertThatThrownBy(() -> appealContributionValidator.validate(appealContributionRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("The dateSet for lastOutcome is invalid");
    }

    @Test
    void givenNoCompleteAssessment_whenValidateIsInvoked_thenValidationExceptionIsRaised() {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        Assessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.withStatus(AssessmentStatus.IN_PROGRESS);
        appealContributionRequest.setAssessments(List.of(assessment));

        assertThatThrownBy(() -> appealContributionValidator.validate(appealContributionRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("There must be at least one COMPLETE assessment");
    }
}
