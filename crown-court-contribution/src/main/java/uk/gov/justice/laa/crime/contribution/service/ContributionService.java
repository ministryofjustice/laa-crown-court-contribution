package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.crime.contribution.builder.AssessmentRequestDTOBuilder;
import uk.gov.justice.laa.crime.contribution.builder.ContributionResponseDTOBuilder;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.*;
import uk.gov.justice.laa.crime.common.model.contribution.ApiContributionTransferRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.UpdateContributionRequest;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;
import uk.gov.justice.laa.crime.contribution.repository.CorrespondenceRuleRepository;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.*;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.enums.PassportAssessmentResult;
import uk.gov.justice.laa.crime.enums.contribution.TransferStatus;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Optional.ofNullable;
import static uk.gov.justice.laa.crime.contribution.common.Constants.PASS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

    private static final String INEL = "INEL";
    private final CorrespondenceRuleRepository correspondenceRuleRepository;
    private final MaatCourtDataService maatCourtDataService;

    protected static String getPassportAssessmentResult(final RepOrderDTO repOrderDTO) {
        List<PassportAssessmentDTO> passportAssessments = new ArrayList<>(repOrderDTO.getPassportAssessments()
                .stream().filter(passportAssessmentDTO -> Constants.Y.equals(passportAssessmentDTO.getReplaced())).toList());
        passportAssessments.sort(Comparator.comparing(PassportAssessmentDTO::getId, Comparator.reverseOrder()));
        return passportAssessments.isEmpty() ? null : passportAssessments.get(0).getResult();
    }

    protected static String getInitialAssessmentResult(final RepOrderDTO repOrderDTO) {
        List<FinancialAssessmentDTO> financialAssessments = new ArrayList<>(repOrderDTO.getFinancialAssessments()
                .stream().filter(financialAssessmentDTO -> Constants.N.equals(financialAssessmentDTO.getReplaced())).toList());
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
            } else if (Set.of(Constants.FAIL, Constants.FULL, Constants.HARDSHIP_APPLICATION)
                    .contains(request.getInitResult()) &&
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
        log.info("Checking contribution conditions ");
        log.info("Checking contribution conditions - Monthly Contribs: {}", request.getMonthlyContribs());
        log.info("Checking contribution request " + request);

        ContributionResponseDTO contributionResponseDTO;
        AssessmentRequestDTO assessmentRequestDTO = AssessmentRequestDTOBuilder.build(request);

        ContributionResponseDTO contributionResponse = new ContributionResponseDTO();
        contributionResponse.setDoContribs(Constants.N);
        contributionResponse.setCalcContribs(Constants.N);

        log.info("Contribution response before: {}", contributionResponse);

        AssessmentResponseDTO assessmentResponseDTO = getAssessmentResult(assessmentRequestDTO);
        request.setIojResult(assessmentResponseDTO.getIojResult());
        request.setMeansResult(assessmentResponseDTO.getMeansResult());
        log.info("Case type: {}", request.getCaseType());
        if (Set.of(CaseType.INDICTABLE, CaseType.EITHER_WAY, CaseType.CC_ALREADY).contains(request.getCaseType())
                || request.getEffectiveDate() != null
                || (CaseType.APPEAL_CC.equals(request.getCaseType()) && PASS.equals(request.getIojResult()))) {
            contributionResponse.setDoContribs(Constants.Y);
        }
        log.info("DoContribs: {}", contributionResponse.getDoContribs());

        if (contributionResponse.getDoContribs().equals(Constants.Y)) {
            log.info("MeansResult --" + request.getMeansResult());
            log.info("IojResult --" + request.getIojResult());
            log.info("MagCourtOutcome --" + request.getMagCourtOutcome());
            log.info("CrownCourtOutcome --" + request.getCrownCourtOutcome());
            log.info("InitResult --" + request.getInitResult());
            CorrespondenceRuleAndTemplateInfo processedCases = getCoteInfo(request);
            log.info("processedCases --" + processedCases);
            if (processedCases == null) {
                contributionResponse.setDoContribs(Constants.N);
                contributionResponse.setCalcContribs(Constants.N);
                log.info("DoContribs --" + contributionResponse.getDoContribs());
                log.info("CalcContribs --" + contributionResponse.getCalcContribs());
            } else {
                log.info("processedCases has value");
                contributionResponseDTO = ContributionResponseDTOBuilder.build(processedCases);
                log.info("contributionResponseDTO--->"+ contributionResponseDTO);
                contributionResponse.setId(contributionResponseDTO.getId());
                contributionResponse.setCalcContribs(contributionResponseDTO.getCalcContribs());
                contributionResponse.setTemplateDesc(contributionResponseDTO.getTemplateDesc());
                contributionResponse.setCorrespondenceType(contributionResponseDTO.getCorrespondenceType());
                contributionResponse.setUpliftCote(contributionResponseDTO.getUpliftCote());
                contributionResponse.setReassessmentCoteId(contributionResponseDTO.getReassessmentCoteId());
                CorrespondenceType correspondenceType = CorrespondenceType.getFrom(processedCases.getCotyCorrespondenceType());
                if (correspondenceType != null) {
                    contributionResponse.setCorrespondenceTypeDesc(correspondenceType.getDescription());
                }
                log.info("contributionResponse--->"+ contributionResponse);
            }
        }
        log.info("FullResult --" + request.getFullResult());
        if (INEL.equals(request.getFullResult())) {
            contributionResponse.setDoContribs(Constants.Y);
        }
        log.info("DoContribs after INEL check--" + contributionResponse.getDoContribs());

        if (request.getMonthlyContribs() != null && request.getMonthlyContribs().compareTo(BigDecimal.ZERO) > 0) {
            contributionResponse.setDoContribs(Constants.Y);
        }
        log.info("DoContribs after MonthlyContribs check--" + contributionResponse.getDoContribs());
        log.info("Remove Contrib--" + request.getRemoveContribs());
        if (Constants.Y.equals(request.getRemoveContribs())) {
            contributionResponse.setCalcContribs(Constants.N);
        }

        log.info("Final Value for DoContribs is: {}", contributionResponse.getDoContribs());
        log.info("Final Value for CalcContribs is: {}", contributionResponse.getCalcContribs());

        return contributionResponse;
    }

    @Transactional
    public CorrespondenceRuleAndTemplateInfo getCoteInfo(ContributionRequestDTO contributionRequestDTO) {
        log.info("CorrespondenceRuleAndTemplateInfo: {}", contributionRequestDTO);
        return correspondenceRuleRepository.getCoteInfo(
                contributionRequestDTO.getMeansResult(),
                contributionRequestDTO.getIojResult(),
                contributionRequestDTO.getMagCourtOutcome(),
                contributionRequestDTO.getCrownCourtOutcome(),
                contributionRequestDTO.getInitResult()
        ).orElse(null);
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

    public boolean hasCCOutcomeChanged(final int repId) {
        List<RepOrderCCOutcomeDTO> repOrderCCOutcomeList = maatCourtDataService.getRepOrderCCOutcomeByRepId(repId);
        if (CollectionUtils.isNotEmpty(repOrderCCOutcomeList)) {
            Optional<RepOrderCCOutcomeDTO> outcomeDTO =
                    repOrderCCOutcomeList.stream().min(Comparator.comparing(RepOrderCCOutcomeDTO::getId));
            return outcomeDTO.isPresent() && outcomeDTO.get().getOutcome() != null
                    && !CrownCourtOutcome.AQUITTED.getCode().equals(outcomeDTO.get().getOutcome());
        }
        return false;
    }

    public boolean hasApplicationStatusChanged(RepOrderDTO repOrderDTO, CaseType caseType, String status) {
        log.info("Get applicant details from Crime Apply datastore");
        return CaseType.INDICTABLE.equals(caseType) && repOrderDTO != null
                && repOrderDTO.getRorsStatus() != null
                && !repOrderDTO.getRorsStatus().equalsIgnoreCase(status);
    }

    public void requestTransfer(final ApiContributionTransferRequest transferRequest) {
        UpdateContributionRequest updateRequest = new UpdateContributionRequest()
                .withId(transferRequest.getContributionId())
                .withTransferStatus(TransferStatus.REQUESTED)
                .withUserModified(transferRequest.getUserModified());
        maatCourtDataService.updateContribution(updateRequest);
    }
}
