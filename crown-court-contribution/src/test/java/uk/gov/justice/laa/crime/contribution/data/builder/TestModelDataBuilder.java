package uk.gov.justice.laa.crime.contribution.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionVariationDTO;
import uk.gov.justice.laa.crime.contribution.model.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.contribution.model.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.contribution.staticdata.entity.ContributionRulesEntity;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CrownCourtOutcome;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestModelDataBuilder {

    public static final String PASSPORT_RESULT_FAIL_CONTINUE = "FAIL CONTINUE";

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

    public static ContributionVariationDTO getContributionVariationDTO() {
        return ContributionVariationDTO.builder()
                .variation("SQL COSTS")
                .variationRule("+")
                .build();
    }

    public static ContributionRulesEntity getContributionRules() {
        return ContributionRulesEntity.builder()
                .variation("SQL COSTS")
                .variationRule("+")
                .build();
    }

    public static ApiCrownCourtOutcome getApiCrownCourtOutcome(CrownCourtOutcome crownCourtOutcome) {
        return new ApiCrownCourtOutcome()
                .withOutcome(crownCourtOutcome);
    }

    public static ApiCrownCourtSummary getApiCrownCourtSummary() {
        List<ApiCrownCourtOutcome> apiCrownCourtOutcomeList = new ArrayList<>();
        apiCrownCourtOutcomeList.add(getApiCrownCourtOutcome(CrownCourtOutcome.ABANDONED));
        apiCrownCourtOutcomeList.add(getApiCrownCourtOutcome(CrownCourtOutcome.PART_CONVICTED));
        apiCrownCourtOutcomeList.add(getApiCrownCourtOutcome(CrownCourtOutcome.SUCCESSFUL));
        return new ApiCrownCourtSummary()
                .withCrownCourtOutcome(apiCrownCourtOutcomeList);
    }

}
