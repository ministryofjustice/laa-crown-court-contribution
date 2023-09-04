package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.MaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;
import static uk.gov.justice.laa.crime.contribution.util.DateUtil.getLocalDateString;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CalculateContributionResponseBuilder {

    public static MaatCalculateContributionResponse build(Contribution contribution) {
        MaatCalculateContributionResponse maatCalculateContributionResponse = new MaatCalculateContributionResponse();
        maatCalculateContributionResponse.withId(contribution.getId());
        maatCalculateContributionResponse.withApplId(contribution.getApplId());
        maatCalculateContributionResponse.withRepId(contribution.getRepId());
        maatCalculateContributionResponse.withContributionFileId(contribution.getContributionFileId());
        maatCalculateContributionResponse.withEffectiveDate(getLocalDateString(contribution.getEffectiveDate()));
        maatCalculateContributionResponse.withCalcDate(convertDateToDateTime(contribution.getCalcDate()));
        maatCalculateContributionResponse.withContributionCap(contribution.getContributionCap());
        maatCalculateContributionResponse.withMonthlyContributions(contribution.getMonthlyContributions());
        maatCalculateContributionResponse.withUpfrontContributions(contribution.getUpfrontContributions());
        maatCalculateContributionResponse.withUpliftApplied(contribution.getUpliftApplied());
        maatCalculateContributionResponse.withBasedOn(contribution.getBasedOn());
        maatCalculateContributionResponse.withTransferStatus(contribution.getTransferStatus());
        maatCalculateContributionResponse.withDateUpliftApplied(convertDateToDateTime(contribution.getDateUpliftApplied()));
        maatCalculateContributionResponse.withDateUpliftRemoved(convertDateToDateTime(contribution.getDateUpliftRemoved()));
        maatCalculateContributionResponse.withDateCreated(contribution.getDateCreated());
        maatCalculateContributionResponse.withUserCreated(contribution.getUserCreated());
        maatCalculateContributionResponse.withDateModified(contribution.getDateModified());
        maatCalculateContributionResponse.withUserModified(contribution.getUserModified());
        maatCalculateContributionResponse.withCreateContributionOrder(contribution.getCreateContributionOrder());
        maatCalculateContributionResponse.withCorrespondenceId(contribution.getCorrespondenceId());
        maatCalculateContributionResponse.withActive(contribution.getActive());
        maatCalculateContributionResponse.withReplacedDate(convertDateToDateTime(contribution.getReplacedDate()));
        maatCalculateContributionResponse.withLatest(contribution.getLatest());
        maatCalculateContributionResponse.withCcOutcomeCount(contribution.getCcOutcomeCount());
        maatCalculateContributionResponse.withSeHistoryId(contribution.getSeHistoryId());

        return maatCalculateContributionResponse;
    }

}
