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
import uk.gov.justice.laa.crime.enums.contribution.CorrespondenceStatus;

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
            return getResultOnNoPreviousContribution(repOrderDTO, repId);
        }
        return getResultOnActiveContribution(calculateContributionDTO, contributionResult, repOrderDTO, repId, activeContribution);
    }

    private int getResultOnNoPreviousContribution(RepOrderDTO repOrderDTO, int repId) {
        if (isCaseTypeAppealCC(repOrderDTO)) {
        }
        if (isCds15WorkAround(repOrderDTO)) {
        }
        return 0;
    }

    private int getResultOnActiveContribution(CalculateContributionDTO calculateContributionDTO,
                                              ContributionResult contributionResult, RepOrderDTO repOrderDTO, int repId,
                                              List<Contribution> contributions) {
        int result;
        Contribution contribution = contributions.get(0);
        if (contributionRecordsAreIdentical(contributionResult, contribution)) {
            result = getResultOnIdenticalContributions(calculateContributionDTO, repOrderDTO, repId);
        } else {
            result = 1;
        }
        log.info("getResultOnActiveContribution.result--" + result);
        return result;
    }

    private int getResultOnIdenticalContributions(CalculateContributionDTO calculateContributionDTO,
                                                  RepOrderDTO repOrderDTO, int repId) {
        CorrespondenceStatus status = maatCourtDataService.findCorrespondenceState(repId);

        if (status == null) {
            status = CorrespondenceStatus.NONE;
        }

        MagCourtOutcome magCourtOutcome = calculateContributionDTO.getMagCourtOutcome();
        String magsOutcome = magCourtOutcome == null ? null : magCourtOutcome.getOutcome();
        if (magCourtOutcomeHasChangedOrCaseTypeAndCorrespondenceStatusIsApealCC(repOrderDTO, status, magsOutcome)) {
            return 1;
        }
        return getResultOnAppealToCCOrCds15WorkAroundOrReassessment(repOrderDTO, repId, status);
    }

    private int getResultOnAppealToCCOrCds15WorkAroundOrReassessment(RepOrderDTO repOrderDTO, int repId,
                                                                     CorrespondenceStatus status) {
        if (isStatusAppealCC(status)) {
            return 2;
        }
        return getResultOnCds15EWorkAroundOrReassessment(repOrderDTO, repId, status);
    }

    private int getResultOnCds15EWorkAroundOrReassessment(RepOrderDTO repOrderDTO, int repId,
                                                          CorrespondenceStatus status) {
        int result = 2;
        if (isCds15WorkAround(repOrderDTO)) {
            result = getResultOnCds15WorkAround(repId, status);
        }
        return result;
    }

    private int getResultOnCds15WorkAround(int repId, CorrespondenceStatus status) {
        int result = 2;
        if (isStatusCds15(status)) {
        } else if (isStatusReass(status)) {
            result = 1;
        }
        return result;
    }

    private static boolean isCaseTypeAppealCC(RepOrderDTO repOrderDTO) {
        return CaseType.APPEAL_CC.getCaseTypeString().equals(repOrderDTO.getCatyCaseType());
    }

    private boolean isCds15WorkAround(RepOrderDTO repOrderDTO) {
        return contributionService.isCds15WorkAround(repOrderDTO);
    }

    private static boolean isStatusCds15(CorrespondenceStatus status) {
        return CorrespondenceStatus.CDS15.getStatus().equals(status.getStatus());
    }

    private static boolean isStatusAppealCC(CorrespondenceStatus status) {
        return CorrespondenceStatus.APPEAL_CC.getStatus().equals(status.getStatus());
    }

    private static boolean isStatusReass(CorrespondenceStatus status) {
        return CorrespondenceStatus.REASS.getStatus().equals(status.getStatus());
    }

    private boolean magCourtOutcomeHasChangedOrCaseTypeAndCorrespondenceStatusIsApealCC(RepOrderDTO repOrderDTO,
                                                                                        CorrespondenceStatus status,
                                                                                        String mcooOutcome) {
        return contributionService.hasMessageOutcomeChanged(mcooOutcome, repOrderDTO) ||
                (isCaseTypeAppealCC(repOrderDTO)
                        && isStatusAppealCC(status));
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