package uk.gov.justice.laa.crime.contribution.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssessmentRequestDTO {

    private String iojResult;
    private String decisionResult;
    private String passportResult;
    private String initResult;
    private String fullResult;
    private String hardshipResult;
}
