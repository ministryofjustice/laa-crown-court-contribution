package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.CalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;
import static uk.gov.justice.laa.crime.contribution.util.DateUtil.getLocalDateString;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CalculateContributionResponseBuilder {

    public static CalculateContributionResponse build(Contribution contribution) {
        CalculateContributionResponse calculateContributionResponse = new CalculateContributionResponse();
        calculateContributionResponse.withId(contribution.getId());
        calculateContributionResponse.withApplId(contribution.getApplId());
        calculateContributionResponse.withRepId(contribution.getRepId());
        calculateContributionResponse.withContributionFileId(contribution.getContributionFileId());
        calculateContributionResponse.withEffectiveDate(getLocalDateString(contribution.getEffectiveDate()));
        calculateContributionResponse.withCalcDate(convertDateToDateTime(contribution.getCalcDate()));
        calculateContributionResponse.withContributionCap(contribution.getContributionCap());
        calculateContributionResponse.withMonthlyContributions(contribution.getMonthlyContributions());
        calculateContributionResponse.withUpfrontContributions(contribution.getUpfrontContributions());
        calculateContributionResponse.withUpliftApplied(contribution.getUpliftApplied());
        calculateContributionResponse.withBasedOn(contribution.getBasedOn());
        calculateContributionResponse.withTransferStatus(contribution.getTransferStatus());
        calculateContributionResponse.withDateUpliftApplied(convertDateToDateTime(contribution.getDateUpliftApplied()));
        calculateContributionResponse.withDateUpliftRemoved(convertDateToDateTime(contribution.getDateUpliftRemoved()));
        calculateContributionResponse.withDateCreated(contribution.getDateCreated());
        calculateContributionResponse.withUserCreated(contribution.getUserCreated());
        calculateContributionResponse.withDateModified(contribution.getDateModified());
        calculateContributionResponse.withUserModified(contribution.getUserModified());
        calculateContributionResponse.withCreateContributionOrder(contribution.getCreateContributionOrder());
        calculateContributionResponse.withCorrespondenceId(contribution.getCorrespondenceId());
        calculateContributionResponse.withActive(contribution.getActive());
        calculateContributionResponse.withReplacedDate(convertDateToDateTime(contribution.getReplacedDate()));
        calculateContributionResponse.withLatest(contribution.getLatest());
        calculateContributionResponse.withCcOutcomeCount(contribution.getCcOutcomeCount());
        calculateContributionResponse.withSeHistoryId(contribution.getSeHistoryId());

        return calculateContributionResponse;
    }

}
