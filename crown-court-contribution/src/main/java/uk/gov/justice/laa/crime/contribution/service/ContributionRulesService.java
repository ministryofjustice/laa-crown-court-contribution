package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionRulesService {

    /*
     * Returns active crown-court outcome from object passed in.
     * The active one is the first in the collection (already ordered in get_crown_court_outcomes)
     */
    public CrownCourtOutcome getActiveCCOutcome(List<ApiCrownCourtOutcome> crownCourtOutcomeList) {
        return crownCourtOutcomeList.stream().findFirst()
                .map(ApiCrownCourtOutcome::getOutcome).orElse(null);
    }

    public boolean isContributionRuleApplicable(CaseType caseType,
                                                MagCourtOutcome magCourtOutcome,
                                                CrownCourtOutcome crownCourtOutcome) {

        return CaseType.EITHER_WAY == caseType && crownCourtOutcome == null &&
                (magCourtOutcome == null || Set.of(MagCourtOutcome.COMMITTED_FOR_TRIAL, MagCourtOutcome.COMMITTED,
                        MagCourtOutcome.SENT_FOR_TRIAL, MagCourtOutcome.APPEAL_TO_CC).contains(magCourtOutcome));
    }
}
