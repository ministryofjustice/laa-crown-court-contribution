package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.CreateContributionRequest;

import java.math.BigDecimal;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

@Component
@AllArgsConstructor
public class CreateContributionRequestMapper {
    public CreateContributionRequest map(ApiMaatCalculateContributionRequest appealContributionRequest, BigDecimal appealContributionAmount) {
        return new CreateContributionRequest()
                .withRepId(appealContributionRequest.getRepId())
                .withApplId(appealContributionRequest.getApplId())
                .withContributionCap(BigDecimal.ZERO)
                .withEffectiveDate((appealContributionRequest.getLastOutcome().getDateSet() != null) ? appealContributionRequest.getLastOutcome().getDateSet() : null)
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpliftApplied("N")
                .withBasedOn(null)
                .withUpfrontContributions(appealContributionAmount)
                .withUserCreated(appealContributionRequest.getUserCreated())
                .withCorrespondenceId(null)
                .withCreateContributionOrder("N");

    }

    public CreateContributionRequest map(CalculateContributionDTO calculateContributionDTO, BigDecimal appealContributionAmount) {
        CreateContributionRequest createContributionRequest = map(calculateContributionDTO);
        return createContributionRequest.withUpfrontContributions(appealContributionAmount);
    }

    public CreateContributionRequest map(CalculateContributionDTO calculateContributionDTO) {
        return new CreateContributionRequest()
                .withRepId(calculateContributionDTO.getRepId())
                .withApplId(calculateContributionDTO.getApplId())
                .withContributionCap(calculateContributionDTO.getContributionCap())
                .withEffectiveDate(convertDateToDateTime(calculateContributionDTO.getEffectiveDate()))
                .withMonthlyContributions(calculateContributionDTO.getMonthlyContributions())
                .withUpliftApplied(calculateContributionDTO.getUpliftApplied())
                .withBasedOn(calculateContributionDTO.getBasedOn())
                .withCorrespondenceId(calculateContributionDTO.getCorrespondenceId())
                .withCreateContributionOrder(calculateContributionDTO.getCreateContributionOrder())
                .withCalcDate(convertDateToDateTime(calculateContributionDTO.getCalcDate()))
                .withContributionFileId(calculateContributionDTO.getContributionFileId())
                .withDateUpliftApplied(convertDateToDateTime(calculateContributionDTO.getDateUpliftApplied()))
                .withDateUpliftRemoved(convertDateToDateTime(calculateContributionDTO.getDateUpliftRemoved()))
                .withTransferStatus(calculateContributionDTO.getTransferStatus())
                .withUserCreated(calculateContributionDTO.getUserCreated())
                .withUpfrontContributions(calculateContributionDTO.getUpfrontContributions());
    }
}
