package uk.gov.justice.laa.crime.contribution.dto;

import lombok.Builder;

@Builder
public record AssessmentResults(String passportResult, String initResult, String fullResult, String hardshipResult) {}
