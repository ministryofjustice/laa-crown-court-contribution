package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.crime.contribution.builder.AssessmentRequestDTOBuilder;
import uk.gov.justice.laa.crime.contribution.builder.ContributionResponseDTOBuilder;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentResponseDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;
import uk.gov.justice.laa.crime.contribution.repository.CorrespondenceRuleRepository;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CorrespondenceType;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

    public static final String INEL = "INEL";
    private final CorrespondenceRuleRepository correspondenceRuleRepository;

    public AssessmentResponseDTO getAssessmentResult(AssessmentRequestDTO request) {
        AssessmentResponseDTO response = new AssessmentResponseDTO();
        response.setIojResult(Optional.ofNullable(request.getDecisionResult())
                .orElse(request.getIojResult()));

        if (StringUtils.isNotBlank(request.getPassportResult())) {

            if (Set.of(Constants.PASS, Constants.TEMP).contains(request.getPassportResult())) {
                response.setMeansResult(Constants.PASSPORT);
            } else if (Constants.FAIL.equals(request.getPassportResult())) {
                response.setMeansResult(Constants.FAILPORT);
            } else if (Constants.PASS.equals(request.getInitResult()) ||
                    Constants.PASS.equals(request.getFullResult()) ||
                    Constants.PASS.equals(request.getHardshipResult())) {
                response.setMeansResult(Constants.PASS);
            } else if (Set.of(Constants.FAIL, Constants.FULL, Constants.HARDSHIP_APPLICATION).contains(request.getInitResult()) &&
                    (Constants.FAIL.equals(request.getFullResult())) &&
                    (Constants.FAIL.equals(Optional.ofNullable(request.getHardshipResult()).orElse(Constants.FAIL)))) {
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
        contributionResponse.setDoContribs('N');
        contributionResponse.setCalcContribs('N');

        AssessmentResponseDTO assessmentResponseDTO = getAssessmentResult(assessmentRequestDTO);
        request.setIojResult(assessmentResponseDTO.getIojResult());
        request.setMeansResult(assessmentResponseDTO.getMeansResult());

        if (Set.of("INDICTABLE", "EITHER WAY", "CC ALREADY").contains(request.getCaseType())
                || request.getEffectiveDate() != null
                || ((request.getCaseType() != null && (request.getCaseType().getCaseTypeString() != null && request.getCaseType().getCaseTypeString().equals("APPEAL CC")))
                && (request.getIojResult() != null && request.getIojResult().equals("PASS")))) {
            contributionResponse.setDoContribs('Y');
        }

        if (contributionResponse.getDoContribs() == 'Y') {
            CorrespondenceRuleAndTemplateInfo processedCases = getCoteInfo(request);
            if (processedCases == null || processedCases.getId() == 0) {
                contributionResponse.setDoContribs('N');
                contributionResponse.setCalcContribs('N');
            }
            contributionResponseDTO = ContributionResponseDTOBuilder.build(processedCases);
            contributionResponseDTO.setDoContribs(contributionResponse.getDoContribs());
            contributionResponseDTO.setCalcContribs(contributionResponse.getCalcContribs());

            if (processedCases != null && CorrespondenceType.getFrom(processedCases.getCotyCorrespondenceType()) != null) {
                    contributionResponseDTO.setCorrespondenceTypeDesc(CorrespondenceType.getFrom(processedCases.getCotyCorrespondenceType()).getDescription());
            }

        }

        if(request.getMonthlyContribs() > 0 || (request.getFullResult() != null && request.getFullResult().equals(INEL))) {
            contributionResponse.setDoContribs('Y');
        }

        if(request.getRemoveContribs() != null && request.getRemoveContribs().equals("Y")) {
            contributionResponse.setCalcContribs('N');
        }

        return contributionResponseDTO;
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


}
