package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.UpdateContributionRequest;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

@Component
@AllArgsConstructor
public class UpdateContributionRequestMapper {
    public UpdateContributionRequest map(CalculateContributionDTO calculateContributionDTO) {
        return new UpdateContributionRequest()
                .withId(calculateContributionDTO.getId())
                .withUserModified(calculateContributionDTO.getUserModified())
                .withCorrespondenceId(calculateContributionDTO.getCorrespondenceId())
                .withContributionCap(calculateContributionDTO.getContributionCap())
                .withBasedOn(calculateContributionDTO.getBasedOn())
                .withCalcDate(convertDateToDateTime(calculateContributionDTO.getCalcDate()))
                .withMonthlyContributions(calculateContributionDTO.getMonthlyContributions())
                .withCreateContributionOrder(calculateContributionDTO.getCreateContributionOrder())
                .withUpfrontContributions(calculateContributionDTO.getUpfrontContributions())
                .withEffectiveDate(convertDateToDateTime(calculateContributionDTO.getEffectiveDate()))
                .withContributionFileId(calculateContributionDTO.getContributionFileId())
                .withContributionCap(calculateContributionDTO.getContributionCap())
                .withUpliftApplied(calculateContributionDTO.getUpliftApplied())
                .withDateUpliftApplied(convertDateToDateTime(calculateContributionDTO.getDateUpliftApplied()))
                .withDateUpliftRemoved(convertDateToDateTime(calculateContributionDTO.getDateUpliftRemoved()))
                .withTransferStatus(calculateContributionDTO.getTransferStatus());
    }
}
