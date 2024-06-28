package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.GetContributionAmountRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.MaatCalculateContributionResponseBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.contribution.CurrentStatus;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppealContributionService {

    private final MaatCourtDataService maatCourtDataService;
    private final GetContributionAmountRequestMapper getContributionAmountRequestMapper;
    private final CreateContributionRequestMapper createContributionRequestMapper;

    private AssessmentResult determineAssessmentResult(List<ApiAssessment> assessments) {
        for (ApiAssessment assessment : assessments) {
            if (assessment.getStatus() == CurrentStatus.COMPLETE && assessment.getResult() == AssessmentResult.PASS) {
                return AssessmentResult.PASS;
            }
        }

        return AssessmentResult.FAIL;
    }

    public ApiMaatCalculateContributionResponse calculateAppealContribution(CalculateContributionDTO calculateContributionDTO) {
        AssessmentResult assessmentResult = determineAssessmentResult(calculateContributionDTO.getAssessments());

        GetContributionAmountRequest getContributionAmountRequest = getContributionAmountRequestMapper.map(calculateContributionDTO, assessmentResult);
        BigDecimal appealContributionAmount = null;
        if(getContributionAmountRequest.getAppealType() != null
                && getContributionAmountRequest.getCaseType() != null
                && getContributionAmountRequest.getOutcome() != null &&
                getContributionAmountRequest.getAssessmentResult() != null){
             appealContributionAmount = maatCourtDataService.getContributionAppealAmount(getContributionAmountRequest);
        }

        Integer repId = calculateContributionDTO.getRepId();
        List<Contribution> currContributionList = maatCourtDataService.findContribution(repId, true);
        if (CollectionUtils.isNotEmpty(currContributionList)) {
            Contribution currContribution = currContributionList.get(0);
            if (currContribution.getUpfrontContributions() == null
                    || (appealContributionAmount == null
                    || currContribution.getUpfrontContributions().compareTo(appealContributionAmount) != 0)) {
                CreateContributionRequest createContributionRequest = createContributionRequestMapper.map(calculateContributionDTO, appealContributionAmount);
                Contribution newContribution = maatCourtDataService.createContribution(createContributionRequest);
                log.info("Contribution data has been updated");
                return MaatCalculateContributionResponseBuilder.build(newContribution);
            }
            log.info("Contribution data is already up to date");
            return MaatCalculateContributionResponseBuilder.build(currContribution);
        }
        return null;
    }

}
