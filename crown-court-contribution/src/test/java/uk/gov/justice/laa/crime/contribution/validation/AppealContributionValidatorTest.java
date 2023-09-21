package uk.gov.justice.laa.crime.contribution.validation;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.exeption.ValidationException;
import uk.gov.justice.laa.crime.contribution.model.common.Assessment;
import uk.gov.justice.laa.crime.contribution.model.maat_api.LastOutcome;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AppealContributionValidatorTest {

    private static final CalculateContributionValidator calculateContributionValidator = new CalculateContributionValidator();

    @Test
    void givenValidRequest_whenValidateIsInvoked_thenNoExceptionIsRaised() {
        ApiMaatCalculateContributionRequest maatCalculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();

        assertThat(calculateContributionValidator.validate(maatCalculateContributionRequest)).isEmpty();
    }

    @Test
    void givenEmptyOutDateSet_whenValidateIsInvoked_thenNoExceptionIsRaised() {
        ApiMaatCalculateContributionRequest maatCalculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        maatCalculateContributionRequest.getLastOutcome().setDateSet(null);

        assertThat(calculateContributionValidator.validate(maatCalculateContributionRequest)).isEmpty();
    }

    @Test
    void givenIncorrectOutcomeDateSet_whenValidateIsInvoked_thenValidationExceptionIsRaised() {
        ApiMaatCalculateContributionRequest maatCalculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        LastOutcome lastOutcome = TestModelDataBuilder.buildLastOutcome();
        lastOutcome.setDateSet(LocalDateTime.now().plusDays(1));
        maatCalculateContributionRequest.setLastOutcome(lastOutcome);

        assertThatThrownBy(() -> calculateContributionValidator.validate(maatCalculateContributionRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("The dateSet for lastOutcome is invalid");
    }

    @Test
    void givenNoCompleteAssessment_whenValidateIsInvoked_thenValidationExceptionIsRaised() {
        ApiMaatCalculateContributionRequest maatCalculateContributionRequest = TestModelDataBuilder.buildCalculateContributionRequest();
        Assessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.withStatus(AssessmentStatus.IN_PROGRESS);
        maatCalculateContributionRequest.setAssessments(List.of(assessment));

        assertThatThrownBy(() -> calculateContributionValidator.validate(maatCalculateContributionRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("There must be at least one COMPLETE assessment");
    }
}
