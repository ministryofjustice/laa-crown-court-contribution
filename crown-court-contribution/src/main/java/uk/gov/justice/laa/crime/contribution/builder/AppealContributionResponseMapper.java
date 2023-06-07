package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.TransferStatus;


@Component
@AllArgsConstructor
public class AppealContributionResponseBuilder {

    public AppealContributionResponse build(Contribution contribution) {
        return new AppealContributionResponse()
                .withId(contribution.getId())
                .withApplId(contribution.getApplId())
                .withRepId(contribution.getRepId())
                .withContributionFileId(contribution.getContributionFileId())
                .withEffectiveDate(contribution.getEffectiveDate().atStartOfDay())
                .withCalcDate(contribution.getCalcDate().atStartOfDay())
                .withContributionCap(contribution.getContributionCap())
                .withMonthlyContributions(contribution.getMonthlyContributions())
                .withUpfrontContributions(contribution.getUpfrontContributions())
                .withUpliftApplied(contribution.getUpliftApplied())
                .withBasedOn(contribution.getBasedOn())
                .withTransferStatus(TransferStatus.valueOf(contribution.getTransferStatus()))
                .withDateUpliftApplied(contribution.getDateUpliftApplied().atStartOfDay())
                .withDateUpliftRemoved(contribution.getDateUpliftRemoved().atStartOfDay())
                .withDateCreated(contribution.getDateCreated())
                .withUserCreated(contribution.getUserCreated())
                .withDateModified(contribution.getDateModified())
                .withUserModified(contribution.getUserModified())
                .withCreateContributionOrder(contribution.getCreateContributionOrder())
                .withCorrespondenceId(contribution.getCorrespondenceId())
                .withActive(contribution.getActive())
                .withReplacedDate(contribution.getReplacedDate().atStartOfDay())
                .withLatest(contribution.getLatest())
                .withCcOutcomeCount(contribution.getCcOutcomeCount())
                .withSeHistoryId(contribution.getSeHistoryId());
    }
}
