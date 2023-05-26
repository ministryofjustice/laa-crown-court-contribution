package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentResult;

@Component
@AllArgsConstructor
public class GetContributionAmountRequestBuilder {

    // TODO: Can this just be mapped using mapstruct???
    public GetContributionAmountRequest build(AppealContributionRequest appealContributionRequest, AssessmentResult assessmentResult) {
        return new GetContributionAmountRequest()
                .withCaseType(appealContributionRequest.getCaseType())
                .withAppealType(appealContributionRequest.getAppealType())
                .withOutcome(appealContributionRequest.getLastOutcome().getOutcome())
                .withAssessmentResult(assessmentResult);
    }
}
