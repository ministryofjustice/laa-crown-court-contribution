package uk.gov.justice.laa.crime.contribution.validation;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.exeption.ValidationException;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AppealContributionValidatorTest {

    private static CalculateContributionValidator calculateContributionValidator = new CalculateContributionValidator();

    @Test
    void givenValidRequest_whenValidateIsInvoked_thenNoExceptionIsRaised() {
        CalculateContributionRequest calculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();

        assertThat(calculateContributionValidator.validate(calculateContributionRequest)).isEmpty();
    }

    @Test
    void givenEmptyOutcome_whenValidateIsInvoked_thenNoExceptionIsRaised() {
        CalculateContributionRequest calculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        calculateContributionRequest.setLastOutcome(null);

        assertThat(calculateContributionValidator.validate(calculateContributionRequest)).isEmpty();
    }

    @Test
    void givenEmptyOutDateSet_whenValidateIsInvoked_thenNoExceptionIsRaised() {
        CalculateContributionRequest calculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        calculateContributionRequest.getLastOutcome().setDateSet(null);

        assertThat(calculateContributionValidator.validate(calculateContributionRequest)).isEmpty();
    }

    @Test
    void givenIncorrectOutcomeDateSet_whenValidateIsInvoked_thenValidationExceptionIsRaised() {
        CalculateContributionRequest calculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        LastOutcome__1 lastOutcome = TestModelDataBuilder.buildLastOutcome_1();
        lastOutcome.setDateSet(LocalDateTime.now().plusDays(1));
        calculateContributionRequest.setLastOutcome(lastOutcome);

        assertThatThrownBy(() -> calculateContributionValidator.validate(calculateContributionRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("The dateSet for lastOutcome is invalid");
    }

    @Test
    void givenNoCompleteAssessment_whenValidateIsInvoked_thenValidationExceptionIsRaised() {
        CalculateContributionRequest calculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        Assessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.withStatus(AssessmentStatus.IN_PROGRESS);
        calculateContributionRequest.setAssessments(List.of(assessment));

        assertThatThrownBy(() -> calculateContributionValidator.validate(calculateContributionRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("There must be at least one COMPLETE assessment");
    }
}
