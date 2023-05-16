package uk.gov.justice.laa.crime.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AssessmentRequestDTO {

    private String iojResult;
    private String decisionResult;
    private String passportResult;
    private String initResult;
    private String fullResult;
    private String hardshipResult;
}
