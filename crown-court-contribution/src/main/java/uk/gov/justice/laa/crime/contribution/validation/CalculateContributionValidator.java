package uk.gov.justice.laa.crime.contribution.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.exeption.ValidationException;
import uk.gov.justice.laa.crime.contribution.model.MaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class CalculateContributionValidator {

    public Optional<Void> validate(MaatCalculateContributionRequest maatCalculateContributionRequest) {
        log.debug("Performing validation against calculate contributions request");
        if (maatCalculateContributionRequest != null
                    && maatCalculateContributionRequest.getLastOutcome() != null
                    && maatCalculateContributionRequest.getLastOutcome().getDateSet() != null
                    && maatCalculateContributionRequest.getLastOutcome().getDateSet().isAfter(LocalDateTime.now())) {
                throw new ValidationException("The dateSet for lastOutcome is invalid");
            }

        boolean isNoCompletedAssessment = maatCalculateContributionRequest.getAssessments()
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
