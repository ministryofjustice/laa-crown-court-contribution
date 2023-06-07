package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class CreateContributionRequestMapper {
    public CreateContributionRequest map(AppealContributionRequest appealContributionRequest, BigDecimal appealContributionAmount) {
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
}
