package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

    private final MaatCourtDataService maatCourtDataService;

    public AssessmentResponseDTO getAssessmentResult(final AssessmentRequestDTO request) {
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

    public boolean checkReassessment(final int repId, final String laaTransactionId) {
        log.info("Check if reassessment is required for REP_ID={}", repId);

        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(repId, laaTransactionId);
        long contributionCount = maatCourtDataService.getContributionCount(repId, laaTransactionId);
        List<FinancialAssessmentDTO> financialAssessments = repOrderDTO.getFinancialAssessments();
        List<PassportAssessmentDTO> passportAssessments = repOrderDTO.getPassportAssessments();

        boolean finAssReassFlag = checkFinancialReassessment(contributionCount, financialAssessments);
        boolean passAssReassFlag = checkPassportReassessment(contributionCount, passportAssessments);

        LocalDateTime latestFinAssDate = financialAssessments.stream()
                .map(FinancialAssessmentDTO::getDateCreated)
                .max(LocalDateTime::compareTo)
                .get();

        LocalDateTime latestPassportAssDate = passportAssessments.stream()
                .map(PassportAssessmentDTO::getDateCreated)
                .max(LocalDateTime::compareTo)
                .get();

        if (latestFinAssDate.isAfter(latestPassportAssDate)) {
            return finAssReassFlag;
        } else return passAssReassFlag;
    }

    private boolean checkFinancialReassessment(final long contributionCount, final List<FinancialAssessmentDTO> financialAssessments) {
        Optional<FinancialAssessmentDTO> financialAssessment = financialAssessments.stream()
                .filter(fa -> fa.getReplaced().equals("Y"))
                .findFirst();
        return financialAssessment.isPresent() && contributionCount > 0;
    }

    private boolean checkPassportReassessment(final long contributionCount, final List<PassportAssessmentDTO> passportAssessments) {
        Optional<PassportAssessmentDTO> passportAssessment = passportAssessments.stream()
                .filter(pa -> pa.getReplaced().equals("Y"))
                .findFirst();
        return passportAssessment.isPresent() && contributionCount > 0;
    }

}
