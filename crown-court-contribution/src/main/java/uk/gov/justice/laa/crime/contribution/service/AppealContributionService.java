package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.builder.AppealContributionResponseMapper;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.GetContributionAmountRequestMapper;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentResult;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppealContributionService {

    private final MaatCourtDataService maatCourtDataService;
    private final GetContributionAmountRequestMapper getContributionAmountRequestMapper;
    private final CreateContributionRequestMapper createContributionRequestMapper;
    private final AppealContributionResponseMapper appealContributionResponseMapper;

    public AppealContributionResponse calculateContribution(AppealContributionRequest appealContributionRequest, String laaTransactionId) {
        AssessmentResult assessmentResult = determineAssessmentResult(appealContributionRequest.getAssessments());

        GetContributionAmountRequest getContributionAmountRequest = getContributionAmountRequestMapper.map(appealContributionRequest, assessmentResult);
        BigDecimal appealContributionAmount = maatCourtDataService.getContributionAppealAmount(getContributionAmountRequest, laaTransactionId);

        Integer repId = appealContributionRequest.getRepId();
        Contribution currContribution =  maatCourtDataService.findContribution(repId, laaTransactionId);

        if (currContribution.getUpfrontContributions().compareTo(appealContributionAmount) != 0) {
            CreateContributionRequest createContributionRequest = createContributionRequestMapper.map(appealContributionRequest, appealContributionAmount);
            Contribution newContribution = maatCourtDataService.createContribution(createContributionRequest, laaTransactionId);

            log.info("Contribution data has been updated");
            return appealContributionResponseMapper.map(newContribution);
        }

        log.info("Contribution data is already up to date");
        return appealContributionResponseMapper.map(currContribution);
    }

    private AssessmentResult determineAssessmentResult(List<Assessment> assessments) {
        for (Assessment assessment : assessments) {
            if (assessment.getStatus() == AssessmentStatus.COMPLETE && assessment.getResult() == AssessmentResult.PASS) {
                return AssessmentResult.PASS;
            }
        }

        return AssessmentResult.FAIL;
    }
}
