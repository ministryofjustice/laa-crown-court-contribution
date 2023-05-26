package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestBuilder;
import uk.gov.justice.laa.crime.contribution.builder.GetContributionAmountRequestBuilder;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentResult;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CrownCourtAppealOutcome;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppealContributionService {

    private final MaatCourtDataService maatCourtDataService;
    private final GetContributionAmountRequestBuilder appealContributionBuilder;
    private final CreateContributionRequestBuilder createContributionRequestBuilder;

    public AppealContributionResponse calculateContribution(AppealContributionRequest appealContributionRequest) {
        // TODO: Check before run bus logic, do this as part of validation as nothing happens otherwise???
        if ((appealContributionRequest.getAppealType() != null) && (appealContributionRequest.getLastOutcome() != null)) {
            // TODO: Need to wrap the following in a try catch to handle any non 2xx returns from MAAT API???
            // TODO: Need to sort out how to get trans id, assume being sent as part of request to C3, this be part of schema???
            String transactionId = "???";
            // TODO: Dont think we actually need the following line if we just sending through latest outcome data
            // CrownCourtAppealOutcome crownCourtAppealOutcome = appealContributionRequest.getLastOutcome();

            AssessmentResult assessmentResult = determineAssessmentResult(appealContributionRequest.getAssessments());

            GetContributionAmountRequest getContributionAmountRequest = appealContributionBuilder.build(appealContributionRequest, assessmentResult);
            BigDecimal appealContributionAmount = maatCourtDataService.getContributionAppealAmount(getContributionAmountRequest, transactionId);

            Integer repId = appealContributionRequest.getRepId();
            Contribution currContribution =  maatCourtDataService.findContribution(repId, transactionId);

            if (currContribution.getUpfrontContributions() != appealContributionAmount) {
                CreateContributionRequest createContributionRequest = createContributionRequestBuilder.build(appealContributionRequest, appealContributionAmount);
                Contribution newContribution = maatCourtDataService.createContribution(createContributionRequest, transactionId);

                // TODO: Need to return something here as CCC has actually gone through and updated appeal contrib, need to find out what to return to pass onto MATRIX processor
                return null;
            }
        }

        // TODO: Nothing has been done in C3 at this point so need to return just null or empty or something to signify no processing took place
        return null;
    }

    private AssessmentResult determineAssessmentResult(List<Assessment> assessments) {
        for (Assessment assessment : assessments) {
            if (assessment.getStatus().equals("COMPLETE") && assessment.getResult().equals("PASS")) {
                return AssessmentResult.PASS;
            } else if (assessment.getStatus().equals("COMPLETE") && assessment.getResult().equals("FAIL")) {
                return AssessmentResult.FAIL;
            } else {
                // TODO: Handle error if no PASS or FAIL results
            }
        }

        return null;
    }
}
