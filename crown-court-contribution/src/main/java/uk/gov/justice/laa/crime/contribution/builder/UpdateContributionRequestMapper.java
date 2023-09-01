package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.UpdateContributionRequest;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

@Component
@AllArgsConstructor
public class UpdateContributionRequestMapper {
    public UpdateContributionRequest map(Contribution contribution) {
        return new UpdateContributionRequest()
                .withId(contribution.getId())
                .withUserModified(contribution.getUserModified())
                .withCorrespondenceId(contribution.getCorrespondenceId())
                .withContributionCap(contribution.getContributionCap())
                .withBasedOn(contribution.getBasedOn())
                .withCalcDate(convertDateToDateTime(contribution.getCalcDate()))
                .withMonthlyContributions(contribution.getMonthlyContributions())
                .withCreateContributionOrder(contribution.getCreateContributionOrder())
                .withUpfrontContributions(contribution.getUpfrontContributions())
                .withEffectiveDate(convertDateToDateTime(contribution.getEffectiveDate()))
                .withContributionFileId(contribution.getContributionFileId())
                .withUpliftApplied(contribution.getUpliftApplied())
                .withDateUpliftApplied(convertDateToDateTime(contribution.getDateUpliftApplied()))
                .withDateUpliftRemoved(convertDateToDateTime(contribution.getDateUpliftRemoved()))
                .withTransferStatus(contribution.getTransferStatus());
    }
}
