package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    public CalculateContributionResponse calculateContribution(ContributionDTO contributionDTO, String laaTransactionId) {
        CalculateContributionResponse response = new CalculateContributionResponse();
        Contribution contribution = new Contribution();
        final Integer contributionId = contributionDTO.getId();

        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(contributionDTO.getRepId(), laaTransactionId);
        contributionDTO.setRepOrderDTO(repOrderDTO);

        if (CaseType.APPEAL_CC.equals(contributionDTO.getCaseType())) {
            // TODO - refactor appealContribs to return response OR map the response from the appealContribs to response object
            appealContributionService.calculateAppealContribution(contributionDTO, laaTransactionId);
        } else {
            boolean isReassessment = contributionService.checkReassessment(repOrderDTO, laaTransactionId);

            Optional<Assessment> fullAssessment = contributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.FULL).findFirst();
            Optional<Assessment> initAssessment = contributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst();

            String fullResult = fullAssessment.map(assessment -> assessment.getResult().name()).orElse(null);

            ContributionResponseDTO contributionResponseDTO = contributionService.checkContribsCondition(ContributionRequestDTO.builder()
                    .caseType(contributionDTO.getCaseType())
                    .effectiveDate(contributionDTO.getEffectiveDate())
                    .monthlyContribs(contributionDTO.getMonthlyContributions())
                    .fullResult(fullResult)
                    .initResult(initAssessment.map(assessment -> assessment.getResult().name()).orElse(null))
                    .removeContribs(contributionDTO.getRemoveContribs())
                    .build());

            if (Constants.Y.equals(contributionResponseDTO.getDoContribs())) {
                if (Constants.Y.equals(contributionResponseDTO.getCalcContribution()) ||
                        contributionDTO.getMonthlyContributions().compareTo(BigDecimal.ZERO) > 0 ||
                        Constants.INEL.equals(fullResult)) {
                    response = calcContribs(contributionDTO, contributionResponseDTO, laaTransactionId);
                } else if (contributionDTO.getMonthlyContributions() != null) {
                    response.setMonthlyContributions(BigDecimal.ZERO);
                    response.setContributionCap(BigDecimal.ZERO);
                    response.setUpfrontContributions(BigDecimal.ZERO);
                }

                if (contributionId != null) {
                    List<Contribution> contributionsList = maatCourtDataService.findContribution(contributionDTO.getRepId(), laaTransactionId, false);
                    contribution = contributionsList.stream().filter(x -> contributionId.equals(x.getId())).findFirst().get();
                }

                BigDecimal monthlyContributions = (contributionDTO.getMonthlyContributions() != null) ? contributionDTO.getMonthlyContributions() : BigDecimal.valueOf(-1);

                if (TransferStatus.REQUESTED.equals(contribution.getTransferStatus())) {
                    maatCourtDataService.updateContribution(new UpdateContributionRequestMapper().map(contributionDTO), laaTransactionId);
                }
                contribution = createContribs(contributionDTO, contributionDTO.getLaaTransactionId());

                List<Contribution> contributionsList = maatCourtDataService.findContribution(contributionDTO.getRepId(), laaTransactionId, true);
                if (!contributionsList.isEmpty()) {
                    contribution = contributionsList.get(0);
                }

                if (((contributionDTO.getMonthlyContributions().compareTo(contribution.getMonthlyContributions()) != 0
                        || contributionDTO.getUpfrontContributions().compareTo(contribution.getUpfrontContributions()) != 0
                        || (!contributionDTO.getEffectiveDate().equals(contribution.getEffectiveDate())
                        && BigDecimal.ZERO.compareTo(contributionDTO.getMonthlyContributions()) < 0)
                        || contributionService.hasCCOutcomeChanged(contributionDTO.getRepId(), laaTransactionId))
                        && Arrays.asList("SENT FOR TRIAL", "COMMITTED FOR TRIAL", "APPEAL TO CC")
                        .contains(contributionDTO.getMagCourtOutcome().getOutcome()) || contributionService.hasContributionBeenSent(contributionDTO.getRepId(), laaTransactionId))
                ) {
                    maatCourtDataService.updateContribution(new UpdateContributionRequest()
                            .withId(contribution.getId())
                            .withTransferStatus(TransferStatus.REQUESTED)
                            .withUserModified(contributionDTO.getUserModified()), laaTransactionId);
                }

                if (contributionResponseDTO.getTemplate() != null && "Y".equals(contribution.getCalculationRan())) {
                    if ("Y".equals(contributionDTO.getUpliftApplied())) {
                        contributionResponseDTO.setTemplate(contributionResponseDTO.getUpliftCote());
                    } else {
                        if (isReassessment) {
                            contributionResponseDTO.setTemplate(contributionResponseDTO.getReassessmentCoteId());
                        }
                    }
                }

                //Call Matrix Activity

                maatCourtDataService.updateContribution(new UpdateContributionRequestMapper().map(contributionDTO), laaTransactionId);
            }

        }

        List<ContributionsSummaryDTO> contribSummaryList = maatCourtDataService.getContributionsSummary(contributionDTO.getRepId(), laaTransactionId);

        //Get Appln Correspondence
        return response;

    }

    public Contribution createContribs(ContributionDTO contributionDTO, String laaTransactionId) {
        log.info("Inactivate existing Contribution and create a new Contribution");
        CreateContributionRequest createContributionRequest = createContributionRequestMapper.map(contributionDTO);
        return compareContributionService.compareContribution(contributionDTO) < 2 ?
                maatCourtDataService.createContribution(createContributionRequest, laaTransactionId) : null;
    }

    public CalculateContributionResponse calcContribs(ContributionDTO contributionDTO, ContributionResponseDTO contributionResponseDTO, String laaTransactionId) {
        CalculateContributionResponse response = new CalculateContributionResponse();
        LocalDate assEffectiveDate = getEffectiveDate(contributionDTO);
        ContributionCalcParametersDTO contributionCalcParametersDTO = maatCourtDataService.getContributionCalcParameters(assEffectiveDate.toString(), laaTransactionId);
        CrownCourtOutcome crownCourtOutcome = contributionRulesService.getActiveCCOutcome(contributionDTO.getCrownCourtSummary());
        boolean isContributionRuleApplicable = contributionRulesService.isContributionRuleApplicable(contributionDTO.getCaseType(),
                contributionDTO.getMagCourtOutcome(), crownCourtOutcome);

        BigDecimal annualDisposableIncome = calculateAnnualDisposableIncome(contributionDTO, laaTransactionId, crownCourtOutcome, isContributionRuleApplicable);

        if (contributionResponseDTO.getUpliftCote() != null &&
                contributionDTO.getDateUpliftApplied() != null &&
                contributionDTO.getDateUpliftRemoved() == null) {
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
            if (monthlyContributions.compareTo(contributionDTO.getContributionCap()) > 0) {
                response.setMonthlyContributions(contributionDTO.getContributionCap());
                response.setBasedOn("Offence Type");
            } else {
                response.setMonthlyContributions(monthlyContributions);
                response.setBasedOn("Means");
            }
            response.setUpfrontContributions(calculateUpfrontContributions(contributionDTO, contributionCalcParametersDTO));
        }

        response.setContributionCap(contributionDTO.getContributionCap()); // TODO refactor the request to pass the offenceType object for Contribs Cap
        response.setEffectiveDate(getEffectiveDateByNewWorkReason(contributionDTO, response.getMonthlyContributions(), assEffectiveDate));
        return response;
    }

    public BigDecimal calculateAnnualDisposableIncome(ContributionDTO contributionDTO, String laaTransactionId, CrownCourtOutcome crownCourtOutcome, boolean isContributionRuleApplicable) {
        BigDecimal annualDisposableIncome = contributionDTO.getDisposableIncomeAfterCrownHardship();
        if (isContributionRuleApplicable) {
            annualDisposableIncome = getAnnualDisposableIncome(contributionDTO, annualDisposableIncome);
            Optional<ContributionVariationDTO> contributionVariation = contributionRulesService.getContributionVariation(contributionDTO.getCaseType(), contributionDTO.getMagCourtOutcome(),
                    crownCourtOutcome);

            if (contributionVariation.isPresent()) {
                annualDisposableIncome = annualDisposableIncome
                        .add(calculateVariationAmount(contributionDTO.getRepId(), laaTransactionId, contributionVariation.get()));
            }
        } else {
            if (annualDisposableIncome == null) {
                if (contributionDTO.getTotalAnnualDisposableIncome() != null) {
                    annualDisposableIncome = contributionDTO.getTotalAnnualDisposableIncome();
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

    public static BigDecimal getAnnualDisposableIncome(final ContributionDTO contributionDTO, final BigDecimal annualDisposableIncome) {
        if (annualDisposableIncome == null) {
            if ((contributionDTO.getDisposableIncomeAfterMagHardship() != null)) {
                return contributionDTO.getDisposableIncomeAfterMagHardship();
            } else {
                if (contributionDTO.getTotalAnnualDisposableIncome() != null) {
                    return contributionDTO.getTotalAnnualDisposableIncome();
                } else return BigDecimal.ZERO;
            }
        }
        return annualDisposableIncome;
    }

    public static String getEffectiveDateByNewWorkReason(final ContributionDTO contributionDTO, final BigDecimal monthlyContributions, final LocalDate assEffectiveDate) {
        NewWorkReason newWorkReason = getNewWorkReason(contributionDTO);
        if (NewWorkReason.FMA == newWorkReason) {
            return assEffectiveDate.toString();
        } else if (NewWorkReason.PAI == newWorkReason) {
            if (contributionDTO.getMonthlyContributions().compareTo(monthlyContributions) <= 0) {
                return contributionDTO.getEffectiveDate().toString();
            } else return assEffectiveDate.toString();
        } else {
            if (contributionDTO.getEffectiveDate() == null) {
                return assEffectiveDate.toString();
            } else return contributionDTO.getEffectiveDate().toString();
        }
    }

    public static NewWorkReason getNewWorkReason(final ContributionDTO contributionDTO) {
        return contributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.PASSPORT).findFirst()
                .map(Assessment::getNewWorkReason)
                .orElse(contributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst()
                        .map(Assessment::getNewWorkReason).orElse(null));
    }

    /**
     * This method calculates the upfront contributions based on the below logic:
     * //        p_application_object.crown_court_overview_object.contributions_object.upfront_contribs
     * //                      := least(p_application_object.crown_court_overview_object.contributions_object.monthly_contribs * v_UPFRONT_TOTAL_MONTHS
     * //                ,p_application_object.offence_type_object.contribs_cap);
     */
    public static BigDecimal calculateUpfrontContributions(final ContributionDTO contributionDTO, final ContributionCalcParametersDTO contributionCalcParametersDTO) {
        BigDecimal upfrontContribution = contributionDTO.getMonthlyContributions().multiply(BigDecimal.valueOf(contributionCalcParametersDTO.getUpfrontTotalMonths()));
        if (upfrontContribution.compareTo(contributionDTO.getContributionCap()) < 0) {
            return upfrontContribution;
        } else return contributionDTO.getContributionCap();
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
    public static LocalDate getEffectiveDate(final ContributionDTO contributionDTO) {
        LocalDate committalDate = contributionDTO.getCommittalDate();
        Optional<Assessment> passAssessment = contributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.PASSPORT).findFirst();
        LocalDateTime assessmentDate = passAssessment.map(Assessment::getAssessmentDate).orElse(null);
        if (assessmentDate == null) {
            Optional<Assessment> fullAssessment = contributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.FULL).findFirst();
            assessmentDate = fullAssessment.map(Assessment::getAssessmentDate).orElse(null);
            if (assessmentDate == null) {
                Optional<Assessment> initAssessment = contributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst();
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