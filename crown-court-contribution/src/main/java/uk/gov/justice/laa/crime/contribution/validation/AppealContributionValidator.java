package uk.gov.justice.laa.crime.contribution.validation;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;

import java.util.Optional;

@Component
public class AppealContributionValidator {

    public Optional<Void> validate(AppealContributionRequest appealContributionRequest) {
        // TODO: applId, just check this isn't null, is this done as part of @Valid

        // TODO: repId, check the db for this?
        // TODO: caseType, check is one of the valid values for enum, think this is done by @Valid
        // TODO: appealType, check is one of the valid values for enum, think this is done by @Valid
        // TODO: monthlyContributions, should this be > 0? Double check
        // TODO: userCreated, do we need to check this is a recognised user???
        // TODO: lastOutcome, check isn't null and there is an outcome and dateSet is valid date
        // TODO: assessments, assume this is done by @Valid min 1 item in list and entries match declared types???
        return Optional.empty();
    }
}
