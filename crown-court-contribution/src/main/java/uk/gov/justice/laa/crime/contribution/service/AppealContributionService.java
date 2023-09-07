package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.builder.MaatCalculateContributionResponseBuilder;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.GetContributionAmountRequestMapper;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.model.common.Assessment;
import uk.gov.justice.laa.crime.contribution.model.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.MaatCalculateContributionResponse;
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


    private AssessmentResult determineAssessmentResult(List<Assessment> assessments) {
        for (Assessment assessment : assessments) {
            if (assessment.getStatus() == AssessmentStatus.COMPLETE && assessment.getResult() == AssessmentResult.PASS) {
                return AssessmentResult.PASS;
            }
        }

        return AssessmentResult.FAIL;
    }

    public MaatCalculateContributionResponse calculateAppealContribution(CalculateContributionDTO calculateContributionDTO, String laaTransactionId) {
        AssessmentResult assessmentResult = determineAssessmentResult(calculateContributionDTO.getAssessments());

        GetContributionAmountRequest getContributionAmountRequest = getContributionAmountRequestMapper.map(calculateContributionDTO, assessmentResult);
        BigDecimal appealContributionAmount = maatCourtDataService.getContributionAppealAmount(getContributionAmountRequest, laaTransactionId);

        Integer repId = calculateContributionDTO.getRepId();
        List<Contribution> currContributionList = maatCourtDataService.findContribution(repId, laaTransactionId, true);
        Contribution currContribution = currContributionList.get(0);
        if (currContribution.getUpfrontContributions().compareTo(appealContributionAmount) != 0) {
            CreateContributionRequest createContributionRequest = createContributionRequestMapper.map(calculateContributionDTO, appealContributionAmount);
            Contribution newContribution = maatCourtDataService.createContribution(createContributionRequest, laaTransactionId);
            log.info("Contribution data has been updated");
            return MaatCalculateContributionResponseBuilder.build(newContribution);
        }
        log.info("Contribution data is already up to date");
        return MaatCalculateContributionResponseBuilder.build(currContribution);
    }

}
