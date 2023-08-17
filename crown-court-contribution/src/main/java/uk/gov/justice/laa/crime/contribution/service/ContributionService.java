package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.crime.contribution.builder.AssessmentRequestDTOBuilder;
import uk.gov.justice.laa.crime.contribution.builder.ContributionResponseDTOBuilder;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.*;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;
import uk.gov.justice.laa.crime.contribution.repository.CorrespondenceRuleRepository;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Optional.ofNullable;
import static uk.gov.justice.laa.crime.contribution.common.Constants.PASS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

    private static final String INEL = "INEL";
    private static final String CONTRIBUTION_YES = "Y";
    private final CorrespondenceRuleRepository correspondenceRuleRepository;
    private final MaatCourtDataService maatCourtDataService;


    protected static String getPassportAssessmentResult(final RepOrderDTO repOrderDTO) {
        List<PassportAssessmentDTO> passportAssessments = new ArrayList<>(repOrderDTO.getPassportAssessments()
                .stream().filter(passportAssessmentDTO -> "Y".equals(passportAssessmentDTO.getReplaced())).toList());
        passportAssessments.sort(Comparator.comparing(PassportAssessmentDTO::getId, Comparator.reverseOrder()));
        return passportAssessments.isEmpty() ? null : passportAssessments.get(0).getResult();
    }

    protected static String getInitialAssessmentResult(final RepOrderDTO repOrderDTO) {
        List<FinancialAssessmentDTO> financialAssessments = new ArrayList<>(repOrderDTO.getFinancialAssessments()
                .stream().filter(financialAssessmentDTO -> "N".equals(financialAssessmentDTO.getReplaced())).toList());
        financialAssessments.sort(Comparator.comparing(FinancialAssessmentDTO::getId, Comparator.reverseOrder()));
        return financialAssessments.isEmpty() ? null : financialAssessments.get(0).getInitResult();
    }

    public AssessmentResponseDTO getAssessmentResult(final AssessmentRequestDTO request) {
        AssessmentResponseDTO response = new AssessmentResponseDTO();
        response.setIojResult(ofNullable(request.getDecisionResult())
                .orElse(request.getIojResult()));

        if (StringUtils.isNotBlank(request.getPassportResult())) {

            if (Set.of(PASS, Constants.TEMP).contains(request.getPassportResult())) {
                response.setMeansResult(Constants.PASSPORT);
            } else if (Constants.FAIL.equals(request.getPassportResult())) {
                response.setMeansResult(Constants.FAILPORT);
            } else if (PASS.equals(request.getInitResult()) ||
                    PASS.equals(request.getFullResult()) ||
                    PASS.equals(request.getHardshipResult())) {
                response.setMeansResult(PASS);
            } else if (Set.of(Constants.FAIL, Constants.FULL, Constants.HARDSHIP_APPLICATION).contains(request.getInitResult()) &&
                    (Constants.FAIL.equals(request.getFullResult())) &&
                    (Constants.FAIL.equals(ofNullable(request.getHardshipResult()).orElse(Constants.FAIL)))) {
                response.setMeansResult(Constants.FAIL);
            }
        } else {
            if (StringUtils.isBlank(request.getFullResult())) {
                response.setMeansResult(StringUtils.isBlank(request.getInitResult()) ? Constants.NONE
                        : Constants.INIT.concat(request.getInitResult()));
            } else {
                response.setMeansResult(request.getFullResult());
            }
        }
        return response;
    }

    @Transactional
    public ContributionResponseDTO checkContribsCondition(ContributionRequestDTO request) {
        ContributionResponseDTO contributionResponseDTO = null;
        AssessmentRequestDTO assessmentRequestDTO = AssessmentRequestDTOBuilder.build(request);

        ContributionResponseDTO contributionResponse = new ContributionResponseDTO();
        contributionResponse.setDoContribs("N");
        contributionResponse.setCalcContribs("N");

        AssessmentResponseDTO assessmentResponseDTO = getAssessmentResult(assessmentRequestDTO);
        request.setIojResult(assessmentResponseDTO.getIojResult());
        request.setMeansResult(assessmentResponseDTO.getMeansResult());

        if (Set.of(CaseType.INDICTABLE, CaseType.EITHER_WAY, CaseType.CC_ALREADY).contains(request.getCaseType())
                || request.getEffectiveDate() != null
                || (CaseType.APPEAL_CC.equals(request.getCaseType()) && PASS.equals(request.getIojResult()))) {
            contributionResponse.setDoContribs("Y");
        }

        if (contributionResponse.getDoContribs() == "Y") {
            CorrespondenceRuleAndTemplateInfo processedCases = getCoteInfo(request);
            if (processedCases == null) {
                contributionResponse.setDoContribs("N");
                contributionResponse.setCalcContribs("N");
            } else {
                contributionResponseDTO = ContributionResponseDTOBuilder.build(processedCases);
                contributionResponse.setId(contributionResponseDTO.getId());
                contributionResponse.setCalcContribs(contributionResponseDTO.getCalcContribs());
                contributionResponse.setTemplateDesc(contributionResponseDTO.getTemplateDesc());
                contributionResponse.setCorrespondenceType(contributionResponseDTO.getCorrespondenceType());
                contributionResponse.setUpliftCote(contributionResponseDTO.getUpliftCote());
                contributionResponse.setReassessmentCoteId(contributionResponseDTO.getReassessmentCoteId());
                contributionResponse.setDoContribs(contributionResponseDTO.getDoContribs());
                if (CorrespondenceType.getFrom(processedCases.getCotyCorrespondenceType()) != null) {
                    contributionResponse.setCorrespondenceTypeDesc(CorrespondenceType.getFrom(processedCases.getCotyCorrespondenceType()).getDescription());
                }
            }
        }

        if (request.getMonthlyContribs().compareTo(BigDecimal.ZERO) > 0 || INEL.equals(request.getFullResult())) {
            contributionResponse.setDoContribs("Y");
        }

        if (CONTRIBUTION_YES.equals(request.getRemoveContribs())) {
            contributionResponse.setCalcContribs("N");
        }

        return contributionResponse;
    }

    @Transactional
    public CorrespondenceRuleAndTemplateInfo getCoteInfo(ContributionRequestDTO contributionRequestDTO) {
        return correspondenceRuleRepository.getCoteInfo(
                contributionRequestDTO.getMeansResult(),
                contributionRequestDTO.getIojResult(),
                contributionRequestDTO.getMagCourtOutcome(),
                contributionRequestDTO.getCrownCourtOutcome(),
                contributionRequestDTO.getInitResult());


    }

    public boolean checkReassessment(RepOrderDTO repOrderDTO, final String laaTransactionId) {
        log.info("Check if reassessment is required for REP_ID={}", repOrderDTO.getId());

        long contributionCount = maatCourtDataService.getContributionCount(repOrderDTO.getId(), laaTransactionId);
        List<FinancialAssessmentDTO> financialAssessments = repOrderDTO.getFinancialAssessments();
        List<PassportAssessmentDTO> passportAssessments = repOrderDTO.getPassportAssessments();

        if (contributionCount > 0) {
            Optional<LocalDateTime> latestFinAssessmentDate = ofNullable(financialAssessments)
                    .orElseGet(Collections::emptyList).stream()
                    .map(FinancialAssessmentDTO::getDateCreated)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo);

            Optional<LocalDateTime> latestPassportAssessmentDate = ofNullable(passportAssessments)
                    .orElseGet(Collections::emptyList).stream()
                    .map(PassportAssessmentDTO::getDateCreated)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo);

            if (latestFinAssessmentDate.isPresent() && latestPassportAssessmentDate.isPresent()) {
                if (latestFinAssessmentDate.get().isAfter(latestPassportAssessmentDate.get())) {
                    return financialAssessments.stream().anyMatch(fa -> fa.getReplaced().equals("Y"));
                } else {
                    return passportAssessments.stream().anyMatch(pa -> pa.getReplaced().equals("Y"));
                }
            }
        }
        return false;
    }


    public boolean isCds15WorkAround(final RepOrderDTO repOrderDTO) {

        String passportAssessmentResult = getPassportAssessmentResult(repOrderDTO);

        String initialAssessmentResult = getInitialAssessmentResult(repOrderDTO);

        return PassportAssessmentResult.FAIL.getResult().equals(passportAssessmentResult)
                && InitAssessmentResult.PASS.getResult().equals(initialAssessmentResult);
    }


    public boolean hasMessageOutcomeChanged(String msgOutcome, RepOrderDTO repOrderDTO) {
        if (null != repOrderDTO) {
            String messageOutcome = Optional.ofNullable(repOrderDTO.getMagsOutcome()).orElse("na");
            return !messageOutcome.equals(msgOutcome);
        }
        return false;
    }

    public boolean hasCCOutcomeChanged(final int repId, final String laaTransactionId) {
        boolean isOutcomeChanged = false;
        List<RepOrderCCOutcomeDTO> repOrderCCOutcomeList = maatCourtDataService.getRepOrderCCOutcomeByRepId(repId, laaTransactionId);
        if (null != repOrderCCOutcomeList && !repOrderCCOutcomeList.isEmpty()) {
            Optional<RepOrderCCOutcomeDTO> outcomeDTO = repOrderCCOutcomeList.stream().min(Comparator.comparing(RepOrderCCOutcomeDTO::getId));
            if (outcomeDTO.isPresent() && !(null == outcomeDTO.get().getOutcome()
                    || CrownCourtOutcome.AQUITTED.getCode().equals(outcomeDTO.get().getOutcome()))) {
                isOutcomeChanged = true;
            }
        }
        return isOutcomeChanged;
    }


    public boolean hasApplicationStatusChanged(RepOrderDTO repOrderDTO, CaseType caseType, String status) {
        log.info("Get applicant details from Crime Apply datastore");
        return CaseType.INDICTABLE.equals(caseType) && repOrderDTO != null
                && repOrderDTO.getRorsStatus() != null
                && !repOrderDTO.getRorsStatus().equalsIgnoreCase(status);
    }

    public boolean hasContributionBeenSent(final int repId, final String laaTransactionId) {
        List<Contribution> contribList = maatCourtDataService.findContribution(repId, laaTransactionId, Boolean.FALSE);
        List<Contribution> contributionList = Optional.ofNullable(contribList).orElse(Collections.emptyList()).stream().filter(
                contribution -> ("SENT".equals(contribution.getTransferStatus()) &&
                        contribution.getMonthlyContributions().compareTo(BigDecimal.ZERO) > 0)
        ).toList();

        return !contributionList.isEmpty();
    }


}