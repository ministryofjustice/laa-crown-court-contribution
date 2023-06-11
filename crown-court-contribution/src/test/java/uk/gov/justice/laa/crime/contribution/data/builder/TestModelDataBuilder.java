package uk.gov.justice.laa.crime.contribution.data.builder;

import org.springframework.stereotype.Component;
import static uk.gov.justice.laa.crime.contribution.common.Constants.*;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionRequestDTO;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;

@Component
public class TestModelDataBuilder {

    public static final String PASSPORT_RESULT_FAIL_CONTINUE= "FAIL CONTINUE";
    public static final Integer CORRESPONDENCE_ID = 1;

    public static AssessmentRequestDTO getAssessmentRequestDTO() {

        return AssessmentRequestDTO.builder()
                .iojResult(PASS)
                .decisionResult(PASS)
                .passportResult(PASS)
                .initResult(PASS)
                .fullResult(FULL)
                .hardshipResult(PASS)
                .build();
    }

    public static ContributionRequestDTO getContributionRequestDTO() {
        return ContributionRequestDTO.builder()
           .iojResult(PASS)
                .decisionResult(PASS)
                .initResult(PASS)
                .fullResult(FULL)
                .hardshipResult(PASS)
                .passportResult(PASS).build();
    }

    public static CorrespondenceRuleAndTemplateInfo getCorrespondenceRuleAndTemplateInfo() {
        return new CorrespondenceRuleAndTemplateInfo() {
            @Override
            public String getCalcContribs() {
                return "N";
            }

            @Override
            public Integer getUpliftCoteId() {
                return CORRESPONDENCE_ID;
            }

            @Override
            public Integer getReassessmentCoteId() {
                return CORRESPONDENCE_ID;
            }

            @Override
            public Integer getId() {
                return CORRESPONDENCE_ID;
            }

            @Override
            public String getCotyCorrespondenceType() {
                return "CONTRIBUTION_NOTICE";
            }

            @Override
            public String getDescription() {
                return "No contributions required";
            }
        };
    }
}
