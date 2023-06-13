package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.TransferStatus;

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
                .withContributionFileId(ofNullable(contribution.getContributionFileId()).orElse(null))
                .withEffectiveDate(contribution.getEffectiveDate().atStartOfDay())
                .withCalcDate(contribution.getCalcDate().atStartOfDay())
                .withContributionCap(contribution.getContributionCap())
                .withMonthlyContributions(contribution.getMonthlyContributions())
                .withUpfrontContributions(ofNullable(contribution.getUpfrontContributions()).orElse(null))
                .withUpliftApplied(ofNullable(contribution.getUpliftApplied()).orElse(null))
                .withBasedOn(ofNullable(contribution.getBasedOn()).orElse(null))
                .withTransferStatus(ofNullable(contribution.getTransferStatus()).map(TransferStatus::getFrom).orElse(null))
                .withDateUpliftApplied(ofNullable(contribution.getDateUpliftApplied()).map(LocalDate::atStartOfDay).orElse(null))
                .withDateUpliftRemoved(ofNullable(contribution.getDateUpliftRemoved()).map(LocalDate::atStartOfDay).orElse(null))
                .withDateCreated(contribution.getDateCreated())
                .withUserCreated(contribution.getUserCreated())
                .withDateModified(ofNullable(contribution.getDateModified()).orElse(null))
                .withUserModified(ofNullable(contribution.getUserModified()).orElse(null))
                .withCreateContributionOrder(ofNullable(contribution.getCreateContributionOrder()).orElse(null))
                .withCorrespondenceId(ofNullable(contribution.getCorrespondenceId()).orElse(null))
                .withActive(ofNullable(contribution.getActive()).orElse(null))
                .withReplacedDate(ofNullable(contribution.getReplacedDate()).map(LocalDate::atStartOfDay).orElse(null))
                .withLatest(ofNullable(contribution.getLatest()).orElse(null))
                .withCcOutcomeCount(ofNullable(contribution.getCcOutcomeCount()).orElse(null))
                .withSeHistoryId(ofNullable(contribution.getSeHistoryId()).orElse(null));
    }
}
