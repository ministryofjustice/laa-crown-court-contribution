package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.builder.*;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.*;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.ContributionResult;
import uk.gov.justice.laa.crime.contribution.util.DateUtil;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.contribution.AssessmentType;

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

    private static boolean isUpliftApplied(CalculateContributionDTO calculateContributionDTO,
                                           ContributionResponseDTO contributionResponseDTO) {
        return contributionResponseDTO.getUpliftCote() != null &&
                calculateContributionDTO.getDateUpliftApplied() != null &&
                calculateContributionDTO.getDateUpliftRemoved() == null;
    }

    public static BigDecimal getAnnualDisposableIncome(final CalculateContributionDTO calculateContributionDTO) {
        if ((calculateContributionDTO.getDisposableIncomeAfterMagHardship() != null)) {
            return calculateContributionDTO.getDisposableIncomeAfterMagHardship();
        } else if (calculateContributionDTO.getTotalAnnualDisposableIncome() != null) {
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
            } else {
                return assEffectiveDate.toString();
            }
        } else {
            if (calculateContributionDTO.getEffectiveDate() == null) {
                return assEffectiveDate.toString();
            } else {
                return calculateContributionDTO.getEffectiveDate().toString();
            }
        }
    }

    public static NewWorkReason getNewWorkReason(final CalculateContributionDTO calculateContributionDTO) {
        return calculateContributionDTO.getAssessments().stream()
                .filter(it -> it.getAssessmentType() == AssessmentType.PASSPORT).findFirst()
                .map(ApiAssessment::getNewWorkReason)
                .orElse(calculateContributionDTO.getAssessments().stream()
                                .filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst()
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
        Optional<ApiAssessment> passAssessment = calculateContributionDTO.getAssessments().stream()
                .filter(it -> it.getAssessmentType() == AssessmentType.PASSPORT).findFirst();
        LocalDateTime assessmentDate = passAssessment.map(ApiAssessment::getAssessmentDate).orElse(null);
        if (assessmentDate == null) {
            Optional<ApiAssessment> fullAssessment = calculateContributionDTO.getAssessments().stream()
                    .filter(it -> it.getAssessmentType() == AssessmentType.FULL).findFirst();
            assessmentDate = fullAssessment.map(ApiAssessment::getAssessmentDate).orElse(null);
            if (assessmentDate == null) {
                Optional<ApiAssessment> initAssessment = calculateContributionDTO.getAssessments().stream()
                        .filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst();
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

    public ApiMaatCalculateContributionResponse calculateContribution(
            CalculateContributionDTO calculateContributionDTO) {
        ApiMaatCalculateContributionResponse response;

        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(calculateContributionDTO.getRepId());
        calculateContributionDTO.setRepOrderDTO(repOrderDTO);

        if (CaseType.APPEAL_CC.equals(calculateContributionDTO.getCaseType())) {
            response = appealContributionService.calculateAppealContribution(calculateContributionDTO);
        } else {
            response = getCalculateContributionResponse(calculateContributionDTO, repOrderDTO);
        }
        log.info("response:  {}", response);

        return response;
    }

    public List<ApiContributionSummary> getContributionSummaries(final int repId) {
        List<ContributionsSummaryDTO> contribSummaryList = maatCourtDataService.getContributionsSummary(repId);
        return contribSummaryList != null ? contribSummaryList.stream().map(contributionSummaryMapper::map)
                .toList() : List.of();
    }

    public ApiMaatCalculateContributionResponse getCalculateContributionResponse(
            final CalculateContributionDTO calculateContributionDTO,
            final RepOrderDTO repOrderDTO) {
        log.info("Start getCalculateContributionResponse");
        ApiMaatCalculateContributionResponse response = new ApiMaatCalculateContributionResponse();

        Optional<ApiAssessment> fullAssessment = calculateContributionDTO.getAssessments().stream()
                .filter(it -> it.getAssessmentType() == AssessmentType.FULL).findFirst();
        Optional<ApiAssessment> initAssessment = calculateContributionDTO.getAssessments().stream()
                .filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst();
        String fullResult = fullAssessment.map(assessment -> assessment.getResult().name()).orElse(null);

        String outcome = null;
        if (null != calculateContributionDTO.getCrownCourtOutcomeList()
                && !calculateContributionDTO.getCrownCourtOutcomeList().isEmpty()) {
            log.info("getCrownCourtOutcomeList");
            ApiCrownCourtOutcome apiCrownCourtOutcome = calculateContributionDTO.getCrownCourtOutcomeList().get(0);
            log.info("apiCrownCourtOutcome--" + apiCrownCourtOutcome);
            if (null != apiCrownCourtOutcome && null != apiCrownCourtOutcome.getOutcome()) {
                log.info("apiCrownCourtOutcome--" + apiCrownCourtOutcome.getOutcome());
                outcome = apiCrownCourtOutcome.getOutcome().getCode();
            }
        }

        log.info("outcome--" + outcome);

        String msgCourtOutcome = null;

        if (null != calculateContributionDTO.getMagCourtOutcome()) {
            msgCourtOutcome = calculateContributionDTO.getMagCourtOutcome().getOutcome();
        }

        log.info("courtOutcome--" + msgCourtOutcome);

        ContributionResponseDTO contributionResponseDTO =
                contributionService.checkContribsCondition(
                        ContributionRequestDTO.builder()
                                .caseType(calculateContributionDTO.getCaseType())
                                .effectiveDate(calculateContributionDTO.getEffectiveDate())
                                .iojResult(repOrderDTO.getIojResult())
                                .monthlyContribs(calculateContributionDTO.getMonthlyContributions())
                                .fullResult(fullResult)
                                .initResult(initAssessment.map(
                                                assessment -> assessment.getResult().name())
                                                    .orElse(null))
                                .magCourtOutcome(msgCourtOutcome)
                                .crownCourtOutcome(outcome)
                                .removeContribs(calculateContributionDTO.getRemoveContribs())
                                .build()
                );

        if (Constants.Y.equals(contributionResponseDTO.getDoContribs())) {
            response = doContribs(calculateContributionDTO, contributionResponseDTO, fullResult, repOrderDTO);
        }
        return response;
    }

    public ApiMaatCalculateContributionResponse doContribs(final CalculateContributionDTO calculateContributionDTO,
                                                           final ContributionResponseDTO contributionResponseDTO,
                                                           final String fullResult,
                                                           final RepOrderDTO repOrderDTO) {
        ApiMaatCalculateContributionResponse response;
        log.info("doContribs");
        log.info("doContribs Monthly Contribs {} " + calculateContributionDTO.getMonthlyContributions());
        log.info("doContribs UpFront Contribs {} " + calculateContributionDTO.getUpfrontContributions());


        ContributionResult result = null;
        //Use Calculated Monthly Contributions value - p_application_object.crown_court_overview_object.contributions_object.monthly_contribs > 0 ->
        if (Constants.Y.equals(contributionResponseDTO.getCalcContribution()) ||
                contributionResponseDTO.getId() != null ||
                (calculateContributionDTO.getMonthlyContributions() != null && calculateContributionDTO.getMonthlyContributions()
                        .compareTo(BigDecimal.ZERO) > 0) ||
                Constants.INEL.equals(fullResult)) {
            log.info("doContribs Calc Contribs");
            result = calcContribs(calculateContributionDTO, contributionResponseDTO);
        } else if (calculateContributionDTO.getMonthlyContributions() != null) {
            log.info("doContribs Set Contribs");
            result = ContributionResult.builder()
                    .monthlyAmount(BigDecimal.ZERO)
                    .contributionCap(BigDecimal.ZERO)
                    .upfrontAmount(BigDecimal.ZERO)
                    .build();
        }

        log.info("Calling  verifyAndCreateContribs");
        Contribution createdContribution = verifyAndCreateContribs(calculateContributionDTO, repOrderDTO, result);
        log.info("End Calling  verifyAndCreateContribs");

        response = new ApiMaatCalculateContributionResponse()
                .withContributionCap(result.contributionCap())
                .withEffectiveDate(DateUtil.convertDateToDateTime(result.effectiveDate()))
                .withTotalMonths(result.totalMonths())
                .withMonthlyContributions(result.monthlyAmount())
                .withUpfrontContributions(result.upfrontAmount())
                .withUpliftApplied(result.isUplift() ? "Y" : "N")
                .withContributionId(createdContribution != null ? createdContribution.getId() : null)
                .withCalcDate(createdContribution != null ? DateUtil.convertDateToDateTime(
                        createdContribution.getCalcDate()) : null)
                .withBasedOn(result.basedOn());

        if (contributionResponseDTO.getId() != null) {
            response.setProcessActivity(true);
        }
        log.info("End doContribs");
        return response;
    }

    private boolean shouldCreateContribs(ContributionResult result, CalculateContributionDTO calculateContributionDTO) {
        return (result.monthlyAmount() != null && result.monthlyAmount()
                .compareTo(calculateContributionDTO.getMonthlyContributions()) != 0)
                || (result.effectiveDate() != null && !result.effectiveDate()
                .equals(calculateContributionDTO.getEffectiveDate()));
    }

    public Contribution verifyAndCreateContribs(final CalculateContributionDTO calculateContributionDTO,
                                                final RepOrderDTO repOrderDTO,
                                                final ContributionResult result) {

        if (result != null && (shouldCreateContribs(result, calculateContributionDTO)
                || (repOrderDTO != null && isCreateContributionRequired(calculateContributionDTO, repOrderDTO)))) {
            return createContribs(calculateContributionDTO, result);
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
                                                final RepOrderDTO repOrderDTO) {

        return (contributionService.hasApplicationStatusChanged(
                repOrderDTO, calculateContributionDTO.getCaseType(), calculateContributionDTO.getApplicationStatus())
                || contributionService.hasCCOutcomeChanged(repOrderDTO.getId())
                || contributionService.isCds15WorkAround(repOrderDTO));
    }

    public Contribution createContribs(final CalculateContributionDTO calculateContributionDTO,
                                       ContributionResult result) {
        log.info("Inactivate existing Contribution and create a new Contribution");
        if (compareContributionService.compareContribution(calculateContributionDTO, result) < 2) {
            CreateContributionRequest createContributionRequest =
                    createContributionRequestMapper.map(calculateContributionDTO, result);
            log.info("Calling createContribution");
            log.info("Calling createContribution request --> " + createContributionRequest);
            return maatCourtDataService.createContribution(createContributionRequest);
        } else {
            return null;
        }
    }

    public ContributionResult calcContribs(final CalculateContributionDTO calculateContributionDTO,
                                           final ContributionResponseDTO contributionResponseDTO) {
        LocalDate assEffectiveDate = getEffectiveDate(calculateContributionDTO);
        ContributionCalcParametersDTO contributionCalcParametersDTO =
                maatCourtDataService.getContributionCalcParameters(DateUtil.getLocalDateString(assEffectiveDate));
        CrownCourtOutcome crownCourtOutcome =
                contributionRulesService.getActiveCCOutcome(calculateContributionDTO.getCrownCourtOutcomeList());
        boolean isContributionRuleApplicable =
                contributionRulesService.isContributionRuleApplicable(calculateContributionDTO.getCaseType(),
                                                                      calculateContributionDTO.getMagCourtOutcome(),
                                                                      crownCourtOutcome
                );

        BigDecimal annualDisposableIncome = calculateAnnualDisposableIncome(calculateContributionDTO, crownCourtOutcome,
                                                                            isContributionRuleApplicable
        );
        int totalMonths = Constants.N.equals(
                contributionResponseDTO.getCalcContribs()) ? 0 : contributionCalcParametersDTO.getTotalMonths();

        ApiCalculateContributionRequest apiCalculateContributionRequest =
                calculateContributionRequestMapper.map(contributionCalcParametersDTO,
                                                       annualDisposableIncome, isUpliftApplied(calculateContributionDTO,
                                                                                               contributionResponseDTO
                        ),
                                                       calculateContributionDTO.getContributionCap()
                );

        // Revisit the request to pass the offenceType object for Contribs Cap
        ApiCalculateContributionResponse apiCalculateContributionResponse =
                calculateContributionService.calculateContribution(apiCalculateContributionRequest);
        String effectiveDate =
                getEffectiveDateByNewWorkReason(calculateContributionDTO, calculateContributionDTO.getContributionCap(),
                                                assEffectiveDate
                );

        return ContributionResult.builder()
                .totalMonths(totalMonths)
                .totalAnnualDisposableIncome(annualDisposableIncome)
                .monthlyAmount(apiCalculateContributionResponse.getMonthlyContributions())
                .upfrontAmount(apiCalculateContributionResponse.getUpfrontContributions())
                .isUplift(Constants.Y.equals(apiCalculateContributionResponse.getUpliftApplied()))
                .basedOn(apiCalculateContributionResponse.getBasedOn())
                .effectiveDate(DateUtil.parse(effectiveDate))
                .contributionCap(calculateContributionDTO.getContributionCap())
                .build();
    }

    public BigDecimal calculateAnnualDisposableIncome(final CalculateContributionDTO calculateContributionDTO,
                                                      final CrownCourtOutcome crownCourtOutcome,
                                                      boolean isContributionRuleApplicable) {
        BigDecimal annualDisposableIncome = calculateContributionDTO.getDisposableIncomeAfterCrownHardship();
        if (annualDisposableIncome == null) {
            if (isContributionRuleApplicable) {
                annualDisposableIncome = getAnnualDisposableIncome(calculateContributionDTO);
                Optional<ContributionVariationDTO> contributionVariation =
                        contributionRulesService.getContributionVariation(calculateContributionDTO.getCaseType(),
                                                                          calculateContributionDTO.getMagCourtOutcome(),
                                                                          crownCourtOutcome
                        );

                if (contributionVariation.isPresent()) {
                    annualDisposableIncome = annualDisposableIncome
                            .add(calculateVariationAmount(calculateContributionDTO.getRepId(),
                                                          contributionVariation.get()
                            ));
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
                                                                        .withDetailType(Objects.requireNonNull(
                                                                                        HardshipReviewDetailType.getFrom(
                                                                                                contributionVariation.getVariation()))
                                                                                                .toString())
                                                                        .withRepId(repId));
        if ("+".equals(contributionVariation.getVariationRule())) {
            return apiCalculateHardshipByDetailResponse.getHardshipSummary();
        } else {
            return BigDecimal.ZERO;
        }
    }
}