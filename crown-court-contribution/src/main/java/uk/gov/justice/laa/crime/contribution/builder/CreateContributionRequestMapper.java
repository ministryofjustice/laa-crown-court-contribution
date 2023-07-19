package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class CreateContributionRequestMapper {
    public static CreateContributionRequest map(AppealContributionRequest appealContributionRequest, BigDecimal appealContributionAmount) {
        return new CreateContributionRequest()
                .withRepId(appealContributionRequest.getRepId())
                .withApplId(appealContributionRequest.getApplId())
                .withContributionCap(BigDecimal.ZERO)
                .withEffectiveDate(appealContributionRequest.getLastOutcome().getDateSet())
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpliftApplied("N")
                .withBasedOn(null)
                .withUpfrontContributions(appealContributionAmount)
                .withUserCreated(appealContributionRequest.getUserCreated())
                .withCorrespondenceId(null)
                .withCreateContributionOrder("N");

    }

    public CreateContributionRequest map(ContributionDTO contributionDTO, BigDecimal appealContributionAmount) {
        return new CreateContributionRequest()
                .withRepId(contributionDTO.getRepId())
                .withApplId(contributionDTO.getApplId())
                .withContributionCap(BigDecimal.ZERO)
                .withEffectiveDate(contributionDTO.getLastOutcome().getDateSet())
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpliftApplied("N")
                .withBasedOn(null)
                .withUpfrontContributions(appealContributionAmount)
                .withUserCreated(contributionDTO.getUserCreated())
                .withCorrespondenceId(null)
                .withCreateContributionOrder("N");

    }

}
