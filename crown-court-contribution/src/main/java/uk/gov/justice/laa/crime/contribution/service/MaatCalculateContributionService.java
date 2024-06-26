package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.builder.*;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.*;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.UpdateContributionRequest;
import uk.gov.justice.laa.crime.contribution.util.DateUtil;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.contribution.AssessmentType;
import uk.gov.justice.laa.crime.enums.contribution.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCalculateContributionService {

    private final MaatCourtDataService maatCourtDataService;
    private final CrimeHardshipService crimeHardshipService;
    private final AppealContributionService appealContributionService;
    private final CompareContributionService compareContributionService;
    private final ContributionRulesService contributionRulesService;
    private final CalculateContributionService calculateContributionService;
    private final ContributionSummaryMapper contributionSummaryMapper;

    private final CreateContributionRequestMapper createContributionRequestMapper;
    private final ContributionService contributionService;
    private final UpdateContributionRequestMapper updateContributionRequestMapper;
    private final CalculateContributionRequestMapper calculateContributionRequestMapper;
    private final MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper;

    private static boolean isUpliftApplied(CalculateContributionDTO calculateContributionDTO, ContributionResponseDTO contributionResponseDTO) {
        return contributionResponseDTO.getUpliftCote() != null &&
                calculateContributionDTO.getDateUpliftApplied() != null &&
                calculateContributionDTO.getDateUpliftRemoved() == null;
    }

    public static BigDecimal getAnnualDisposableIncome(final CalculateContributionDTO calculateContributionDTO) {
        if ((calculateContributionDTO.getDisposableIncomeAfterMagHardship() != null)) {
            return calculateContributionDTO.getDisposableIncomeAfterMagHardship();
        } else if (calculateContributionDTO.getTotalAnnualDisposableIncome() != null){
            return calculateContributionDTO.getTotalAnnualDisposableIncome();
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static String getEffectiveDateByNewWorkReason(final CalculateContributionDTO calculateContributionDTO,
                                                         final BigDecimal monthlyContributions,
                                                         final LocalDate assEffectiveDate) {
        NewWorkReason newWorkReason = getNewWorkReason(calculateContributionDTO);
        if (NewWorkReason.FMA == newWorkReason) {
            return assEffectiveDate.toString();
        } else if (NewWorkReason.PAI == newWorkReason) {
            if (calculateContributionDTO.getMonthlyContributions().compareTo(monthlyContributions) <= 0) {
                return calculateContributionDTO.getEffectiveDate().toString();
            } else return assEffectiveDate.toString();
        } else {
            if (calculateContributionDTO.getEffectiveDate() == null) {
                return assEffectiveDate.toString();
            } else return calculateContributionDTO.getEffectiveDate().toString();
        }
    }

    public static NewWorkReason getNewWorkReason(final CalculateContributionDTO calculateContributionDTO) {
        return calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.PASSPORT).findFirst()
                .map(ApiAssessment::getNewWorkReason)
                .orElse(calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst()
                        .map(ApiAssessment::getNewWorkReason).orElse(null));
    }

    /**
     * This method returns the date based on the following logic:
     * v_effective_date := greatest(nvl(p_application_object.committal_date,to_date('01/01/1900', 'dd/mm/yyyy' ))
     * ,coalesce(p_application_object.passport_assessment_object.ass_date
     * ,p_application_object.current_assessment_object.fin_assessment_object.full_assessment_object.assessment_date
     * ,p_application_object.current_assessment_object.fin_assessment_object.initial_assessment_object.assessment_date)
     * );
     **/
    public static LocalDate getEffectiveDate(final CalculateContributionDTO calculateContributionDTO) {
        LocalDate committalDate = calculateContributionDTO.getCommittalDate();
        Optional<ApiAssessment> passAssessment = calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.PASSPORT).findFirst();
        LocalDateTime assessmentDate = passAssessment.map(ApiAssessment::getAssessmentDate).orElse(null);
        if (assessmentDate == null) {
            Optional<ApiAssessment> fullAssessment = calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.FULL).findFirst();
            assessmentDate = fullAssessment.map(ApiAssessment::getAssessmentDate).orElse(null);
            if (assessmentDate == null) {
                Optional<ApiAssessment> initAssessment = calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst();
                assessmentDate = initAssessment.map(ApiAssessment::getAssessmentDate).orElse(null);
            }
        }
        if (committalDate == null) {
            return DateUtil.parseLocalDate(assessmentDate);
        } else {
            LocalDate assDate = DateUtil.parseLocalDate(assessmentDate);
            if (committalDate.isAfter(assDate)) {
                return committalDate;
            } else {
                return assDate;
            }
        }
    }

    public ApiMaatCalculateContributionResponse calculateContribution(CalculateContributionDTO calculateContributionDTO) {
        ApiMaatCalculateContributionResponse response;

        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(calculateContributionDTO.getRepId());
        calculateContributionDTO.setRepOrderDTO(repOrderDTO);

        if (CaseType.APPEAL_CC.equals(calculateContributionDTO.getCaseType())) {
            response = appealContributionService.calculateAppealContribution(calculateContributionDTO);
        } else {
            response = getCalculateContributionResponse(calculateContributionDTO, repOrderDTO);
        }

        return response;
    }

    public List<ApiContributionSummary> getContributionSummaries(final int repId) {
        List<ContributionsSummaryDTO> contribSummaryList = maatCourtDataService.getContributionsSummary(repId);
        return contribSummaryList != null ? contribSummaryList.stream().map(contributionSummaryMapper::map).toList() : List.of();
    }

    public ApiMaatCalculateContributionResponse getCalculateContributionResponse(final CalculateContributionDTO calculateContributionDTO,
                                                                                 final RepOrderDTO repOrderDTO) {
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse();

        Optional<ApiAssessment> fullAssessment = calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.FULL).findFirst();
        Optional<ApiAssessment> initAssessment = calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst();
        String fullResult = fullAssessment.map(assessment -> assessment.getResult().name()).orElse(null);

        ContributionResponseDTO contributionResponseDTO = contributionService.checkContribsCondition(ContributionRequestDTO.builder()
                .caseType(calculateContributionDTO.getCaseType())
                .effectiveDate(calculateContributionDTO.getEffectiveDate())
                .monthlyContribs(calculateContributionDTO.getMonthlyContributions())
                .fullResult(fullResult)
                .initResult(initAssessment.map(assessment -> assessment.getResult().name()).orElse(null))
                .removeContribs(calculateContributionDTO.getRemoveContribs())
                .build());

        if (Constants.Y.equals(contributionResponseDTO.getDoContribs())) {
            response = doContribs(calculateContributionDTO, contributionResponseDTO, fullResult, repOrderDTO);
        }
        return response;
    }

    public ApiMaatCalculateContributionResponse doContribs(final CalculateContributionDTO calculateContributionDTO,
                                                           final ContributionResponseDTO contributionResponseDTO,
                                                           final String fullResult,
                                                           final RepOrderDTO repOrderDTO) {
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse();

        //Use Calculated Monthly Contributions value - p_application_object.crown_court_overview_object.contributions_object.monthly_contribs > 0 ->
        if (Constants.Y.equals(contributionResponseDTO.getCalcContribution()) ||
                contributionResponseDTO.getTemplate() != null ||
                (calculateContributionDTO.getMonthlyContributions() != null && calculateContributionDTO.getMonthlyContributions().compareTo(BigDecimal.ZERO) > 0) ||
                Constants.INEL.equals(fullResult)) {
            response = calcContribs(calculateContributionDTO, contributionResponseDTO);
        } else if (calculateContributionDTO.getMonthlyContributions() != null) {
            response.setMonthlyContributions(BigDecimal.ZERO);
            response.setContributionCap(BigDecimal.ZERO);
            response.setUpfrontContributions(BigDecimal.ZERO);
        }

        Contribution currentContribution = getCurrentContribution(calculateContributionDTO);

        Contribution createdContribution = verifyAndCreateContribs(calculateContributionDTO, repOrderDTO,
                response, currentContribution);

        if (contributionResponseDTO.getTemplate() != null && createdContribution != null) {
            response.setProcessActivity(true);
        }
        return response;
    }

    public Contribution verifyAndCreateContribs(final CalculateContributionDTO calculateContributionDTO,
                                                final RepOrderDTO repOrderDTO,
                                                final ApiMaatCalculateContributionResponse response,
                                                final Contribution currentContribution) {
        TransferStatus currentTransferStatus = null;
        Integer currentContributionFileId = null;

        if (currentContribution != null) {
            currentTransferStatus = currentContribution.getTransferStatus();
            currentContributionFileId = currentContribution.getContributionFileId();
        } else {
            log.error("C3 Service: Current Contribution Is NULL.");
        }
        if ((calculateContributionDTO.getMonthlyContributions() != null
                && response.getMonthlyContributions().compareTo(calculateContributionDTO.getMonthlyContributions()) != 0)
                || (response.getEffectiveDate() != null && !response.getEffectiveDate().toLocalDate().equals(calculateContributionDTO.getEffectiveDate()))
        ) {
            if (TransferStatus.REQUESTED.equals(currentTransferStatus)) {
                TransferStatus transferStatus = (currentContributionFileId == null) ? null : TransferStatus.SENT;
                UpdateContributionRequest updateContributionRequest = updateContributionRequestMapper.map(currentContribution);
                updateContributionRequest.setTransferStatus(transferStatus);
                maatCourtDataService.updateContribution(updateContributionRequest);
            }
            //Revisit the createContribs logic - do we need to change the input?
            return createContribs(calculateContributionDTO);
        } else if (isCreateContributionRequired(calculateContributionDTO, repOrderDTO, currentTransferStatus)) {
            return createContribs(calculateContributionDTO);
        }
        return null;
    }

    public Contribution getCurrentContribution(final CalculateContributionDTO calculateContributionDTO) {
        final Integer contributionId = calculateContributionDTO.getContributionId();
        List<Contribution> contributionsList = new ArrayList<>();
        if (contributionId != null) {
            contributionsList = maatCourtDataService.findContribution(calculateContributionDTO.getRepId(), false);
        }
        return contributionsList.stream().filter(x -> contributionId.equals(x.getId())).findFirst().orElse(null);
    }

    public boolean isCreateContributionRequired(final CalculateContributionDTO calculateContributionDTO,
                                                final RepOrderDTO repOrderDTO,
                                                final TransferStatus currentTransferStatus) {
        return (!TransferStatus.REQUESTED.equals(currentTransferStatus)
                && (contributionService.hasApplicationStatusChanged(repOrderDTO, calculateContributionDTO.getCaseType(), calculateContributionDTO.getApplicationStatus())
                || contributionService.hasCCOutcomeChanged(repOrderDTO.getId())
                || contributionService.isCds15WorkAround(repOrderDTO)));
    }

    public Contribution createContribs(final CalculateContributionDTO calculateContributionDTO) {
        log.info("Inactivate existing Contribution and create a new Contribution");
        CreateContributionRequest createContributionRequest = createContributionRequestMapper.map(calculateContributionDTO);
        if (compareContributionService.compareContribution(calculateContributionDTO) < 2) {
            return maatCourtDataService.createContribution(createContributionRequest);
        } else return null;
    }

    public ApiMaatCalculateContributionResponse calcContribs(final CalculateContributionDTO calculateContributionDTO,
                                                             final ContributionResponseDTO contributionResponseDTO) {
        LocalDate assEffectiveDate = getEffectiveDate(calculateContributionDTO);
        ContributionCalcParametersDTO contributionCalcParametersDTO = maatCourtDataService.getContributionCalcParameters(DateUtil.getLocalDateString(assEffectiveDate));
        CrownCourtOutcome crownCourtOutcome = contributionRulesService.getActiveCCOutcome(calculateContributionDTO.getCrownCourtOutcomeList());
        boolean isContributionRuleApplicable = contributionRulesService.isContributionRuleApplicable(calculateContributionDTO.getCaseType(),
                calculateContributionDTO.getMagCourtOutcome(), crownCourtOutcome);

        BigDecimal annualDisposableIncome = calculateAnnualDisposableIncome(calculateContributionDTO, crownCourtOutcome, isContributionRuleApplicable);
        Integer totalMonths = Constants.N.equals(contributionResponseDTO.getCalcContribs()) ? 0 : contributionCalcParametersDTO.getTotalMonths();

        ApiCalculateContributionRequest apiCalculateContributionRequest = calculateContributionRequestMapper.map(contributionCalcParametersDTO,
                annualDisposableIncome, isUpliftApplied(calculateContributionDTO, contributionResponseDTO),
                calculateContributionDTO.getContributionCap());

        // Revisit the request to pass the offenceType object for Contribs Cap
        ApiCalculateContributionResponse apiCalculateContributionResponse = calculateContributionService.calculateContribution(apiCalculateContributionRequest);
        String effectiveDate = getEffectiveDateByNewWorkReason(calculateContributionDTO, calculateContributionDTO.getContributionCap(), assEffectiveDate);
        return maatCalculateContributionResponseMapper.map(apiCalculateContributionResponse, calculateContributionDTO.getContributionCap(), effectiveDate, totalMonths);
    }

    public BigDecimal calculateAnnualDisposableIncome(final CalculateContributionDTO calculateContributionDTO,
                                                      final CrownCourtOutcome crownCourtOutcome,
                                                      boolean isContributionRuleApplicable) {
        BigDecimal annualDisposableIncome = calculateContributionDTO.getDisposableIncomeAfterCrownHardship();
        if (annualDisposableIncome == null) {
            if (isContributionRuleApplicable) {
                annualDisposableIncome = getAnnualDisposableIncome(calculateContributionDTO);
                Optional<ContributionVariationDTO> contributionVariation = contributionRulesService.getContributionVariation(calculateContributionDTO.getCaseType(), calculateContributionDTO.getMagCourtOutcome(),
                        crownCourtOutcome);

                if (contributionVariation.isPresent()) {
                    annualDisposableIncome = annualDisposableIncome
                            .add(calculateVariationAmount(calculateContributionDTO.getRepId(), contributionVariation.get()));
                }
            } else {
                if (calculateContributionDTO.getTotalAnnualDisposableIncome() != null) {
                    annualDisposableIncome = calculateContributionDTO.getTotalAnnualDisposableIncome();
                } else {
                    annualDisposableIncome = BigDecimal.ZERO;
                }
            }
        }
        return annualDisposableIncome;
    }

    public BigDecimal calculateVariationAmount(final Integer repId,
                                               final ContributionVariationDTO contributionVariation) {
        ApiCalculateHardshipByDetailResponse apiCalculateHardshipByDetailResponse =
                crimeHardshipService.calculateHardshipForDetail(new ApiCalculateHardshipByDetailRequest()
                        .withDetailType(Objects.requireNonNull(HardshipReviewDetailType.getFrom(contributionVariation.getVariation())).toString())
                        .withRepId(repId));
        if ("+".equals(contributionVariation.getVariationRule())) {
            return apiCalculateHardshipByDetailResponse.getHardshipSummary();
        } else return BigDecimal.ZERO;
    }
}