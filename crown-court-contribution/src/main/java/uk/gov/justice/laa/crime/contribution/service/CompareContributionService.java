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
        } else {
            return getResultOnActiveContribution(contributionDTO, repOrderDTO, laaTransactionId, repId, activeContribution);
        }
    }

    private int getResultOnNoPreviousContribution(RepOrderDTO repOrderDTO, String laaTransactionId, int repId) {
        int result = 0;
        if (isCaseTypeApealCC(repOrderDTO)) {
            createCorrespondenceStatus(CorrespondenceStatus.APPEAL_CC.getStatus(), repId, laaTransactionId);
        }
        if (isCds15WorkAround(repOrderDTO)) {
            createCorrespondenceStatus(CorrespondenceStatus.CDS15.getStatus(), repId, laaTransactionId);
        }

        if (isReassessment(laaTransactionId, repId)) {
            createCorrespondenceStatus(CorrespondenceStatus.REASS.getStatus(), repId, laaTransactionId);
        }
        return result;
    }

    private int getResultOnActiveContribution(ContributionDTO contributionDTO, RepOrderDTO repOrderDTO, String laaTransactionId, int repId, List<Contribution> contributions) {
        int result = 2;
        Contribution contribution = contributions.get(0);
        if (contributionRecordsAreIdentical(contributionDTO, contribution)) {
            result = getResultOnIdenticalContributions(contributionDTO, repOrderDTO, laaTransactionId, repId);
        } else if (isReassessment(laaTransactionId, repId)) {
            result = 1;
            createCorrespondenceStatus(CorrespondenceStatus.REASS.getStatus(), repId, laaTransactionId);
        }
        return result;
    }

    private int getResultOnIdenticalContributions(ContributionDTO contributionDTO, RepOrderDTO repOrderDTO, String laaTransactionId, int repId) {
        CorrespondenceState status = maatCourtDataService.findCorrespondenceState(repId, laaTransactionId);
        int result = 2;
        MagCourtOutcome magCourtOutcome = contributionDTO.getMagCourtOutcome();
        String mcooOutcome = magCourtOutcome == null ? null : magCourtOutcome.getOutcome();
        if (magCourtOutcomeHasChangedOrCaseTypeAndCorrespondenceSatatusIsApealCC(repOrderDTO, status, mcooOutcome)) {
            result = 1;
            createCorrespondenceStatus(CorrespondenceStatus.APPEAL_CC.getStatus(), repId, laaTransactionId);
        } else {
            result = getResultOnAppealToCCOrCds15WorkAround(repOrderDTO, laaTransactionId, repId, status);
        }
        return result;
    }

    private int getResultOnAppealToCCOrCds15WorkAround(RepOrderDTO repOrderDTO, String laaTransactionId, int repId, CorrespondenceState status) {
        int result = 2;
        if (checkStatusForAppealCC(status)) {
            createCorrespondenceStatus(CorrespondenceStatus.NONE.getStatus(), repId, laaTransactionId);
        } else {
            result = getResultOnCds15EWorkAroundOrReassesment(repOrderDTO, laaTransactionId, repId, status);
        }
        return result;
    }

    private int getResultOnCds15EWorkAroundOrReassesment(RepOrderDTO repOrderDTO, String laaTransactionId, int repId, CorrespondenceState status) {
        int result = 2;
        if (isCds15WorkAround(repOrderDTO)) {
            result = getResultOnCds15WorkAround(laaTransactionId, repId, status);
        }
        if (isReassessment(laaTransactionId, repId)) {
            result = getResultOnReass(laaTransactionId, repId);
        }
        return result;
    }

    private int getResultOnReass(String laaTransactionId, int repId) {
        int result = 2;
        CorrespondenceState status = maatCourtDataService.findCorrespondenceState(repId, laaTransactionId);
        if (checkStatusForReass(status)) {
            createCorrespondenceStatus(CorrespondenceStatus.NONE.getStatus(), repId, laaTransactionId);
        } else {
            result = 1;
            createCorrespondenceStatus(CorrespondenceStatus.REASS.getStatus(), repId, laaTransactionId);
        }
        return result;
    }

    private int getResultOnCds15WorkAround(String laaTransactionId, int repId, CorrespondenceState status) {
        int result = 2;
        if (checkStatusForCds15(status)) {
            createCorrespondenceStatus(CorrespondenceStatus.NONE.getStatus(), repId, laaTransactionId);
        } else if (checkStatusForReass(status)) {
            result = 1;
            createCorrespondenceStatus(CorrespondenceStatus.CDS15.getStatus(), repId, laaTransactionId);
        }
        return result;
    }

    private static boolean isCaseTypeApealCC(RepOrderDTO repOrderDTO) {
        return CaseType.APPEAL_CC.getCaseTypeString().equals(repOrderDTO.getCatyCaseType());
    }

    private boolean isCds15WorkAround(RepOrderDTO repOrderDTO) {
        return contributionService.isCds15WorkAround(repOrderDTO);
    }

    private boolean isReassessment(String laaTransactionId, int repId) {
        return contributionService.checkReassessment(repId, laaTransactionId);
    }

    private static boolean checkStatusForCds15(CorrespondenceState status) {
        return CorrespondenceStatus.CDS15.getStatus().equals(status.getStatus());
    }

    private static boolean checkStatusForAppealCC(CorrespondenceState status) {
        return CorrespondenceStatus.APPEAL_CC.getStatus().equals(status.getStatus());
    }

    private static boolean checkStatusForReass(CorrespondenceState status) {
        return CorrespondenceStatus.REASS.getStatus().equals(status.getStatus());
    }


    private void createCorrespondenceStatus(String statusAppealCc, int repId, String laaTransactionId) {
        CorrespondenceState correspondenceState = CorrespondenceState.builder().status(statusAppealCc).repId(repId).build();
        maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
    }

    private boolean magCourtOutcomeHasChangedOrCaseTypeAndCorrespondenceSatatusIsApealCC(RepOrderDTO repOrderDTO, CorrespondenceState status, String mcooOutcome) {
        return contributionService.hasMessageOutcomeChanged(mcooOutcome, repOrderDTO) ||
                (isCaseTypeApealCC(repOrderDTO)
                        && checkStatusForAppealCC(status));
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