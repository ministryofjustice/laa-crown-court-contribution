package uk.gov.justice.laa.crime.contribution.service;

import static java.util.Optional.ofNullable;
import static uk.gov.justice.laa.crime.contribution.common.Constants.FAIL;
import static uk.gov.justice.laa.crime.contribution.common.Constants.PASS;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.contribution.builder.AssessmentRequestDTOBuilder;
import uk.gov.justice.laa.crime.contribution.builder.ContributionResponseDTOMapper;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.dto.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.contribution.dto.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;
import uk.gov.justice.laa.crime.contribution.repository.CorrespondenceRuleRepository;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.MeansAssessmentResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.enums.PassportAssessmentResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

    private static final String INEL = "INEL";
    private final CorrespondenceRuleRepository correspondenceRuleRepository;
    private final MaatCourtDataService maatCourtDataService;
    private final ContributionResponseDTOMapper contributionResponseDTOMapper;

    protected static String getPassportAssessmentResult(final RepOrderDTO repOrderDTO) {
        List<PassportAssessmentDTO> passportAssessments = new ArrayList<>(repOrderDTO.getPassportAssessments().stream()
                .filter(passportAssessmentDTO -> Constants.Y.equals(passportAssessmentDTO.getReplaced()))
                .toList());
        passportAssessments.sort(Comparator.comparing(PassportAssessmentDTO::getId, Comparator.reverseOrder()));
        return passportAssessments.isEmpty() ? null : passportAssessments.get(0).getResult();
    }

    protected static String getInitialAssessmentResult(final RepOrderDTO repOrderDTO) {
        List<FinancialAssessmentDTO> financialAssessments =
                new ArrayList<>(repOrderDTO.getFinancialAssessments().stream()
                        .filter(financialAssessmentDTO -> Constants.N.equals(financialAssessmentDTO.getReplaced()))
                        .toList());
        financialAssessments.sort(Comparator.comparing(FinancialAssessmentDTO::getId, Comparator.reverseOrder()));
        return financialAssessments.isEmpty()
                ? null
                : financialAssessments.get(0).getInitResult();
    }

    public MeansAssessmentResult getMeansAssessmentResult(final AssessmentRequestDTO request) {
        if (StringUtils.isNotBlank(request.getPassportResult())) {
            if (Set.of(PASS, Constants.TEMP).contains(request.getPassportResult())) {
                return MeansAssessmentResult.PASSPORT;
            } else if (FAIL.equals(request.getPassportResult())) {
                return MeansAssessmentResult.FAILPORT;
            } else if (PASS.equals(request.getInitResult())
                    || PASS.equals(request.getFullResult())
                    || PASS.equals(request.getHardshipResult())) {
                return MeansAssessmentResult.PASS;
            } else if (Set.of(FAIL, Constants.FULL, Constants.HARDSHIP_APPLICATION)
                            .contains(request.getInitResult())
                    && (FAIL.equals(request.getFullResult()))
                    && (FAIL.equals(ofNullable(request.getHardshipResult()).orElse(FAIL)))) {
                return MeansAssessmentResult.FAIL;
            }
            return null;
        }

        if (StringUtils.isBlank(request.getFullResult())) {
            if (StringUtils.isBlank(request.getInitResult())) {
                return MeansAssessmentResult.NONE;
            }
            return switch (request.getInitResult()) {
                case Constants.PASS -> MeansAssessmentResult.INIT_PASS;
                case Constants.FAIL -> MeansAssessmentResult.INIT_FAIL;
                default -> null;
            };
        }

        return switch (request.getFullResult()) {
            case Constants.PASS -> MeansAssessmentResult.PASS;
            case Constants.FAIL -> MeansAssessmentResult.FAIL;
            case Constants.INEL -> MeansAssessmentResult.INEL;
            default -> null;
        };
    }

    @Transactional
    public ContributionResponseDTO checkContributionsCondition(ContributionRequestDTO request) {

        AssessmentRequestDTO assessmentRequestDTO = AssessmentRequestDTOBuilder.build(request);

        ContributionResponseDTO contributionResponse = ContributionResponseDTO.builder()
                .doContribs(Constants.N)
                .calcContribs(Constants.N)
                .build();

        MeansAssessmentResult result = getMeansAssessmentResult(assessmentRequestDTO);
        request.setIojResult(ofNullable(request.getDecisionResult()).orElse(request.getIojResult()));
        request.setMeansResult(result.getResult());

        if (Set.of(CaseType.INDICTABLE, CaseType.EITHER_WAY, CaseType.CC_ALREADY)
                        .contains(request.getCaseType())
                || request.getEffectiveDate() != null
                || (CaseType.APPEAL_CC.equals(request.getCaseType()) && PASS.equals(request.getIojResult()))) {

            contributionResponse.setDoContribs(Constants.Y);
            CorrespondenceRuleAndTemplateInfo processedCases = getCoteInfo(request);

            if (processedCases == null) {
                contributionResponse.setDoContribs(Constants.N);
                contributionResponse.setCalcContribs(Constants.N);
            } else {
                contributionResponseDTOMapper.map(processedCases, contributionResponse);
            }
        }

        if (INEL.equals(request.getFullResult())
                || (request.getMonthlyContribs() != null
                        && request.getMonthlyContribs().compareTo(BigDecimal.ZERO) > 0)) {
            contributionResponse.setDoContribs(Constants.Y);
        }

        if (Constants.Y.equals(request.getRemoveContribs())) {
            contributionResponse.setCalcContribs(Constants.N);
        }

        return contributionResponse;
    }

    @Transactional
    public CorrespondenceRuleAndTemplateInfo getCoteInfo(ContributionRequestDTO contributionRequestDTO) {
        return correspondenceRuleRepository
                .getCoteInfo(
                        contributionRequestDTO.getMeansResult(),
                        contributionRequestDTO.getIojResult(),
                        contributionRequestDTO.getMagCourtOutcome(),
                        contributionRequestDTO.getCrownCourtOutcome(),
                        contributionRequestDTO.getInitResult())
                .orElse(null);
    }

    public boolean isCds15WorkAround(final RepOrderDTO repOrderDTO) {

        String passportAssessmentResult = getPassportAssessmentResult(repOrderDTO);

        String initialAssessmentResult = getInitialAssessmentResult(repOrderDTO);

        return PassportAssessmentResult.FAIL.getResult().equals(passportAssessmentResult)
                && InitAssessmentResult.PASS.getResult().equals(initialAssessmentResult);
    }

    public boolean hasCCOutcomeChanged(final int repId) {
        return maatCourtDataService.getRepOrderCCOutcomeByRepId(repId).stream()
                .min(Comparator.comparing(RepOrderCCOutcomeDTO::getId))
                .map(outcome -> outcome.getOutcome() != null
                        && !CrownCourtOutcome.AQUITTED.getCode().equals(outcome.getOutcome()))
                .orElse(false);
    }

    public boolean hasApplicationStatusChanged(RepOrderDTO repOrderDTO, CaseType caseType, String status) {
        log.info("Get applicant details from Crime Apply datastore");
        return CaseType.INDICTABLE.equals(caseType)
                && repOrderDTO != null
                && repOrderDTO.getRorsStatus() != null
                && !repOrderDTO.getRorsStatus().equalsIgnoreCase(status);
    }
}
