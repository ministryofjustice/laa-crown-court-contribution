package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.builder.ContributionSummaryMapper;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.UpdateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.*;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.*;
import uk.gov.justice.laa.crime.contribution.util.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculateContributionService {

    private final MaatCourtDataService maatCourtDataService;
    private final CrimeHardshipService crimeHardshipService;

    private final AppealContributionService appealContributionService;
    private final CompareContributionService compareContributionService;
    private final ContributionRulesService contributionRulesService;
    private final CreateContributionRequestMapper createContributionRequestMapper;
    private final ContributionService contributionService;
    private final ContributionSummaryMapper contributionSummaryMapper;

    public CalculateContributionResponse calculateContribution(CalculateContributionDTO calculateContributionDTO, String laaTransactionId) {
        CalculateContributionResponse response;

        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(calculateContributionDTO.getRepId(), laaTransactionId);
        calculateContributionDTO.setRepOrderDTO(repOrderDTO);

        if (CaseType.APPEAL_CC.equals(calculateContributionDTO.getCaseType())) {
            response = appealContributionService.calculateAppealContribution(calculateContributionDTO, laaTransactionId);
        } else {
            response = getCalculateContributionResponse(calculateContributionDTO, laaTransactionId, repOrderDTO );
        }

        List<ContributionSummary> contributionSummary = getContributionSummaries(calculateContributionDTO, laaTransactionId);
        response.setContributionsSummary(contributionSummary);

        return response;
    }



    public CalculateContributionResponse getCalculateContributionResponse(CalculateContributionDTO calculateContributionDTO, String laaTransactionId, RepOrderDTO repOrderDTO) {
        CalculateContributionResponse response = new CalculateContributionResponse();
        boolean isReassessment = contributionService.checkReassessment(repOrderDTO, laaTransactionId);

        Optional<Assessment> fullAssessment = calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.FULL).findFirst();
        Optional<Assessment> initAssessment = calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst();
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
            response = doContribs(calculateContributionDTO, laaTransactionId, contributionResponseDTO, fullResult, isReassessment);
        }
        return response;
    }



    public CalculateContributionResponse doContribs(CalculateContributionDTO calculateContributionDTO, String laaTransactionId,
                                                     ContributionResponseDTO contributionResponseDTO, String fullResult, boolean isReassessment) {
        CalculateContributionResponse response = new CalculateContributionResponse();
        Contribution contribution = new Contribution();
        final Integer contributionId = calculateContributionDTO.getId();

        if (Constants.Y.equals(contributionResponseDTO.getCalcContribution()) ||
                calculateContributionDTO.getMonthlyContributions().compareTo(BigDecimal.ZERO) > 0 ||
                Constants.INEL.equals(fullResult)) {
            response = calcContribs(calculateContributionDTO, contributionResponseDTO, laaTransactionId);
        } else if (calculateContributionDTO.getMonthlyContributions() != null) {
            response.setMonthlyContributions(BigDecimal.ZERO);
            response.setContributionCap(BigDecimal.ZERO);
            response.setUpfrontContributions(BigDecimal.ZERO);
        }

        if (contributionId != null) {
            List<Contribution> contributionsList = maatCourtDataService.findContribution(calculateContributionDTO.getRepId(), laaTransactionId, false);
            contribution = contributionsList.stream().filter(x -> contributionId.equals(x.getId())).findFirst().get();
        }

        BigDecimal monthlyContributions = (calculateContributionDTO.getMonthlyContributions() != null) ? calculateContributionDTO.getMonthlyContributions() : BigDecimal.valueOf(-1);

        if (TransferStatus.REQUESTED.equals(contribution.getTransferStatus())) {
            maatCourtDataService.updateContribution(new UpdateContributionRequestMapper().map(calculateContributionDTO), laaTransactionId);
        }
        contribution = createContribs(calculateContributionDTO, calculateContributionDTO.getLaaTransactionId());

        List<Contribution> contributionsList = maatCourtDataService.findContribution(calculateContributionDTO.getRepId(), laaTransactionId, true);
        if (!contributionsList.isEmpty()) {
            contribution = contributionsList.get(0);
        }

        if (((calculateContributionDTO.getMonthlyContributions().compareTo(contribution.getMonthlyContributions()) != 0
                || calculateContributionDTO.getUpfrontContributions().compareTo(contribution.getUpfrontContributions()) != 0
                || (!calculateContributionDTO.getEffectiveDate().equals(contribution.getEffectiveDate())
                && BigDecimal.ZERO.compareTo(calculateContributionDTO.getMonthlyContributions()) < 0)
                || contributionService.hasCCOutcomeChanged(calculateContributionDTO.getRepId(), laaTransactionId))
                && Arrays.asList("SENT FOR TRIAL", "COMMITTED FOR TRIAL", "APPEAL TO CC")
                .contains(calculateContributionDTO.getMagCourtOutcome().getOutcome()) || contributionService.hasContributionBeenSent(calculateContributionDTO.getRepId(), laaTransactionId))
        ) {
            maatCourtDataService.updateContribution(new UpdateContributionRequest()
                    .withId(contribution.getId())
                    .withTransferStatus(TransferStatus.REQUESTED)
                    .withUserModified(calculateContributionDTO.getUserModified()), laaTransactionId);
        }

        if (contributionResponseDTO.getTemplate() != null && "Y".equals(contribution.getCalculationRan())) {
            if ("Y".equals(calculateContributionDTO.getUpliftApplied())) {
                contributionResponseDTO.setTemplate(contributionResponseDTO.getUpliftCote());
            } else {
                if (isReassessment) {
                    contributionResponseDTO.setTemplate(contributionResponseDTO.getReassessmentCoteId());
                }
            }
        }

        //ToDo - Call Matrix Activity
        maatCourtDataService.updateContribution(new UpdateContributionRequestMapper().map(calculateContributionDTO), laaTransactionId);
        return response;
    }


    public List<ContributionSummary> getContributionSummaries(CalculateContributionDTO calculateContributionDTO, String laaTransactionId) {
        List<ContributionsSummaryDTO> contribSummaryList = maatCourtDataService.getContributionsSummary(calculateContributionDTO.getRepId(), laaTransactionId);
        return contribSummaryList.stream().map(x -> contributionSummaryMapper.map(x)).toList();
    }


    public Contribution createContribs(CalculateContributionDTO calculateContributionDTO, String laaTransactionId) {
        log.info("Inactivate existing Contribution and create a new Contribution");
        CreateContributionRequest createContributionRequest = createContributionRequestMapper.map(calculateContributionDTO);
        if(compareContributionService.compareContribution(calculateContributionDTO) < 2) {
            Contribution contribution = maatCourtDataService.createContribution(createContributionRequest, laaTransactionId);
            contribution.setCalculationRan(Constants.Y);
            return contribution;
        } else
            return null;
    }

    public CalculateContributionResponse calcContribs(CalculateContributionDTO calculateContributionDTO, ContributionResponseDTO contributionResponseDTO, String laaTransactionId) {
        CalculateContributionResponse response = new CalculateContributionResponse();
        LocalDate assEffectiveDate = getEffectiveDate(calculateContributionDTO);
        ContributionCalcParametersDTO contributionCalcParametersDTO = maatCourtDataService.getContributionCalcParameters(assEffectiveDate.toString(), laaTransactionId);
        CrownCourtOutcome crownCourtOutcome = contributionRulesService.getActiveCCOutcome(calculateContributionDTO.getCrownCourtSummary());
        boolean isContributionRuleApplicable = contributionRulesService.isContributionRuleApplicable(calculateContributionDTO.getCaseType(),
                calculateContributionDTO.getMagCourtOutcome(), crownCourtOutcome);

        BigDecimal annualDisposableIncome = calculateAnnualDisposableIncome(calculateContributionDTO, laaTransactionId, crownCourtOutcome, isContributionRuleApplicable);

        if (contributionResponseDTO.getUpliftCote() != null &&
                calculateContributionDTO.getDateUpliftApplied() != null &&
                calculateContributionDTO.getDateUpliftRemoved() == null) {
            BigDecimal monthlyContributions = calculateUpliftedMonthlyAmount(annualDisposableIncome, contributionCalcParametersDTO);
            response.setMonthlyContributions(monthlyContributions);
            response.setUpliftApplied(Constants.Y);
        } else if (contributionResponseDTO.getCalcContribs().equals(Constants.N)) {
            response.setMonthlyContributions(BigDecimal.ZERO);
            response.setUpfrontContributions(BigDecimal.ZERO);
            response.setUpliftApplied(Constants.N);
            response.setBasedOn(null);
            response.setTotalMonths(0);
        } else {
            BigDecimal monthlyContributions = calculateDisposableContribution(annualDisposableIncome, contributionCalcParametersDTO);
            response.setUpliftApplied(Constants.N);
            if (monthlyContributions.compareTo(calculateContributionDTO.getContributionCap()) > 0) {
                response.setMonthlyContributions(calculateContributionDTO.getContributionCap());
                response.setBasedOn("Offence Type");
            } else {
                response.setMonthlyContributions(monthlyContributions);
                response.setBasedOn("Means");
            }
            response.setUpfrontContributions(calculateUpfrontContributions(calculateContributionDTO, contributionCalcParametersDTO));
        }

        response.setContributionCap(calculateContributionDTO.getContributionCap()); // TODO refactor the request to pass the offenceType object for Contribs Cap
        response.setEffectiveDate(getEffectiveDateByNewWorkReason(calculateContributionDTO, response.getMonthlyContributions(), assEffectiveDate));
        return response;
    }

    public BigDecimal calculateAnnualDisposableIncome(CalculateContributionDTO calculateContributionDTO, String laaTransactionId, CrownCourtOutcome crownCourtOutcome, boolean isContributionRuleApplicable) {
        BigDecimal annualDisposableIncome = calculateContributionDTO.getDisposableIncomeAfterCrownHardship();
        if (isContributionRuleApplicable) {
            annualDisposableIncome = getAnnualDisposableIncome(calculateContributionDTO, annualDisposableIncome);
            Optional<ContributionVariationDTO> contributionVariation = contributionRulesService.getContributionVariation(calculateContributionDTO.getCaseType(), calculateContributionDTO.getMagCourtOutcome(),
                    crownCourtOutcome);

            if (contributionVariation.isPresent()) {
                annualDisposableIncome = annualDisposableIncome
                        .add(calculateVariationAmount(calculateContributionDTO.getRepId(), laaTransactionId, contributionVariation.get()));
            }
        } else {
            if (annualDisposableIncome == null) {
                if (calculateContributionDTO.getTotalAnnualDisposableIncome() != null) {
                    annualDisposableIncome = calculateContributionDTO.getTotalAnnualDisposableIncome();
                } else annualDisposableIncome = BigDecimal.ZERO;
            }
        }
        return annualDisposableIncome;
    }

    public BigDecimal calculateVariationAmount(final Integer repId,
                                                final String laaTransactionId,
                                                final ContributionVariationDTO contributionVariation) {
        ApiCalculateHardshipByDetailResponse apiCalculateHardshipByDetailResponse =
                crimeHardshipService.calculateHardshipForDetail(new ApiCalculateHardshipByDetailRequest()
                        .withDetailType(contributionVariation.getVariation())
                        .withRepId(repId)
                        .withLaaTransactionId(laaTransactionId));
        if ("+".equals(contributionVariation.getVariationRule())) {
            return apiCalculateHardshipByDetailResponse.getHardshipSummary();
        } else return BigDecimal.ZERO;
    }

    public static BigDecimal getAnnualDisposableIncome(final CalculateContributionDTO calculateContributionDTO, final BigDecimal annualDisposableIncome) {
        if (annualDisposableIncome == null) {
            if ((calculateContributionDTO.getDisposableIncomeAfterMagHardship() != null)) {
                return calculateContributionDTO.getDisposableIncomeAfterMagHardship();
            } else {
                if (calculateContributionDTO.getTotalAnnualDisposableIncome() != null) {
                    return calculateContributionDTO.getTotalAnnualDisposableIncome();
                } else return BigDecimal.ZERO;
            }
        }
        return annualDisposableIncome;
    }

    public static String getEffectiveDateByNewWorkReason(final CalculateContributionDTO calculateContributionDTO, final BigDecimal monthlyContributions, final LocalDate assEffectiveDate) {
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
                .map(Assessment::getNewWorkReason)
                .orElse(calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst()
                        .map(Assessment::getNewWorkReason).orElse(null));
    }

    /**
     * This method calculates the upfront contributions based on the below logic:
     * //        p_application_object.crown_court_overview_object.contributions_object.upfront_contribs
     * //                      := least(p_application_object.crown_court_overview_object.contributions_object.monthly_contribs * v_UPFRONT_TOTAL_MONTHS
     * //                ,p_application_object.offence_type_object.contribs_cap);
     */
    public static BigDecimal calculateUpfrontContributions(final CalculateContributionDTO calculateContributionDTO, final ContributionCalcParametersDTO contributionCalcParametersDTO) {
        BigDecimal upfrontContribution = calculateContributionDTO.getMonthlyContributions().multiply(BigDecimal.valueOf(contributionCalcParametersDTO.getUpfrontTotalMonths()));
        if (upfrontContribution.compareTo(calculateContributionDTO.getContributionCap()) < 0) {
            return upfrontContribution;
        } else return calculateContributionDTO.getContributionCap();
    }

    public static BigDecimal calculateDisposableContribution(final BigDecimal annualDisposableIncome, final ContributionCalcParametersDTO contributionCalcParametersDTO) {
        BigDecimal monthlyContributionsCalc = annualDisposableIncome.divide(BigDecimal.valueOf(12), RoundingMode.FLOOR)
                .multiply(contributionCalcParametersDTO.getDisposableIncomePercent())
                .divide(BigDecimal.valueOf(100), RoundingMode.FLOOR);
        BigDecimal monthlyContributions = (monthlyContributionsCalc.compareTo(BigDecimal.ZERO) > 0) ? monthlyContributionsCalc : BigDecimal.ZERO;

        if (monthlyContributions.compareTo(contributionCalcParametersDTO.getMinimumMonthlyAmount()) < 0) {
            return BigDecimal.ZERO;
        } else return monthlyContributions;
    }

    /**
     * This method calculates the uplifted monthly amount based on the following logic:
     * //        p_application_object.crown_court_overview_object.contributions_object.monthly_contribs
     * //               := floor(greatest(
     * //               (v_annual_disposable_income/12)  * (v_UPLIFTED_INCOME_PERCENT / 100), v_MIN_UPLIFTED_MONTHLY_AMOUNT)
     * //               );
     */
    public static BigDecimal calculateUpliftedMonthlyAmount(final BigDecimal annualDisposableIncome, final ContributionCalcParametersDTO contributionCalcParametersDTO) {
        BigDecimal monthlyContributionsCalc = annualDisposableIncome.divide(BigDecimal.valueOf(12), RoundingMode.FLOOR)
                .multiply(contributionCalcParametersDTO.getUpliftedIncomePercent())
                .divide(BigDecimal.valueOf(100), RoundingMode.FLOOR);
        if (monthlyContributionsCalc.compareTo(contributionCalcParametersDTO.getMinUpliftedMonthlyAmount()) > 0) {
            return monthlyContributionsCalc;
        } else {
            return contributionCalcParametersDTO.getMinUpliftedMonthlyAmount();
        }
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
        Optional<Assessment> passAssessment = calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.PASSPORT).findFirst();
        LocalDateTime assessmentDate = passAssessment.map(Assessment::getAssessmentDate).orElse(null);
        if (assessmentDate == null) {
            Optional<Assessment> fullAssessment = calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.FULL).findFirst();
            assessmentDate = fullAssessment.map(Assessment::getAssessmentDate).orElse(null);
            if (assessmentDate == null) {
                Optional<Assessment> initAssessment = calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst();
                assessmentDate = initAssessment.map(Assessment::getAssessmentDate).orElse(null);
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
}