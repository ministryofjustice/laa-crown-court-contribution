package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {

    private final MaatCourtDataService maatCourtDataService;

    public AssessmentResponseDTO getAssessmentResult(final AssessmentRequestDTO request) {
        AssessmentResponseDTO response = new AssessmentResponseDTO();
        response.setIojResult(ofNullable(request.getDecisionResult())
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

    public boolean checkReassessment(final int repId, final String laaTransactionId) {
        log.info("Check if reassessment is required for REP_ID={}", repId);

        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(repId, laaTransactionId);
        long contributionCount = maatCourtDataService.getContributionCount(repId, laaTransactionId);
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

}
