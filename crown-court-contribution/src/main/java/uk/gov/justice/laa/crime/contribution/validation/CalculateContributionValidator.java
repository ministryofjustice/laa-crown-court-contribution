package uk.gov.justice.laa.crime.contribution.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;

import java.util.Optional;

@Slf4j
@Component
public class CalculateContributionValidator {

    public Optional<Void> validate(ApiMaatCalculateContributionRequest maatCalculateContributionRequest) {
        log.debug("Performing validation against calculate contributions request");
        boolean isNoCompletedAssessment = maatCalculateContributionRequest.getAssessments()
                .stream()
                .filter(assessment -> assessment.getStatus() == CurrentStatus.COMPLETE)
                .toList()
                .isEmpty();
        if (isNoCompletedAssessment) {
            throw new ValidationException("There must be at least one COMPLETE assessment");
        }
        return Optional.empty();
    }
}
