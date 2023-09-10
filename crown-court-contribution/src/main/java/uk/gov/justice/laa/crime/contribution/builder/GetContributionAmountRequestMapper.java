package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.maat_api.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentResult;

@Component
@AllArgsConstructor
public class GetContributionAmountRequestMapper {

    public GetContributionAmountRequest map(AppealContributionRequest appealContributionRequest, AssessmentResult assessmentResult) {
        return new GetContributionAmountRequest()
                .withCaseType(appealContributionRequest.getCaseType())
                .withAppealType(appealContributionRequest.getAppealType())
                .withOutcome(appealContributionRequest.getLastOutcome().getOutcome())
                .withAssessmentResult(assessmentResult);
    }

    public GetContributionAmountRequest map(CalculateContributionDTO calculateContributionDTO, AssessmentResult assessmentResult) {
        return new GetContributionAmountRequest()
                .withCaseType(calculateContributionDTO.getCaseType())
                .withAppealType(calculateContributionDTO.getAppealType())
                .withOutcome(calculateContributionDTO.getLastOutcome().getOutcome())
                .withAssessmentResult(assessmentResult);
    }

}
