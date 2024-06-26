package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.dto.ContributionVariationDTO;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.contribution.staticdata.entity.ContributionRulesEntity;
import uk.gov.justice.laa.crime.contribution.staticdata.repository.ContributionRulesRepository;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionRulesService {

    private final ContributionRulesRepository contributionRulesRepository;

    public Optional<ContributionVariationDTO> getContributionVariation(CaseType caseType,
                                                                       MagCourtOutcome magCourtOutcome,
                                                                       CrownCourtOutcome crownCourtOutcome) {

        String mcooOutcome = magCourtOutcome == null ? null : magCourtOutcome.getOutcome();
        String ccooOutcome = crownCourtOutcome == null ? null : crownCourtOutcome.getCode();
        ContributionRulesEntity contributionRulesEntity = contributionRulesRepository
                .findContributionRulesEntitiesByCaseTypeAndVariationNotNullAndMagistratesCourtOutcomeAndCrownCourtOutcome(
                        caseType.getCaseTypeString(), mcooOutcome, ccooOutcome);
        if (contributionRulesEntity != null) {
            return Optional.of(ContributionVariationDTO.builder()
                    .variation(contributionRulesEntity.getVariation())
                    .variationRule(contributionRulesEntity.getVariationRule())
                    .build());
        } else return Optional.empty();
    }

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
        return getContributionVariation(caseType, magCourtOutcome, crownCourtOutcome).isPresent();
    }
}
