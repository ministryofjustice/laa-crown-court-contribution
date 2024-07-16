package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.CorrespondenceState;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CorrespondenceStatus;
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
    public int compareContribution(CalculateContributionDTO calculateContributionDTO) {
        log.info("Start  compareContribution");
        int repId = calculateContributionDTO.getRepId();
        RepOrderDTO repOrderDTO = calculateContributionDTO.getRepOrderDTO();
        List<Contribution> contributions = maatCourtDataService.findContribution(repId, false);
        log.info(" compareContribution.contributions--"+ contributions);
        List<Contribution> activeContribution = Optional.ofNullable(contributions).orElse(Collections.emptyList()).stream()
                .filter(isActiveContribution(repId)).toList();
        log.info(" compareContribution.activeContribution--"+ activeContribution);
        if (activeContribution.isEmpty()) {
            return getResultOnNoPreviousContribution(repOrderDTO, repId);
        }
        return getResultOnActiveContribution(calculateContributionDTO, repOrderDTO, repId, activeContribution);
    }

    private int getResultOnNoPreviousContribution(RepOrderDTO repOrderDTO, int repId) {
        if (isCaseTypeAppealCC(repOrderDTO)) {
            setCorrespondenceStatus(CorrespondenceStatus.APPEAL_CC.getStatus(), repId);
        }
        if (isCds15WorkAround(repOrderDTO)) {
            setCorrespondenceStatus(CorrespondenceStatus.CDS15.getStatus(), repId);
        }
        return 0;
    }

    private int getResultOnActiveContribution(CalculateContributionDTO calculateContributionDTO, RepOrderDTO repOrderDTO, int repId, List<Contribution> contributions) {
        int result = 2;
        Contribution contribution = contributions.get(0);
        if (contributionRecordsAreIdentical(calculateContributionDTO, contribution)) {
            result = getResultOnIdenticalContributions(calculateContributionDTO, repOrderDTO, repId);
        }
        log.info("getResultOnActiveContribution.result--"+ result);
        return result;
    }

    private int getResultOnIdenticalContributions(CalculateContributionDTO calculateContributionDTO, RepOrderDTO repOrderDTO, int repId) {
        CorrespondenceState status = maatCourtDataService.findCorrespondenceState(repId);
        MagCourtOutcome magCourtOutcome = calculateContributionDTO.getMagCourtOutcome();
        String mcooOutcome = magCourtOutcome == null ? null : magCourtOutcome.getOutcome();
        if (magCourtOutcomeHasChangedOrCaseTypeAndCorrespondenceStatusIsApealCC(repOrderDTO, status, mcooOutcome)) {
            setCorrespondenceStatus(CorrespondenceStatus.APPEAL_CC.getStatus(), repId);
            return 1;
        }
        return getResultOnAppealToCCOrCds15WorkAroundOrReassessment(repOrderDTO, repId, status);
    }

    private int getResultOnAppealToCCOrCds15WorkAroundOrReassessment(RepOrderDTO repOrderDTO, int repId, CorrespondenceState status) {
        if (isStatusAppealCC(status)) {
            setCorrespondenceStatus(CorrespondenceStatus.NONE.getStatus(), repId);
            return 2;
        }
        return getResultOnCds15EWorkAroundOrReassessment(repOrderDTO, repId, status);
    }

    private int getResultOnCds15EWorkAroundOrReassessment(RepOrderDTO repOrderDTO, int repId, CorrespondenceState status) {
        int result = 2;
        if (isCds15WorkAround(repOrderDTO)) {
            result = getResultOnCds15WorkAround(repId, status);
        }
        return result;
    }

    private int getResultOnCds15WorkAround(int repId, CorrespondenceState status) {
        int result = 2;
        if (isStatusCds15(status)) {
            setCorrespondenceStatus(CorrespondenceStatus.NONE.getStatus(), repId);
        } else if (isStatusReass(status)) {
            result = 1;
            setCorrespondenceStatus(CorrespondenceStatus.CDS15.getStatus(), repId);
        }
        return result;
    }

    private static boolean isCaseTypeAppealCC(RepOrderDTO repOrderDTO) {
        return CaseType.APPEAL_CC.getCaseTypeString().equals(repOrderDTO.getCatyCaseType());
    }

    private boolean isCds15WorkAround(RepOrderDTO repOrderDTO) {
        return contributionService.isCds15WorkAround(repOrderDTO);
    }

    private static boolean isStatusCds15(CorrespondenceState status) {
        return CorrespondenceStatus.CDS15.getStatus().equals(status.getStatus());
    }

    private static boolean isStatusAppealCC(CorrespondenceState status) {
        return CorrespondenceStatus.APPEAL_CC.getStatus().equals(status.getStatus());
    }

    private static boolean isStatusReass(CorrespondenceState status) {
        return CorrespondenceStatus.REASS.getStatus().equals(status.getStatus());
    }

    private void setCorrespondenceStatus(String statusAppealCc, int repId) {
        CorrespondenceState correspondenceState = CorrespondenceState.builder().status(statusAppealCc).repId(repId).build();
        maatCourtDataService.updateCorrespondenceState(correspondenceState);
    }

    private boolean magCourtOutcomeHasChangedOrCaseTypeAndCorrespondenceStatusIsApealCC(RepOrderDTO repOrderDTO, CorrespondenceState status, String mcooOutcome) {
        return contributionService.hasMessageOutcomeChanged(mcooOutcome, repOrderDTO) ||
                (isCaseTypeAppealCC(repOrderDTO)
                        && isStatusAppealCC(status));
    }

    private static boolean contributionRecordsAreIdentical(CalculateContributionDTO compareContributionDTO, Contribution contribution) {
        return contribution.getContributionCap().compareTo(compareContributionDTO.getContributionCap()) == 0 &&
                contribution.getUpfrontContributions().compareTo(compareContributionDTO.getUpfrontContributions()) == 0 &&
                contribution.getMonthlyContributions().compareTo(compareContributionDTO.getMonthlyContributions()) == 0 &&
                contribution.getEffectiveDate().isEqual(compareContributionDTO.getEffectiveDate());
    }

    private static Predicate<Contribution> isActiveContribution(int repId) {
        return contribution ->
                contribution.getRepId().equals(repId)
                        && contribution.getReplacedDate() == null
                        && contribution.getActive().equals("Y");
    }
}