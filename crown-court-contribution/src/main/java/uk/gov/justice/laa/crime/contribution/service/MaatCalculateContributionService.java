package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.common.model.contribution.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.builder.CalculateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.ContributionSummaryMapper;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.MaatCalculateContributionResponseMapper;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
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

        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(calculateContributionDTO.getRepId());
        calculateContributionDTO.setRepOrderDTO(repOrderDTO);

        if (CaseType.APPEAL_CC.equals(calculateContributionDTO.getCaseType())) {
            return appealContributionService.calculateAppealContribution(calculateContributionDTO);
        }
        return getCalculateContributionResponse(calculateContributionDTO, repOrderDTO);
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

        Optional<ApiAssessment> fullAssessment = calculateContributionDTO.getAssessments().stream()
                .filter(it -> it.getAssessmentType() == AssessmentType.FULL).findFirst();
        Optional<ApiAssessment> initAssessment = calculateContributionDTO.getAssessments().stream()
                .filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst();
        String fullResult = fullAssessment.map(assessment -> assessment.getResult().name()).orElse(null);

        String crownCourtOutcome = getCrownCourtOutcome(calculateContributionDTO);

        String magCourtOutcome = (calculateContributionDTO.getMagCourtOutcome() != null) ?
                calculateContributionDTO.getMagCourtOutcome().getOutcome() : null;

        ContributionResponseDTO contributionResponseDTO =
                contributionService.checkContributionsCondition(
                        ContributionRequestDTO.builder()
                                .caseType(calculateContributionDTO.getCaseType())
                                .effectiveDate(calculateContributionDTO.getEffectiveDate())
                                .iojResult(repOrderDTO.getIojResult())
                                .monthlyContribs(calculateContributionDTO.getMonthlyContributions())
                                .fullResult(fullResult)
                                .initResult(initAssessment.map(
                                                assessment -> assessment.getResult().name())
                                        .orElse(null))
                                .magCourtOutcome(magCourtOutcome)
                                .crownCourtOutcome(crownCourtOutcome)
                                .removeContribs(calculateContributionDTO.getRemoveContribs())
                                .build()
                );

        if (Constants.Y.equals(contributionResponseDTO.getDoContribs())) {
            return performContributions(calculateContributionDTO, contributionResponseDTO, fullResult, repOrderDTO);
        }
        return new ApiMaatCalculateContributionResponse();
    }

    public static String getCrownCourtOutcome(CalculateContributionDTO calculateContributionDTO) {
        List<ApiCrownCourtOutcome> crownCourtOutcomeList = calculateContributionDTO.getCrownCourtOutcomeList();

        if (crownCourtOutcomeList == null || crownCourtOutcomeList.isEmpty()) {
            return null;
        }

        ApiCrownCourtOutcome apiCrownCourtOutcome = crownCourtOutcomeList.get(0);
        if (apiCrownCourtOutcome == null || apiCrownCourtOutcome.getOutcome() == null) {
            return null;
        }

        return apiCrownCourtOutcome.getOutcome().getCode();
    }

    public ApiMaatCalculateContributionResponse performContributions(final CalculateContributionDTO calculateContributionDTO,
                                                                     final ContributionResponseDTO contributionResponseDTO,
                                                                     final String fullResult,
                                                                     final RepOrderDTO repOrderDTO) {
        ContributionResult result;

        //Use Calculated Monthly Contributions value - p_application_object.crown_court_overview_object.contributions_object.monthly_contribs > 0 ->
        //No need to calculate contributions for INEL
        log.info("contributionResponseDTO.getCalcContribs() : " + contributionResponseDTO.getCalcContribs());
        log.info("calculateContributionDTO.getId() : " + calculateContributionDTO.getId());
        log.info("calculateContributionDTO.getMonthlyContributions() : " + calculateContributionDTO.getMonthlyContributions());

        if (Constants.Y.equals(contributionResponseDTO.getCalcContribs()) ||
                contributionResponseDTO.getId() != null ||
                (calculateContributionDTO.getMonthlyContributions() != null && calculateContributionDTO.getMonthlyContributions()
                        .compareTo(BigDecimal.ZERO) > 0) ||
                Constants.INEL.equals(fullResult)) {
            log.info("Call calculateContributions");
            result = calculateContributions(calculateContributionDTO, contributionResponseDTO);
        } else {
            log.info("ContributionResult Builder");
            result = ContributionResult.builder()
                    .monthlyAmount(BigDecimal.ZERO)
                    .contributionCap(BigDecimal.ZERO)
                    .upfrontAmount(BigDecimal.ZERO)
                    .build();
        }
        log.info("Call verifyAndCreateContributions");

        Contribution createdContribution = verifyAndCreateContributions(calculateContributionDTO, repOrderDTO, result);

        return maatCalculateContributionResponseMapper.map(result, createdContribution, contributionResponseDTO);
    }

    private boolean shouldCreateContributions(ContributionResult result, CalculateContributionDTO calculateContributionDTO) {
        return (result.monthlyAmount() != null && result.monthlyAmount().compareTo(calculateContributionDTO.getMonthlyContributions()) != 0)
                || (result.effectiveDate() != null && !result.effectiveDate()
                .equals(calculateContributionDTO.getEffectiveDate()));
    }

    public Contribution verifyAndCreateContributions(final CalculateContributionDTO calculateContributionDTO,
                                                     final RepOrderDTO repOrderDTO,
                                                     final ContributionResult result) {

        if (result != null && (shouldCreateContributions(result, calculateContributionDTO)
                || (repOrderDTO != null && isCreateContributionRequired(calculateContributionDTO, repOrderDTO)))) {
            return createContributions(calculateContributionDTO, result);
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

    public Contribution createContributions(final CalculateContributionDTO calculateContributionDTO,
                                            ContributionResult result) {
        log.info("Inactivate existing Contribution and create a new Contribution");
        if (compareContributionService.shouldCreateContribution(calculateContributionDTO, result)) {
            CreateContributionRequest createContributionRequest =
                    createContributionRequestMapper.map(calculateContributionDTO, result);
            return maatCourtDataService.createContribution(createContributionRequest);
        } else {
            return null;
        }
    }

    public ContributionResult calculateContributions(final CalculateContributionDTO calculateContributionDTO,
                                                     final ContributionResponseDTO contributionResponseDTO) {
        log.info("calculateContributionDTO : " + calculateContributionDTO);
        log.info("contributionResponseDTO : " + contributionResponseDTO);

        LocalDate assEffectiveDate = getEffectiveDate(calculateContributionDTO);
        log.info("assEffectiveDate : " + assEffectiveDate);

        ContributionCalcParametersDTO contributionCalcParametersDTO =
                maatCourtDataService.getContributionCalcParameters(DateUtil.getLocalDateString(assEffectiveDate));
        CrownCourtOutcome crownCourtOutcome =
                contributionRulesService.getActiveCCOutcome(calculateContributionDTO.getCrownCourtOutcomeList());

        BigDecimal annualDisposableIncome = calculateAnnualDisposableIncome(calculateContributionDTO, crownCourtOutcome);
        int totalMonths = Constants.N.equals(
                contributionResponseDTO.getCalcContribs()) ? 0 : contributionCalcParametersDTO.getTotalMonths();

        ApiCalculateContributionRequest apiCalculateContributionRequest =
                calculateContributionRequestMapper.map(contributionCalcParametersDTO,
                        annualDisposableIncome, isUpliftApplied(calculateContributionDTO,
                                contributionResponseDTO
                        ),
                        calculateContributionDTO.getContributionCap()
                );
        log.info("apiCalculateContributionRequest : " + apiCalculateContributionRequest);


        Optional<ApiAssessment> fullAssessment = calculateContributionDTO.getAssessments().stream()
                .filter(it -> it.getAssessmentType() == AssessmentType.FULL).findFirst();
        String fullResult = fullAssessment.map(assessment -> assessment.getResult().name()).orElse(null);

        log.info("fullResult : " + fullResult);


        ApiCalculateContributionResponse apiCalculateContributionResponse = null;
        //if (Constants.INEL.equals(fullResult) && !apiCalculateContributionRequest.getUpliftApplied()) {
        if (Constants.INEL.equals(fullResult)) {
            log.info("INEL - No need to calculate contributions");
            apiCalculateContributionResponse = new ApiCalculateContributionResponse();
            apiCalculateContributionResponse.setUpfrontContributions(BigDecimal.ZERO);
            apiCalculateContributionResponse.setMonthlyContributions(BigDecimal.ZERO);
            apiCalculateContributionResponse.setUpliftApplied(Constants.N);
            apiCalculateContributionResponse.setBasedOn(null);
            totalMonths = 0;
        } else {
            log.info("ELSE BLOCK NOT INEL - calculate contributions");

            // Revisit the request to pass the offenceType object for Contribs Cap
            apiCalculateContributionResponse =
                    calculateContributionService.calculateContribution(apiCalculateContributionRequest);
        }

        String effectiveDate =
                getEffectiveDateByNewWorkReason(calculateContributionDTO, calculateContributionDTO.getContributionCap(),
                        assEffectiveDate
                );
        log.info("effectiveDate : " + effectiveDate);


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
                                                      final CrownCourtOutcome crownCourtOutcome) {
        if (calculateContributionDTO.getDisposableIncomeAfterCrownHardship() != null) {
            return calculateContributionDTO.getDisposableIncomeAfterCrownHardship();
        }

        boolean isContributionRuleApplicable =
                contributionRulesService.isContributionRuleApplicable(calculateContributionDTO.getCaseType(),
                        calculateContributionDTO.getMagCourtOutcome(),
                        crownCourtOutcome
                );
        if (isContributionRuleApplicable) {
            return getAnnualDisposableIncome(calculateContributionDTO)
                    .add(calculateVariationAmount(calculateContributionDTO.getRepId()));
        }

        if (calculateContributionDTO.getTotalAnnualDisposableIncome() != null) {
            return calculateContributionDTO.getTotalAnnualDisposableIncome();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateVariationAmount(final Integer repId) {
        ApiCalculateHardshipByDetailResponse apiCalculateHardshipByDetailResponse =
                crimeHardshipService.calculateHardshipForDetail(new ApiCalculateHardshipByDetailRequest()
                        .withDetailType(HardshipReviewDetailType.SOL_COSTS.toString())
                        .withRepId(repId));
        return apiCalculateHardshipByDetailResponse.getHardshipSummary();
    }
}