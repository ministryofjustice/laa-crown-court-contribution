package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.UpdateContributionRequest;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

@Component
@AllArgsConstructor
public class UpdateContributionRequestMapper {
    public UpdateContributionRequest map(ContributionDTO contributionDTO) {
        return new UpdateContributionRequest()
                .withId(contributionDTO.getId())
                .withUserModified(contributionDTO.getUserModified())
                .withCorrespondenceId(contributionDTO.getCorrespondenceId())
                .withContributionCap(contributionDTO.getContributionCap())
                .withBasedOn(contributionDTO.getBasedOn())
                .withCalcDate(convertDateToDateTime(contributionDTO.getCalcDate()))
                .withMonthlyContributions(contributionDTO.getMonthlyContributions())
                .withCreateContributionOrder(contributionDTO.getCreateContributionOrder())
                .withUpfrontContributions(contributionDTO.getUpfrontContributions())
                .withEffectiveDate(convertDateToDateTime(contributionDTO.getEffectiveDate()))
                .withContributionFileId(contributionDTO.getContributionFileId())
                .withContributionCap(contributionDTO.getContributionCap())
                .withUpliftApplied(contributionDTO.getUpliftApplied())
                .withDateUpliftApplied(convertDateToDateTime(contributionDTO.getDateUpliftApplied()))
                .withDateUpliftRemoved(convertDateToDateTime(contributionDTO.getDateUpliftRemoved()))
                .withTransferStatus(contributionDTO.getTransferStatus());
    }
}
