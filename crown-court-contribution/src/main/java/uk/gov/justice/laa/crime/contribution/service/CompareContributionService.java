package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.ContributionResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompareContributionService {

    private final MaatCourtDataService maatCourtDataService;

    @Transactional
    public boolean shouldCreateContribution(CalculateContributionDTO calculateContributionDTO, ContributionResult contributionResult) {
        int repId = calculateContributionDTO.getRepId();
        List<Contribution> contributions = maatCourtDataService.findContribution(repId, false);
        log.debug("shouldCreateContribution.contributions--" + contributions);
        Optional<Contribution> activeContribution =
            Optional.ofNullable(contributions)
                .orElse(Collections.emptyList()).stream()
                .filter(isActiveContribution(repId))
                .findFirst();

        if (activeContribution.isPresent()
                && areContributionRecordsIdentical(contributionResult, activeContribution.get())
                && isMagsCourtOutcomeUnchanged(calculateContributionDTO.getMagCourtOutcome(),
                                               calculateContributionDTO.getRepOrderDTO())) {
            log.info("Contributions should not be created");
            return false;
        }
        log.info("Contributions should be created");
        return true;
    }

    private boolean isMagsCourtOutcomeUnchanged(MagCourtOutcome magsCourtOutcome, RepOrderDTO repOrderDTO) {
        if (repOrderDTO == null) return true;

        return Objects.equals(magsCourtOutcome, MagCourtOutcome.getFrom(repOrderDTO.getMagsOutcome()));
    }

    private static boolean areContributionRecordsIdentical(ContributionResult contributionResult,
                                                           Contribution contribution) {
        return contribution.getContributionCap().compareTo(contributionResult.contributionCap()) == 0 &&
                contribution.getUpfrontContributions()
                        .compareTo(contributionResult.upfrontAmount()) == 0 &&
                contribution.getMonthlyContributions()
                        .compareTo(contributionResult.monthlyAmount()) == 0 &&
                contribution.getEffectiveDate().isEqual(contributionResult.effectiveDate());
    }

    private static Predicate<Contribution> isActiveContribution(int repId) {
        return contribution ->
                contribution.getRepId().equals(repId)
                        && contribution.getReplacedDate() == null
                        && contribution.getActive().equals("Y");
    }
}