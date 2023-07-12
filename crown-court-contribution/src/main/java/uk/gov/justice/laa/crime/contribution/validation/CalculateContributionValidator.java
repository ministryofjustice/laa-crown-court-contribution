package uk.gov.justice.laa.crime.contribution.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.exeption.ValidationException;
import uk.gov.justice.laa.crime.contribution.model.CalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class CalculateContributionValidator {

    public Optional<Void> validate(CalculateContributionRequest calculateContributionRequest) {
        log.debug("Performing validation against calculate contributions request");
        if (calculateContributionRequest != null) {
            if (calculateContributionRequest.getLastOutcome() != null
                    && calculateContributionRequest.getLastOutcome().getDateSet() != null
                    && calculateContributionRequest.getLastOutcome().getDateSet().isAfter(LocalDateTime.now())) {
                throw new ValidationException("The dateSet for lastOutcome is invalid");
            }
        }

        boolean isNoCompletedAssessment = calculateContributionRequest.getAssessments()
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
