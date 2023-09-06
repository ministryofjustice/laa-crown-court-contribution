package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.maat_api.AppealContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;

import java.time.LocalDate;

import static java.util.Optional.ofNullable;


@Component
@AllArgsConstructor
public class AppealContributionResponseMapper {

    public AppealContributionResponse map(Contribution contribution) {
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
                .withTransferStatus(contribution.getTransferStatus())
                .withDateUpliftApplied(ofNullable(contribution.getDateUpliftApplied()).map(LocalDate::atStartOfDay).orElse(null))
                .withDateUpliftRemoved(ofNullable(contribution.getDateUpliftRemoved()).map(LocalDate::atStartOfDay).orElse(null))
                .withDateCreated(contribution.getDateCreated())
                .withUserCreated(contribution.getUserCreated())
                .withDateModified(contribution.getDateModified())
                .withUserModified(contribution.getUserModified())
                .withCreateContributionOrder(contribution.getCreateContributionOrder())
                .withCorrespondenceId(contribution.getCorrespondenceId())
                .withActive(contribution.getActive())
                .withReplacedDate(ofNullable(contribution.getReplacedDate()).map(LocalDate::atStartOfDay).orElse(null))
                .withLatest(contribution.getLatest())
                .withCcOutcomeCount(contribution.getCcOutcomeCount())
                .withSeHistoryId(contribution.getSeHistoryId());
    }
}
