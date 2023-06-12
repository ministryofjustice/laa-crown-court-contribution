package uk.gov.justice.laa.crime.contribution.service;

import uk.gov.justice.laa.crime.contribution.dto.FinancialAssessmentDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<FinancialAssessmentDTO> financialAssessments = new ArrayList<>();
        LocalDateTime latestFinAssessmentDate = financialAssessments.stream()
                .map(FinancialAssessmentDTO::getDateCreated)
                .max(LocalDateTime::compareTo)
                .get();
        System.out.println(latestFinAssessmentDate);
    }
}
