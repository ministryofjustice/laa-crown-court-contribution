package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.ContributionResult;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateContributionRequestMapper {
    public CreateContributionRequest map(
            CalculateContributionDTO calculateContributionDTO, BigDecimal appealContributionAmount) {
        CreateContributionRequest createContributionRequest = map(calculateContributionDTO);
        return createContributionRequest.withUpfrontContributions(appealContributionAmount);
    }

    public CreateContributionRequest map(CalculateContributionDTO calculateContributionDTO) {
        return new CreateContributionRequest()
                .withRepId(calculateContributionDTO.getRepId())
                .withApplicantId(calculateContributionDTO.getApplicantId())
                .withContributionCap(calculateContributionDTO.getContributionCap())
                .withEffectiveDate(calculateContributionDTO.getEffectiveDate())
                .withMonthlyContributions(calculateContributionDTO.getMonthlyContributions())
                .withUpliftApplied(calculateContributionDTO.getUpliftApplied())
                .withBasedOn(calculateContributionDTO.getBasedOn())
                .withCreateContributionOrder(calculateContributionDTO.getCreateContributionOrder())
                .withCalcDate(calculateContributionDTO.getCalcDate())
                .withContributionFileId(calculateContributionDTO.getContributionFileId())
                .withDateUpliftApplied(calculateContributionDTO.getDateUpliftApplied())
                .withDateUpliftRemoved(calculateContributionDTO.getDateUpliftRemoved())
                .withTransferStatus(calculateContributionDTO.getTransferStatus())
                .withUserCreated(calculateContributionDTO.getUserCreated())
                .withUpfrontContributions(calculateContributionDTO.getUpfrontContributions());
    }

    public CreateContributionRequest map(CalculateContributionDTO calculateContributionDTO, ContributionResult result) {
        return new CreateContributionRequest()
                .withRepId(calculateContributionDTO.getRepId())
                .withApplicantId(calculateContributionDTO.getApplicantId())
                .withContributionCap(result.contributionCap())
                .withEffectiveDate(result.effectiveDate())
                .withMonthlyContributions(result.monthlyAmount())
                .withUpliftApplied(result.isUplift() ? "Y" : "N")
                .withBasedOn(result.basedOn())
                .withCreateContributionOrder(calculateContributionDTO.getCreateContributionOrder())
                .withCalcDate(calculateContributionDTO.getCalcDate())
                .withDateUpliftApplied(calculateContributionDTO.getDateUpliftApplied())
                .withDateUpliftRemoved(calculateContributionDTO.getDateUpliftRemoved())
                .withUserCreated(calculateContributionDTO.getUserCreated())
                .withUpfrontContributions(result.upfrontAmount());
    }
}
