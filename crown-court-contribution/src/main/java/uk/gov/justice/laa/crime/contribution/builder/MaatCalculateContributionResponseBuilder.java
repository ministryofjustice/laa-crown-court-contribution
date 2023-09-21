package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiMaatCalculateContributionResponse;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;
import static uk.gov.justice.laa.crime.contribution.util.DateUtil.getLocalDateString;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaatCalculateContributionResponseBuilder {

    public static ApiMaatCalculateContributionResponse build(Contribution contribution) {
        return new ApiMaatCalculateContributionResponse()
                .withId(contribution.getId())
                .withApplId(contribution.getApplId())
                .withRepId(contribution.getRepId())
                .withContributionFileId(contribution.getContributionFileId())
                .withEffectiveDate(getLocalDateString(contribution.getEffectiveDate()))
                .withCalcDate(convertDateToDateTime(contribution.getCalcDate()))
                .withContributionCap(contribution.getContributionCap())
                .withMonthlyContributions(contribution.getMonthlyContributions())
                .withUpfrontContributions(contribution.getUpfrontContributions())
                .withUpliftApplied(contribution.getUpliftApplied())
                .withBasedOn(contribution.getBasedOn())
                .withTransferStatus(contribution.getTransferStatus())
                .withDateUpliftApplied(convertDateToDateTime(contribution.getDateUpliftApplied()))
                .withDateUpliftRemoved(convertDateToDateTime(contribution.getDateUpliftRemoved()))
                .withDateCreated(contribution.getDateCreated())
                .withUserCreated(contribution.getUserCreated())
                .withDateModified(contribution.getDateModified())
                .withUserModified(contribution.getUserModified())
                .withCreateContributionOrder(contribution.getCreateContributionOrder())
                .withCorrespondenceId(contribution.getCorrespondenceId())
                .withActive(contribution.getActive())
                .withReplacedDate(convertDateToDateTime(contribution.getReplacedDate()))
                .withLatest(contribution.getLatest())
                .withCcOutcomeCount(contribution.getCcOutcomeCount())
                .withSeHistoryId(contribution.getSeHistoryId());
    }
}
