package uk.gov.justice.laa.crime.contribution.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CrownCourtAppealOutcome;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum AppealContributionAmount {

    NO_CONTRIBUTION(new BigDecimal(0)),
    PART_CONTRIBUTION(new BigDecimal(250)),
    FULL_CONTRIBUTION(new BigDecimal(500));

    private final BigDecimal contributionAmount;

    public static AppealContributionAmount calculate(AppealType appealType, CrownCourtAppealOutcome appealOutcome, AssessmentResult assessmentResult) {
        if (AssessmentResult.PASS.equals(assessmentResult)) {
            return NO_CONTRIBUTION;
        }

        if (CrownCourtAppealOutcome.SUCCESSFUL.equals(appealOutcome)) {
            return NO_CONTRIBUTION;
        }

        if ((AppealType.ACS.equals(appealType) && CrownCourtAppealOutcome.PART_SUCCESS.equals(appealOutcome))
                || (AppealType.ASE.equals(appealType) && CrownCourtAppealOutcome.UNSUCCESSFUL.equals(appealOutcome))) {
            return PART_CONTRIBUTION;
        }

        if (CrownCourtAppealOutcome.UNSUCCESSFUL.equals(appealOutcome)) {
            return FULL_CONTRIBUTION;
        }

        return NO_CONTRIBUTION;
    }
}
