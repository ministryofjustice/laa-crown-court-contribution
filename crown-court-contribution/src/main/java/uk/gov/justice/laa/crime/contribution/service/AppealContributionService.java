package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.builder.AppealContributionResponseBuilder;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestBuilder;
import uk.gov.justice.laa.crime.contribution.builder.GetContributionAmountRequestBuilder;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentResult;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppealContributionService {

    private final MaatCourtDataService maatCourtDataService;
    private final GetContributionAmountRequestBuilder getContributionAmountRequestBuilder;
    private final CreateContributionRequestBuilder createContributionRequestBuilder;
    private final AppealContributionResponseBuilder appealContributionResponseBuilder;

    public AppealContributionResponse calculateContribution(AppealContributionRequest appealContributionRequest, String laaTransactionId) {
        AssessmentResult assessmentResult = determineAssessmentResult(appealContributionRequest.getAssessments());

        GetContributionAmountRequest getContributionAmountRequest = getContributionAmountRequestBuilder.build(appealContributionRequest, assessmentResult);
        BigDecimal appealContributionAmount = maatCourtDataService.getContributionAppealAmount(getContributionAmountRequest, laaTransactionId);

        Integer repId = appealContributionRequest.getRepId();
        Contribution currContribution =  maatCourtDataService.findContribution(repId, laaTransactionId);

        if (currContribution.getUpfrontContributions() != appealContributionAmount) {
            CreateContributionRequest createContributionRequest = createContributionRequestBuilder.build(appealContributionRequest, appealContributionAmount);
            Contribution newContribution = maatCourtDataService.createContribution(createContributionRequest, laaTransactionId);

            log.info("Contribution data has been updated");
            return appealContributionResponseBuilder.build(newContribution);
        }

        log.info("Contribution data is already up to date");
        return appealContributionResponseBuilder.build(currContribution);
    }

    private AssessmentResult determineAssessmentResult(List<Assessment> assessments) {
        for (Assessment assessment : assessments) {
            if (assessment.getStatus().value.equals("COMPLETE") && (assessment.getResult().equals(AssessmentResult.PASS))) {
                return AssessmentResult.PASS;
            }
        }

        return AssessmentResult.FAIL;
    }
}
