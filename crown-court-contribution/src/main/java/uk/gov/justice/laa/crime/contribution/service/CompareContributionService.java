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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompareContributionService {

    private final MaatCourtDataService maatCourtDataService;

    private final ContributionService contributionService;

    @Transactional
    public boolean shouldCreateContribution(CalculateContributionDTO calculateContributionDTO, ContributionResult contributionResult) {
        log.info("CompareContributionService: shouldCreateContribution starting...");

        int repId = calculateContributionDTO.getRepId();
        RepOrderDTO repOrderDTO = calculateContributionDTO.getRepOrderDTO();
        String magsCourtOutcome = calculateContributionDTO.getMagCourtOutcome() == null ? null
                : calculateContributionDTO.getMagCourtOutcome().getOutcome();
        log.info("--- magsCourtOutcome: {}", magsCourtOutcome);

        List<Contribution> contributions = maatCourtDataService.findContribution(repId, false);
        log.debug("shouldCreateContribution.contributions--" + contributions);
        List<Contribution> activeContribution =
                Optional.ofNullable(contributions)
                        .orElse(Collections.emptyList()).stream()
                        .filter(isActiveContribution(repId)).toList();
        log.info("--- activeContribution--" + activeContribution);

        boolean contributionRecordsIdentical = false;
        boolean messageOutcomeChanged = false;

        if (!activeContribution.isEmpty()) {
            contributionRecordsIdentical = areContributionRecordsIdentical(contributionResult, contributions.get(0));
            messageOutcomeChanged = contributionService.hasMessageOutcomeChanged(magsCourtOutcome, repOrderDTO);

            log.info("--- contributionRecordsIdentical--" + contributionRecordsIdentical);
            log.info("--- messageOutcomeChanged--" + messageOutcomeChanged);
        }
        else {
            log.info("--- No active contribution found");
        }

        if (!activeContribution.isEmpty()
                && contributionRecordsIdentical
                && !(messageOutcomeChanged
                    || CaseType.APPEAL_CC.getCaseTypeString().equals(repOrderDTO.getCatyCaseType()))) {
            log.info("--- Contributions should not be created");

            return false;
        }

        log.info("Contributions should be created");

        return true;
    }

    private static boolean areContributionRecordsIdentical(ContributionResult contributionResult,
                                                           Contribution contribution) {
        log.info("--- ACRI --- inside areContributionRecordsIdentical ---");

        log.info("--- ACRI - Contribution Result: {}", contributionResult);
        log.info("--- ACRI - Contribution: {}", contribution);

        log.info("--- ACRI ---contribution cap equal?: {}", contribution.getContributionCap().compareTo(contributionResult.contributionCap()) == 0);
        log.info("--- ACRI ---upfront contributions equal?: {}", contribution.getUpfrontContributions().compareTo(contributionResult.upfrontAmount()) == 0);
        log.info("--- ACRI ---monthly contributions equal?: {}", contribution.getMonthlyContributions().compareTo(contributionResult.monthlyAmount()) == 0);
        log.info("--- ACRI ---effective date equal?: {}", contribution.getEffectiveDate().isEqual(contributionResult.effectiveDate()));

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