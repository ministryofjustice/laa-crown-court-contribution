package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.builder.CalculateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.CreateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.builder.MaatCalculateContributionResponseMapper;
import uk.gov.justice.laa.crime.contribution.builder.UpdateContributionRequestMapper;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.*;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.common.Assessment;
import uk.gov.justice.laa.crime.contribution.model.maat_api.*;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.*;
import uk.gov.justice.laa.crime.contribution.util.DateUtil;

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

    private final CreateContributionRequestMapper createContributionRequestMapper;
    private final ContributionService contributionService;
    private final UpdateContributionRequestMapper updateContributionRequestMapper;
    private final CalculateContributionRequestMapper calculateContributionRequestMapper;
    private final MaatCalculateContributionResponseMapper maatCalculateContributionResponseMapper;

    private final List<MagCourtOutcome> earlyTransferMagOutcomes = List.of(MagCourtOutcome.SENT_FOR_TRIAL,
            MagCourtOutcome.COMMITTED_FOR_TRIAL,
            MagCourtOutcome.APPEAL_TO_CC);

    public MaatCalculateContributionResponse calculateContribution(CalculateContributionDTO calculateContributionDTO, String laaTransactionId) {
        MaatCalculateContributionResponse response;

        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(calculateContributionDTO.getRepId(), laaTransactionId);
        calculateContributionDTO.setRepOrderDTO(repOrderDTO);

        if (CaseType.APPEAL_CC.equals(calculateContributionDTO.getCaseType())) {
            response = appealContributionService.calculateAppealContribution(calculateContributionDTO, laaTransactionId);
        } else {
            response = getCalculateContributionResponse(calculateContributionDTO, laaTransactionId, repOrderDTO);
        }

        return response;
    }

    public MaatCalculateContributionResponse getCalculateContributionResponse(final CalculateContributionDTO calculateContributionDTO,
                                                                              final String laaTransactionId,
                                                                              final RepOrderDTO repOrderDTO) {
        MaatCalculateContributionResponse response = new MaatCalculateContributionResponse();
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
            response = doContribs(calculateContributionDTO, laaTransactionId, contributionResponseDTO, fullResult, isReassessment, repOrderDTO);
        }
        return response;
    }

    public MaatCalculateContributionResponse doContribs(final CalculateContributionDTO calculateContributionDTO,
                                                        final String laaTransactionId,
                                                        final ContributionResponseDTO contributionResponseDTO,
                                                        final String fullResult,
                                                        final boolean isReassessment,
                                                        final RepOrderDTO repOrderDTO) {
        MaatCalculateContributionResponse response = new MaatCalculateContributionResponse();

        //Use Calculated Monthly Contributions value - p_application_object.crown_court_overview_object.contributions_object.monthly_contribs > 0 ->
        if (Constants.Y.equals(contributionResponseDTO.getCalcContribution()) ||
                contributionResponseDTO.getTemplate() != null ||
                (calculateContributionDTO.getMonthlyContributions() != null && calculateContributionDTO.getMonthlyContributions().compareTo(BigDecimal.ZERO) > 0) ||
                Constants.INEL.equals(fullResult)) {
            response = calcContribs(calculateContributionDTO, contributionResponseDTO, laaTransactionId);
        } else if (calculateContributionDTO.getMonthlyContributions() != null) {
            response.setMonthlyContributions(BigDecimal.ZERO);
            response.setContributionCap(BigDecimal.ZERO);
            response.setUpfrontContributions(BigDecimal.ZERO);
        }

        Contribution currentContribution = getCurrentContribution(calculateContributionDTO, laaTransactionId);

        Contribution createdContribution = verifyAndCreateContribs(calculateContributionDTO, laaTransactionId, isReassessment, repOrderDTO,
                response, currentContribution);

        requestEarlyTransfer(calculateContributionDTO, laaTransactionId, response, currentContribution);

        if (contributionResponseDTO.getTemplate() != null && createdContribution != null) {
            response.setProcessActivity(true);
        }
        return response;
    }

    public Contribution verifyAndCreateContribs(final CalculateContributionDTO calculateContributionDTO,
                                         final String laaTransactionId,
                                         final boolean isReassessment,
                                         final RepOrderDTO repOrderDTO,
                                         final MaatCalculateContributionResponse response,
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
                || (response.getEffectiveDate() != null && !response.getEffectiveDate().equals(calculateContributionDTO.getEffectiveDate().toString()))
        ) {
            if (TransferStatus.REQUESTED.equals(currentTransferStatus)) {
                TransferStatus transferStatus = (currentContributionFileId == null) ? null : TransferStatus.SENT;
                UpdateContributionRequest updateContributionRequest = updateContributionRequestMapper.map(currentContribution);
                updateContributionRequest.setTransferStatus(transferStatus);
                maatCourtDataService.updateContribution(updateContributionRequest, laaTransactionId);
            }
            //Revisit the createContribs logic - do we need to change the input?
            return createContribs(calculateContributionDTO, laaTransactionId);
        } else if (isCreateContributionRequired(calculateContributionDTO, isReassessment, repOrderDTO, currentTransferStatus)) {
            return createContribs(calculateContributionDTO, laaTransactionId);
        }
        return null;
    }

    public void requestEarlyTransfer(final CalculateContributionDTO calculateContributionDTO,
                                     final String laaTransactionId,
                                     final MaatCalculateContributionResponse response,
                                     final Contribution currentContribution) {
        Contribution latestSentContribution = maatCourtDataService.findLatestSentContribution(calculateContributionDTO.getRepId(), laaTransactionId);
        if (isEarlyTransferRequired(calculateContributionDTO, laaTransactionId, response, latestSentContribution) && currentContribution != null) {
            maatCourtDataService.updateContribution(new UpdateContributionRequest()
                    .withId(currentContribution.getId())
                    .withTransferStatus(TransferStatus.REQUESTED)
                    .withUserModified(calculateContributionDTO.getUserModified()), laaTransactionId);
        }
    }

    public Contribution getCurrentContribution(final CalculateContributionDTO calculateContributionDTO, final String laaTransactionId) {
        final Integer contributionId = calculateContributionDTO.getId();
        List<Contribution> contributionsList = new ArrayList<>();
        if (contributionId != null) {
            contributionsList = maatCourtDataService.findContribution(calculateContributionDTO.getRepId(), laaTransactionId, false);
        }
        return contributionsList.stream().filter(x -> contributionId.equals(x.getId())).findFirst().orElse(null);
    }

    public boolean isCreateContributionRequired(final CalculateContributionDTO calculateContributionDTO,
                                                final boolean isReassessment,
                                                final RepOrderDTO repOrderDTO,
                                                final TransferStatus currentTransferStatus) {
        return ((!TransferStatus.REQUESTED.equals(currentTransferStatus)
                && (contributionService.hasApplicationStatusChanged(repOrderDTO, calculateContributionDTO.getCaseType(), calculateContributionDTO.getApplicationStatus())
                || contributionService.hasCCOutcomeChanged(repOrderDTO.getId(), calculateContributionDTO.getLaaTransactionId())
                || contributionService.isCds15WorkAround(repOrderDTO))) || isReassessment) || (TransferStatus.REQUESTED.equals(currentTransferStatus)
                && CaseType.APPEAL_CC.equals(calculateContributionDTO.getCaseType()));
    }

    public boolean isEarlyTransferRequired(final CalculateContributionDTO calculateContributionDTO,
                                           final String laaTransactionId,
                                           final MaatCalculateContributionResponse response,
                                           final Contribution latestSentContribution) {
        return ((response.getMonthlyContributions().compareTo(latestSentContribution.getMonthlyContributions()) != 0
                || response.getUpfrontContributions().compareTo(latestSentContribution.getUpfrontContributions()) != 0
                || (latestSentContribution.getEffectiveDate() != null && !response.getEffectiveDate().equals(latestSentContribution.getEffectiveDate().toString())
                && BigDecimal.ZERO.compareTo(response.getMonthlyContributions()) < 0)
                || contributionService.hasCCOutcomeChanged(calculateContributionDTO.getRepId(), laaTransactionId))
                && (calculateContributionDTO.getMagCourtOutcome() != null && earlyTransferMagOutcomes.contains(calculateContributionDTO.getMagCourtOutcome())))
                || contributionService.hasContributionBeenSent(calculateContributionDTO.getRepId(), laaTransactionId);
    }

    public Contribution createContribs(final CalculateContributionDTO calculateContributionDTO, final String laaTransactionId) {
        log.info("Inactivate existing Contribution and create a new Contribution");
        CreateContributionRequest createContributionRequest = createContributionRequestMapper.map(calculateContributionDTO);
        if (compareContributionService.compareContribution(calculateContributionDTO) < 2) {
            return maatCourtDataService.createContribution(createContributionRequest, laaTransactionId);
        } else return null;
    }

    public MaatCalculateContributionResponse calcContribs(final CalculateContributionDTO calculateContributionDTO,
                                                          final ContributionResponseDTO contributionResponseDTO,
                                                          final String laaTransactionId) {
        LocalDate assEffectiveDate = getEffectiveDate(calculateContributionDTO);
        ContributionCalcParametersDTO contributionCalcParametersDTO = maatCourtDataService.getContributionCalcParameters(assEffectiveDate.toString(), laaTransactionId);
        CrownCourtOutcome crownCourtOutcome = contributionRulesService.getActiveCCOutcome(calculateContributionDTO.getCrownCourtSummary());
        boolean isContributionRuleApplicable = contributionRulesService.isContributionRuleApplicable(calculateContributionDTO.getCaseType(),
                calculateContributionDTO.getMagCourtOutcome(), crownCourtOutcome);

        BigDecimal annualDisposableIncome = calculateAnnualDisposableIncome(calculateContributionDTO, laaTransactionId, crownCourtOutcome, isContributionRuleApplicable);
        Integer totalMonths = Constants.N.equals(contributionResponseDTO.getCalcContribs()) ? 0 : null;

        ApiCalculateContributionRequest apiCalculateContributionRequest = calculateContributionRequestMapper.map(contributionCalcParametersDTO,
                annualDisposableIncome, isUpliftApplied(calculateContributionDTO, contributionResponseDTO),
                calculateContributionDTO.getContributionCap());

        // Revisit the request to pass the offenceType object for Contribs Cap
        return maatCalculateContributionResponseMapper.map(calculateContributionService.calculateContribution(apiCalculateContributionRequest),
                calculateContributionDTO.getContributionCap(),
                getEffectiveDateByNewWorkReason(calculateContributionDTO,
                        calculateContributionDTO.getContributionCap(), assEffectiveDate), totalMonths);
    }

    private static boolean isUpliftApplied(CalculateContributionDTO calculateContributionDTO, ContributionResponseDTO contributionResponseDTO) {
        return contributionResponseDTO.getUpliftCote() != null &&
                calculateContributionDTO.getDateUpliftApplied() != null &&
                calculateContributionDTO.getDateUpliftRemoved() == null;
    }

    public BigDecimal calculateAnnualDisposableIncome(final CalculateContributionDTO calculateContributionDTO,
                                                      final String laaTransactionId,
                                                      final CrownCourtOutcome crownCourtOutcome,
                                                      boolean isContributionRuleApplicable) {
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
                .map(Assessment::getNewWorkReason)
                .orElse(calculateContributionDTO.getAssessments().stream().filter(it -> it.getAssessmentType() == AssessmentType.INIT).findFirst()
                        .map(Assessment::getNewWorkReason).orElse(null));
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