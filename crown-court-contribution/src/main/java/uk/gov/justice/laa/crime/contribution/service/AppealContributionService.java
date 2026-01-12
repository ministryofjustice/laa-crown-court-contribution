package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.common.model.contribution.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
        ApiCrownCourtOutcome latestAppealOutcome = Optional.ofNullable(
                        calculateContributionDTO.getCrownCourtOutcomeList())
                .orElse(Collections.emptyList())
                .stream()
                .reduce((first, second) -> second)
                .filter(outcome -> CrownCourtOutcomeType.APPEAL
                        .getType()
                        .equals(outcome.getOutcome().getType()))
                .orElse(null);

        if (latestAppealOutcome != null) {
            BigDecimal appealContributionAmount = AppealContributionAmount.calculate(
                            calculateContributionDTO.getAppealType(),
                            latestAppealOutcome.getOutcome(),
                            assessmentResult)
                    .getContributionAmount();

            Integer repId = calculateContributionDTO.getRepId();
            List<Contribution> currentContributionList = maatCourtDataService.findContribution(repId, true);
            if (!currentContributionList.isEmpty()) {
                Contribution currentContribution = currentContributionList.getFirst();
                if (currentContribution.getUpfrontContributions() == null
                        || currentContribution.getUpfrontContributions().compareTo(appealContributionAmount) != 0) {
                    CreateContributionRequest createContributionRequest =
                            createContributionRequestMapper.map(calculateContributionDTO, appealContributionAmount);
                    Contribution newContribution = maatCourtDataService.createContribution(createContributionRequest);
                    return maatCalculateContributionResponseMapper.map(newContribution);
                }
                return maatCalculateContributionResponseMapper.map(currentContribution);
            }
        }
        return new ApiMaatCalculateContributionResponse();
    }
}
