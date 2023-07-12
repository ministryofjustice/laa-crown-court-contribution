package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;


@Component
@AllArgsConstructor
public class CalculateContributionResponseMapper {

    public static ContributionDTO map(Contribution contribution) {
        ContributionDTO.ContributionDTOBuilder builder = ContributionDTO.builder()
                .id(contribution.getId())
                .applId(contribution.getApplId())
                .repId(contribution.getRepId())
                .contributionFileId(contribution.getContributionFileId())
                .effectiveDate(contribution.getEffectiveDate())
                .calcDate(contribution.getCalcDate())
                .contributionCap(contribution.getContributionCap())
                .monthlyContributions(contribution.getMonthlyContributions())
                .upfrontContributions(contribution.getUpfrontContributions())
                .upliftApplied(contribution.getUpliftApplied())
                .basedOn(contribution.getBasedOn())
                .transferStatus(contribution.getTransferStatus())
                .dateUpliftApplied(contribution.getDateUpliftApplied())
                .dateUpliftRemoved(contribution.getDateUpliftRemoved())
                .dateCreated(contribution.getDateCreated())
                .userCreated(contribution.getUserCreated())
                .dateModified(contribution.getDateModified())
                .userModified(contribution.getUserModified())
                .createContributionOrder(contribution.getCreateContributionOrder())
                .correspondenceId(contribution.getCorrespondenceId())
                .active(contribution.getActive())
                .replacedDate(contribution.getReplacedDate())
                .latest(contribution.getLatest())
                .ccOutcomeCount(contribution.getCcOutcomeCount())
                .seHistoryId(contribution.getSeHistoryId());
        return builder.build();
    }

}
