package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.MaatCalculateContributionResponseMapper;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AppealContributionAmount;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcomeType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppealContributionService {

    private final MaatCourtDataService maatCourtDataService;
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
        ApiCrownCourtOutcome latestAppealOutcome = calculateContributionDTO.getCrownCourtOutcomeList()
                .stream()
                .filter(outcome -> CrownCourtOutcomeType.APPEAL.getType().equals(outcome.getOutcome().getType()))
                .reduce((first, second) -> second)
                .orElse(null);

        BigDecimal appealContributionAmount = null;
        if (latestAppealOutcome != null) {
            appealContributionAmount = AppealContributionAmount.calculate(
                            calculateContributionDTO.getAppealType(), latestAppealOutcome.getOutcome(),
                            assessmentResult
                    )
                    .getContributionAmount();

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
        }
        return new ApiMaatCalculateContributionResponse();
    }

}
