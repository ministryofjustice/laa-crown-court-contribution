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
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompareContributionService {

    private final MaatCourtDataService maatCourtDataService;

    private final ContributionService contributionService;

    @Transactional
    public int compareContribution(CalculateContributionDTO calculateContributionDTO, ContributionResult contributionResult) {
        log.info("Start  compareContribution");
        int repId = calculateContributionDTO.getRepId();
        RepOrderDTO repOrderDTO = calculateContributionDTO.getRepOrderDTO();
        List<Contribution> contributions = maatCourtDataService.findContribution(repId, false);
        log.info(" compareContribution.contributions--" + contributions);

        List<Contribution> activeContribution =
                Optional.ofNullable(contributions)
                        .orElse(Collections.emptyList()).stream()
                        .filter(isActiveContribution(repId)).toList();

        log.info(" compareContribution.activeContribution--" + activeContribution);
        if (activeContribution.isEmpty()) {
            return 0; // Set create flag to true
        }
        return getResultOnActiveContribution(calculateContributionDTO, contributionResult, repOrderDTO, repId, activeContribution);
    }

    private int getResultOnActiveContribution(CalculateContributionDTO calculateContributionDTO,
                                              ContributionResult contributionResult, RepOrderDTO repOrderDTO, int repId,
                                              List<Contribution> contributions) {
        int result;
        Contribution contribution = contributions.get(0);
        if (contributionRecordsAreIdentical(contributionResult, contribution)) {
            result = getResultOnIdenticalContributions(calculateContributionDTO, repOrderDTO, repId);
        } else {
            result = 1; // Set create flag to true
        }
        log.info("getResultOnActiveContribution.result--" + result);
        return result;
    }

    private int getResultOnIdenticalContributions(CalculateContributionDTO calculateContributionDTO,
                                                  RepOrderDTO repOrderDTO, int repId) {
        MagCourtOutcome magCourtOutcome = calculateContributionDTO.getMagCourtOutcome();
        String magsOutcome = magCourtOutcome == null ? null : magCourtOutcome.getOutcome();
        if (contributionService.hasMessageOutcomeChanged(magsOutcome, repOrderDTO) || isCaseTypeAppealCC(repOrderDTO)) { // Do we also need to check case type appeal && status appeal???
            return 1; // Set create flag to true
        }
        return 2; // Set create flag to false
    }

    private static boolean isCaseTypeAppealCC(RepOrderDTO repOrderDTO) {
        return CaseType.APPEAL_CC.getCaseTypeString().equals(repOrderDTO.getCatyCaseType());
    }

    private static boolean contributionRecordsAreIdentical(ContributionResult contributionResult,
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