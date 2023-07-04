package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributionDTOBuilder {

    public static ContributionDTO build(final CreateContributionRequest request) {
        ContributionDTO.ContributionDTOBuilder builder = ContributionDTO.builder()
                .applId(request.getApplId())
                .repId(request.getRepId())
                .contributionFileId(request.getContributionFileId())
                .effectiveDate(request.getEffectiveDate() != null ? request.getEffectiveDate().toLocalDate() : null)
                .calcDate(request.getCalcDate() != null ? request.getCalcDate().toLocalDate() : null)
                .contributionCap(request.getContributionCap())
                .monthlyContributions(request.getMonthlyContributions())
                .upfrontContributions(request.getUpfrontContributions())
                .upliftApplied(request.getUpliftApplied())
                .basedOn(request.getBasedOn())
                .transferStatus(request.getTransferStatus() != null ? request.getTransferStatus().getValue() : null)
                .dateUpliftApplied(request.getDateUpliftApplied() != null ? request.getDateUpliftApplied().toLocalDate() : null)
                .dateUpliftRemoved(request.getDateUpliftRemoved() != null ? request.getDateUpliftRemoved().toLocalDate() : null)
                .createContributionOrder(request.getCreateContributionOrder())
                .correspondenceId(request.getCorrespondenceId());

        return builder.build();

    }
}
