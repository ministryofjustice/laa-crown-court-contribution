package uk.gov.justice.laa.crime.contribution.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;

@Component
public class TestModelDataBuilder {

    public static final String PASSPORT_RESULT_FAIL_CONTINUE= "FAIL CONTINUE";

    public static AssessmentRequestDTO getAssessmentRequestDTO() {

        return AssessmentRequestDTO.builder()
                .iojResult(Constants.PASS)
                .decisionResult(Constants.PASS)
                .passportResult(Constants.PASS)
                .initResult(Constants.PASS)
                .fullResult(Constants.FULL)
                .hardshipResult(Constants.PASS)
                .build();
    }
}