package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.TransferStatus;

import java.time.LocalDate;

import static java.util.Optional.ofNullable;


@Component
@AllArgsConstructor
public class CalculateContributionResponseMapper {

    public ContributionDTO map(Contribution contribution) {
        ContributionDTO.ContributionDTOBuilder builder = ContributionDTO.builder()
                .id(contribution.getId())
                .applId(contribution.getApplId())
                .repId(contribution.getRepId())
                .contributionFileId(ofNullable(contribution.getContributionFileId()).orElse(null))
                .effectiveDate(contribution.getEffectiveDate().atStartOfDay().toLocalDate())
                .calcDate(contribution.getCalcDate().atStartOfDay().toLocalDate())
                .contributionCap(contribution.getContributionCap())
                .monthlyContributions(contribution.getMonthlyContributions())
                .upfrontContributions(ofNullable(contribution.getUpfrontContributions()).orElse(null))
                .upliftApplied(ofNullable(contribution.getUpliftApplied()).orElse(null))
                .basedOn(ofNullable(contribution.getBasedOn()).orElse(null))
                .transferStatus(contribution.getTransferStatus())
                .dateUpliftApplied(contribution.getDateUpliftApplied())
                .dateUpliftRemoved(contribution.getDateUpliftRemoved())
                .dateCreated(contribution.getDateCreated())
                .userCreated(contribution.getUserCreated())
                .dateModified(ofNullable(contribution.getDateModified()).orElse(null))
                .userModified(ofNullable(contribution.getUserModified()).orElse(null))
                .createContributionOrder(ofNullable(contribution.getCreateContributionOrder()).orElse(null))
                .correspondenceId(ofNullable(contribution.getCorrespondenceId()).orElse(null))
                .active(ofNullable(contribution.getActive()).orElse(null))
                .replacedDate(contribution.getReplacedDate())
                .latest(ofNullable(contribution.getLatest()).orElse(null))
                .ccOutcomeCount(contribution.getCcOutcomeCount())
                .seHistoryId(ofNullable(contribution.getSeHistoryId()).orElse(null));

        return builder.build();
    }

}
