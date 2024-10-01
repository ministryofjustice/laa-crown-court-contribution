package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.GetContributionAmountRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.MaatCalculateContributionResponseMapper;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AppealContributionAmount;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CurrentStatus;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppealContributionService {

    private final MaatCourtDataService maatCourtDataService;
    private final GetContributionAmountRequestMapper getContributionAmountRequestMapper;
    private final CreateContributionRequestMapper createContributionRequestMapper;
    private final MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper;

    private AssessmentResult determineAssessmentResult(List<ApiAssessment> assessments) {
        for (ApiAssessment assessment : assessments) {
            if (assessment.getStatus() == CurrentStatus.COMPLETE && assessment.getResult() == AssessmentResult.PASS) {
                return AssessmentResult.PASS;
            }
        }
        return AssessmentResult.FAIL;
    }

    public ApiMaatCalculateContributionResponse calculateAppealContribution(
            CalculateContributionDTO calculateContributionDTO) {
        AssessmentResult assessmentResult = determineAssessmentResult(calculateContributionDTO.getAssessments());

        // TODO: Need to amend this mapper as we are no longer passing through lastOutcome field in DTO
        // TODO: Also do we even need this object at all if we are only passing members of it later in method?
        GetContributionAmountRequest getContributionAmountRequest =
                getContributionAmountRequestMapper.map(calculateContributionDTO, assessmentResult);
        BigDecimal appealContributionAmount = null;

        // TODO: Replace this conditional with check for non empty list of outcomes
        // TODO: Also change this if conditional to encompass the rest of the logic in this method
        if (getContributionAmountRequest.getOutcome() != null &&
                getContributionAmountRequest.getAssessmentResult() != null) {
            appealContributionAmount = AppealContributionAmount.calculate(
                            getContributionAmountRequest.getAppealType(), getContributionAmountRequest.getOutcome(),
                            getContributionAmountRequest.getAssessmentResult()
                    )
                    .getContributionAmount();
        }

        Integer repId = calculateContributionDTO.getRepId();
        List<Contribution> currContributionList = maatCourtDataService.findContribution(repId, true);
        if (CollectionUtils.isNotEmpty(currContributionList)) {
            Contribution currContribution = currContributionList.get(0);
            if (currContribution.getUpfrontContributions() == null
                    || (appealContributionAmount == null
                    || currContribution.getUpfrontContributions().compareTo(appealContributionAmount) != 0)) {
                CreateContributionRequest createContributionRequest =
                        createContributionRequestMapper.map(calculateContributionDTO, appealContributionAmount);
                Contribution newContribution = maatCourtDataService.createContribution(createContributionRequest);
                return maatCalculateContributionResponseMapper.map(newContribution);
            }
            return maatCalculateContributionResponseMapper.map(currContribution);
        }
        return new ApiMaatCalculateContributionResponse();
    }

}
