package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;

import java.math.BigDecimal;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

@Component
@AllArgsConstructor
public class CreateContributionRequestMapper {
    public CreateContributionRequest map(AppealContributionRequest appealContributionRequest, BigDecimal appealContributionAmount) {
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

    public CreateContributionRequest map(ContributionDTO contributionDTO, BigDecimal appealContributionAmount) {
        CreateContributionRequest createContributionRequest = map(contributionDTO);
        return createContributionRequest.withUpfrontContributions(appealContributionAmount);
    }

    public CreateContributionRequest map(ContributionDTO contributionDTO) {
        return new CreateContributionRequest()
                .withRepId(contributionDTO.getRepId())
                .withApplId(contributionDTO.getApplId())
                .withContributionCap(contributionDTO.getContributionCap())
                .withEffectiveDate(convertDateToDateTime(contributionDTO.getEffectiveDate()))
                .withMonthlyContributions(contributionDTO.getMonthlyContributions())
                .withUpliftApplied(contributionDTO.getUpliftApplied())
                .withBasedOn(contributionDTO.getBasedOn())
                .withUserCreated(contributionDTO.getUserCreated())
                .withCorrespondenceId(contributionDTO.getCorrespondenceId())
                .withCreateContributionOrder(contributionDTO.getCreateContributionOrder())
                .withCalcDate(convertDateToDateTime(contributionDTO.getCalcDate()))
                .withContributionFileId(contributionDTO.getContributionFileId())
                .withDateUpliftApplied(convertDateToDateTime(contributionDTO.getDateUpliftApplied()))
                .withDateUpliftRemoved(convertDateToDateTime(contributionDTO.getDateUpliftRemoved()))
                .withTransferStatus(contributionDTO.getTransferStatus())
                .withUserCreated(contributionDTO.getUserCreated())
                .withUpfrontContributions(contributionDTO.getUpfrontContributions());
    }
}
