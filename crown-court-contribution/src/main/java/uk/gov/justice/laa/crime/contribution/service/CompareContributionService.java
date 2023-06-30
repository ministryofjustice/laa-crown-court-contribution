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
import uk.gov.justice.laa.crime.contribution.staticdata.enums.MagCourtOutcome;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompareContributionService {

    public static final String STATUS_APPEAL_CC = "appealCC";
    public static final String STATUS_NONE = "none";
    public static final String STATUS_CDS_15 = "cds15";
    public static final String STATUS_RE_ASS = "re-ass";

    private final MaatCourtDataService maatCourtDataService;

    private final ContributionService contributionService;

    @Transactional
    public Integer compareContribution(ContributionDTO contributionDTO){
        String laaTransactionId = contributionDTO.getLaaTransactionId();
        Integer repId = contributionDTO.getRepId();
        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(repId, laaTransactionId);
        List<Contribution> contributions = maatCourtDataService.findContribution(repId, laaTransactionId, false);
        contributions = Optional.ofNullable(contributions).orElse(Collections.emptyList()).stream()
                .filter(isActiveContribution(repId)).toList();
        if(contributions.isEmpty()) {
            return getResutlOnNoPreviousContribution(repOrderDTO, laaTransactionId, repId);
        } else {
            return getResultOnActiveContribution(contributionDTO, repOrderDTO, laaTransactionId, repId, contributions);
        }
    }

    private Integer getResutlOnNoPreviousContribution(RepOrderDTO repOrderDTO, String laaTransactionId, Integer repId) {
        Integer result = 0;
        if(repOrderDTO.getCatyCaseType().equals(CaseType.APPEAL_CC.getCaseTypeString())) {
            CorrespondenceState correspondenceState = CorrespondenceState.builder().status(STATUS_APPEAL_CC).repId(repId).build();
            maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
        }
        if(contributionService.isCds15WorkAround(repOrderDTO)){
            CorrespondenceState correspondenceState = CorrespondenceState.builder().status(STATUS_CDS_15).repId(repId).build();
            maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
        }

        if(contributionService.checkReassessment(repId, laaTransactionId)){
            CorrespondenceState correspondenceState = CorrespondenceState.builder().status(STATUS_RE_ASS).repId(repId).build();
            maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
        }
        return result;
    }

    private Integer getResultOnActiveContribution(ContributionDTO contributionDTO, RepOrderDTO repOrderDTO, String laaTransactionId, Integer repId, List<Contribution> contributions) {
        Integer result;
        Contribution contribution = contributions.get(0);
        if (contributionRecordsAreIdentical(contributionDTO, contribution)) {
            CorrespondenceState status = maatCourtDataService.findCorrespondenceState(repId, laaTransactionId);
            result = 2;
            MagCourtOutcome MagCourtOutcome = contributionDTO.getMagCourtOutcome();
            String mcooOutcome = MagCourtOutcome == null ? null : MagCourtOutcome.getOutcome();
            if (contributionService.hasMessageOutcomeChanged(mcooOutcome, repOrderDTO) ||
                    (repOrderDTO.getCatyCaseType().equals(CaseType.APPEAL_CC.getCaseTypeString()) && status.getStatus().equals(STATUS_APPEAL_CC))) {
                result = 1;
                CorrespondenceState correspondenceState = CorrespondenceState.builder().status(STATUS_APPEAL_CC).repId(repId).build();
                maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
            } else if (status.getStatus().equals(CaseType.APPEAL_CC.getCaseTypeString())) {
                CorrespondenceState correspondenceState = CorrespondenceState.builder().status(STATUS_NONE).repId(repId).build();
                maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
            } else {
                if(contributionService.isCds15WorkAround(repOrderDTO)) {
                    if(status.getStatus().equals(STATUS_CDS_15)) {
                        result = 2;
                        CorrespondenceState correspondenceState = CorrespondenceState.builder().status(STATUS_NONE).repId(repId).build();
                        maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
                    } else if(status.getStatus().equals(STATUS_RE_ASS)) {
                        result = 1;
                        CorrespondenceState correspondenceState = CorrespondenceState.builder().status(STATUS_CDS_15).repId(repId).build();
                        maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
                    }
                }
                if(contributionService.checkReassessment(repId, laaTransactionId)){
                    status = maatCourtDataService.findCorrespondenceState(repId, laaTransactionId);
                    if(status.getStatus().equals(STATUS_RE_ASS)) {
                        result = 2;
                        CorrespondenceState correspondenceState = CorrespondenceState.builder().status(STATUS_NONE).repId(repId).build();
                        maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
                    } else {
                        result = 1;
                        CorrespondenceState correspondenceState = CorrespondenceState.builder().status(STATUS_RE_ASS).repId(repId).build();
                        maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
                    }
                }
            }
        } else {
            result = 1;
            if(contributionService.checkReassessment(repId, laaTransactionId)) {
                CorrespondenceState correspondenceState = CorrespondenceState.builder().status(STATUS_RE_ASS).repId(repId).build();
                maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
            }
        }
        return result;
    }

    private static boolean contributionRecordsAreIdentical(ContributionDTO compareContributionDTO, Contribution contribution) {
        return contribution.getContributionCap().compareTo(compareContributionDTO.getContributionCap()) == 0 &&
                contribution.getUpfrontContributions().compareTo(compareContributionDTO.getUpfrontContributions()) == 0 &&
                contribution.getMonthlyContributions().compareTo(compareContributionDTO.getMonthlyContributions()) == 0 &&
                contribution.getEffectiveDate().isEqual(compareContributionDTO.getEffectiveDate());
    }


    private static Predicate<Contribution> isActiveContribution(Integer repId) {
        return contribution ->
                contribution.getRepId().equals(repId)
                        && contribution.getReplacedDate() == null
                        && contribution.getActive().equals("Y");
    }
}