package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.CorrespondenceState;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CorrespondenceStatus;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.MagCourtOutcome;

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
    public int compareContribution(ContributionDTO contributionDTO) {
        String laaTransactionId = contributionDTO.getLaaTransactionId();
        int repId = contributionDTO.getRepId();
        RepOrderDTO repOrderDTO = contributionDTO.getRepOrderDTO();
        List<Contribution> contributions = maatCourtDataService.findContribution(repId, laaTransactionId, false);
        List<Contribution> activeContribution = Optional.ofNullable(contributions).orElse(Collections.emptyList()).stream()
                .filter(isActiveContribution(repId)).toList();
        if (activeContribution.isEmpty()) {
            return getResultOnNoPreviousContribution(repOrderDTO, laaTransactionId, repId);
        }
        return getResultOnActiveContribution(contributionDTO, repOrderDTO, laaTransactionId, repId, activeContribution);
    }

    private int getResultOnNoPreviousContribution(RepOrderDTO repOrderDTO, String laaTransactionId, int repId) {
        if (isCaseTypeAppealCC(repOrderDTO)) {
            setCorrespondenceStatus(CorrespondenceStatus.APPEAL_CC.getStatus(), repId, laaTransactionId);
        }
        if (isCds15WorkAround(repOrderDTO)) {
            setCorrespondenceStatus(CorrespondenceStatus.CDS15.getStatus(), repId, laaTransactionId);
        }
        if (isReassessment(laaTransactionId, repOrderDTO)) {
            setCorrespondenceStatus(CorrespondenceStatus.REASS.getStatus(), repId, laaTransactionId);
        }
        return 0;
    }

    private int getResultOnActiveContribution(ContributionDTO contributionDTO, RepOrderDTO repOrderDTO, String laaTransactionId, int repId, List<Contribution> contributions) {
        int result = 2;
        Contribution contribution = contributions.get(0);
        if (contributionRecordsAreIdentical(contributionDTO, contribution)) {
            result = getResultOnIdenticalContributions(contributionDTO, repOrderDTO, laaTransactionId, repId);
        } else if (isReassessment(laaTransactionId, repOrderDTO)) {
            result = 1;
            setCorrespondenceStatus(CorrespondenceStatus.REASS.getStatus(), repId, laaTransactionId);
        }
        return result;
    }

    private int getResultOnIdenticalContributions(ContributionDTO contributionDTO, RepOrderDTO repOrderDTO, String laaTransactionId, int repId) {
        CorrespondenceState status = maatCourtDataService.findCorrespondenceState(repId, laaTransactionId);
        MagCourtOutcome magCourtOutcome = contributionDTO.getMagCourtOutcome();
        String mcooOutcome = magCourtOutcome == null ? null : magCourtOutcome.getOutcome();
        if (magCourtOutcomeHasChangedOrCaseTypeAndCorrespondenceStatusIsApealCC(repOrderDTO, status, mcooOutcome)) {
            setCorrespondenceStatus(CorrespondenceStatus.APPEAL_CC.getStatus(), repId, laaTransactionId);
            return 1;
        }
        return getResultOnAppealToCCOrCds15WorkAroundOrReassessment(repOrderDTO, laaTransactionId, repId, status);
    }

    private int getResultOnAppealToCCOrCds15WorkAroundOrReassessment(RepOrderDTO repOrderDTO, String laaTransactionId, int repId, CorrespondenceState status) {
        if (isStatusAppealCC(status)) {
            setCorrespondenceStatus(CorrespondenceStatus.NONE.getStatus(), repId, laaTransactionId);
            return 2;
        }
        return getResultOnCds15EWorkAroundOrReassessment(repOrderDTO, laaTransactionId, repId, status);
    }

    private int getResultOnCds15EWorkAroundOrReassessment(RepOrderDTO repOrderDTO, String laaTransactionId, int repId, CorrespondenceState status) {
        int result = 2;
        if (isCds15WorkAround(repOrderDTO)) {
            result = getResultOnCds15WorkAround(laaTransactionId, repId, status);
        }
        if (isReassessment(laaTransactionId, repOrderDTO)) {
            result = getResultOnReassessment(laaTransactionId, repId);
        }
        return result;
    }

    private int getResultOnCds15WorkAround(String laaTransactionId, int repId, CorrespondenceState status) {
        int result = 2;
        if (isStatusCds15(status)) {
            setCorrespondenceStatus(CorrespondenceStatus.NONE.getStatus(), repId, laaTransactionId);
        } else if (isStatusReass(status)) {
            result = 1;
            setCorrespondenceStatus(CorrespondenceStatus.CDS15.getStatus(), repId, laaTransactionId);
        }
        return result;
    }

    private int getResultOnReassessment(String laaTransactionId, int repId) {
        int result = 2;
        CorrespondenceState status = maatCourtDataService.findCorrespondenceState(repId, laaTransactionId);
        if (isStatusReass(status)) {
            setCorrespondenceStatus(CorrespondenceStatus.NONE.getStatus(), repId, laaTransactionId);
        } else {
            result = 1;
            setCorrespondenceStatus(CorrespondenceStatus.REASS.getStatus(), repId, laaTransactionId);
        }
        return result;
    }

    private static boolean isCaseTypeAppealCC(RepOrderDTO repOrderDTO) {
        return CaseType.APPEAL_CC.getCaseTypeString().equals(repOrderDTO.getCatyCaseType());
    }

    private boolean isCds15WorkAround(RepOrderDTO repOrderDTO) {
        return contributionService.isCds15WorkAround(repOrderDTO);
    }

    private boolean isReassessment(String laaTransactionId, RepOrderDTO repOrderDTO) {
        return contributionService.checkReassessment(repOrderDTO, laaTransactionId);
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

    private void setCorrespondenceStatus(String statusAppealCc, int repId, String laaTransactionId) {
        CorrespondenceState correspondenceState = CorrespondenceState.builder().status(statusAppealCc).repId(repId).build();
        maatCourtDataService.updateCorrespondenceState(correspondenceState, laaTransactionId);
    }

    private boolean magCourtOutcomeHasChangedOrCaseTypeAndCorrespondenceStatusIsApealCC(RepOrderDTO repOrderDTO, CorrespondenceState status, String mcooOutcome) {
        return contributionService.hasMessageOutcomeChanged(mcooOutcome, repOrderDTO) ||
                (isCaseTypeAppealCC(repOrderDTO)
                        && isStatusAppealCC(status));
    }

    private static boolean contributionRecordsAreIdentical(ContributionDTO compareContributionDTO, Contribution contribution) {
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