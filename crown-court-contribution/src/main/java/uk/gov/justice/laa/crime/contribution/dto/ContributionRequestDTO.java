package uk.gov.justice.laa.crime.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ContributionRequestDTO {
    private String magCourtOutcome;
    private String crownCourtOutcome;
    private CaseType caseType;
    private LocalDate effectiveDate;
    private String iojResult;
    private String decisionResult;
    private String passportResult;
    private String initResult;
    private String fullResult;
    private String hardshipResult;
    private Integer monthlyContribs;
    private String removeContribs;
    private String meansResult;

}
