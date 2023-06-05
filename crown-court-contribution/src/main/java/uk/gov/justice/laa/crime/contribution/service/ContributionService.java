package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentResponseDTO;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionService {


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
}
