package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CrownCourtAppealOutcome;

@Component
@AllArgsConstructor
public class GetContributionAmountRequestMapper {

    public GetContributionAmountRequest map(CalculateContributionDTO calculateContributionDTO, AssessmentResult assessmentResult) {
        CrownCourtAppealOutcome outcome = calculateContributionDTO.getLastOutcome() != null ?
                calculateContributionDTO.getLastOutcome().getOutcome() : null;
        return new GetContributionAmountRequest()
                .withCaseType(calculateContributionDTO.getCaseType())
                .withAppealType(calculateContributionDTO.getAppealType())
                .withOutcome(outcome)
                .withAssessmentResult(assessmentResult);
    }

}
