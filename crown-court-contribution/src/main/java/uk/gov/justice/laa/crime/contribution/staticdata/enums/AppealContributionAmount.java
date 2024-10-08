package uk.gov.justice.laa.crime.contribution.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum AppealContributionAmount {

    NO_CONTRIBUTION(new BigDecimal(0)),
    PART_CONTRIBUTION(new BigDecimal(250)),
    FULL_CONTRIBUTION(new BigDecimal(500));

    private final BigDecimal contributionAmount;

    public static AppealContributionAmount calculate(AppealType appealType, CrownCourtOutcome appealOutcome, AssessmentResult assessmentResult) {
        if (AssessmentResult.PASS.equals(assessmentResult)) {
            return NO_CONTRIBUTION;
        }

        if (CrownCourtOutcome.SUCCESSFUL.equals(appealOutcome)) {
            return NO_CONTRIBUTION;
        }

        if ((AppealType.ACS.equals(appealType) && CrownCourtOutcome.PART_SUCCESS.equals(appealOutcome))
                || (AppealType.ASE.equals(appealType) && CrownCourtOutcome.UNSUCCESSFUL.equals(appealOutcome))) {
            return PART_CONTRIBUTION;
        }

        if (CrownCourtOutcome.UNSUCCESSFUL.equals(appealOutcome)) {
            return FULL_CONTRIBUTION;
        }

        return NO_CONTRIBUTION;
    }
}
