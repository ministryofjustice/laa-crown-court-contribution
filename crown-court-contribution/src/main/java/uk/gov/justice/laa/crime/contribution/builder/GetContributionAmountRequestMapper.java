package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.GetContributionAmountRequest;
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

    public GetContributionAmountRequest map(ContributionDTO contributionDTO, AssessmentResult assessmentResult) {
        return new GetContributionAmountRequest()
                .withCaseType(contributionDTO.getCaseType())
                .withAppealType(contributionDTO.getAppealType())
                .withOutcome(contributionDTO.getLastOutcome().getOutcome())
                .withAssessmentResult(assessmentResult);
    }

}
