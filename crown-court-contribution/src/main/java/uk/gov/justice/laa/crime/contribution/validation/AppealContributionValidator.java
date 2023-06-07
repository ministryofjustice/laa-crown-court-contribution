package uk.gov.justice.laa.crime.contribution.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.exeption.ValidationException;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class AppealContributionValidator {

    public Optional<Void> validate(AppealContributionRequest appealContributionRequest) {
        log.debug("Performing validation against calculate appeal contributions request");
        if (appealContributionRequest.getLastOutcome().getDateSet().isAfter(LocalDateTime.now())) {
            throw new ValidationException("The dateSet for lastOutcome is invalid");
        }

        boolean isNoCompletedAssessment = appealContributionRequest.getAssessments()
                .stream()
                .filter(assessment -> assessment.getStatus() == AssessmentStatus.COMPLETE)
                .toList()
                .isEmpty();
        if (isNoCompletedAssessment) {
            throw new ValidationException("There must be at least one COMPLETE assessment");
        }

        return Optional.empty();
    }
}
