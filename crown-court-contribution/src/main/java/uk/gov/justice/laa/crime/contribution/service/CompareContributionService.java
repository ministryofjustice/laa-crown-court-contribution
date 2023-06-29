package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.CorrespondenceState;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompareContributionService {

    private final MaatCourtDataService maatCourtDataService;

    private final ContributionService contributionService;

    public Integer compareContribution(ContributionDTO compareContributionDTO){
        RepOrderDTO repOrderDTO = compareContributionDTO.getRepOrderDTO();
        String laaTransactionId = compareContributionDTO.getLaaTransactionId();
        Integer repId = repOrderDTO.getId();
        Integer result = null;
        List<Contribution> contributions = maatCourtDataService.findContribution(repId, laaTransactionId, false);
        contributions = contributions.stream()
                .filter(getActiveContribution(repId)).toList();
        if(contributions.isEmpty()) {
            return getResutlOnNoPreviousContribution(repOrderDTO, laaTransactionId, repId);
        } else {
            return getResultOnActiveContribution(compareContributionDTO, repOrderDTO, laaTransactionId, repId, contributions);
        }
    }
    private Integer getResultOnActiveContribution(ContributionDTO compareContributionDTO, RepOrderDTO repOrderDTO, String laaTransactionId, Integer repId, List<Contribution> contributions) {
        Integer result;
        Contribution contribution = contributions.get(0);
        if (contribution.getContributionCap().compareTo(compareContributionDTO.getContributionCap()) == 0 &&
                contribution.getUpfrontContributions().compareTo(compareContributionDTO.getUpfrontContributions()) == 0 &&
                contribution.getMonthlyContributions().compareTo(compareContributionDTO.getMonthlyContributions()) == 0 &&
                contribution.getEffectiveDate().isEqual(compareContributionDTO.getEffectiveDate())) {
            CorrespondenceState status = maatCourtDataService.findCorrespondenceState(repId, laaTransactionId);
            result = 2;

            if (contributionService.hasMessageOutcomeChanged(compareContributionDTO.getMagCourtOutcome().getOutcome(), repOrderDTO) ||
                    (repOrderDTO.getCatyCaseType().equals("APPEAL CC") && status.getStatus().equals("appealCC"))) {

                result = 1;
                CorrespondenceState correspondenceState = CorrespondenceState.builder().status("appealCC").repId(repId).build();
                maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);

            } else if (status.getStatus().equals("APPEAL CC")) {
                CorrespondenceState correspondenceState = CorrespondenceState.builder().status("none").repId(repId).build();
                maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
            } else {
                if(contributionService.isCds15WorkAround(repOrderDTO)) {
                    if(status.getStatus().equals("cds15")) {
                        result = 2;
                        CorrespondenceState correspondenceState = CorrespondenceState.builder().status("none").repId(repId).build();
                        maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
                    } else if(status.getStatus().equals("re-ass")) {
                        result = 1;
                        CorrespondenceState correspondenceState = CorrespondenceState.builder().status("cds15").repId(repId).build();
                        maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
                    }
                }
                if(contributionService.checkReassessment(repId, laaTransactionId)){
                    status = maatCourtDataService.findCorrespondenceState(repId, laaTransactionId);
                    if(status.getStatus().equals("re-ass")) {
                        result = 2;
                        CorrespondenceState correspondenceState = CorrespondenceState.builder().status("none").repId(repId).build();
                        maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
                    } else {
                        result = 1;
                        CorrespondenceState correspondenceState = CorrespondenceState.builder().status("re-ass").repId(repId).build();
                        maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
                    }
                }
            }
        } else {
            result = 1;
            if(contributionService.checkReassessment(repId, laaTransactionId)) {
                CorrespondenceState correspondenceState = CorrespondenceState.builder().status("re-ass").repId(repId).build();
                maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
            }
        }
        return result;
    }

    private Integer getResutlOnNoPreviousContribution(RepOrderDTO repOrderDTO, String laaTransactionId, Integer repId) {
        Integer result;
        result = 0;
        if(repOrderDTO.getCatyCaseType().equals("APPEAL CC")) {
            CorrespondenceState correspondenceState = CorrespondenceState.builder().status("appealCC").repId(repId).build();
            maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
        }
        if(contributionService.isCds15WorkAround(repOrderDTO)){
            CorrespondenceState correspondenceState = CorrespondenceState.builder().status("cds15").repId(repId).build();
            maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
        }

        if(contributionService.checkReassessment(repId, laaTransactionId)){
            CorrespondenceState correspondenceState = CorrespondenceState.builder().status("re-ass").repId(repId).build();
            maatCourtDataService.createCorrespondenceState(correspondenceState, laaTransactionId);
        }
        return result;
    }

    private static Predicate<Contribution> getActiveContribution(Integer repId) {
        return contribution ->
                contribution.getRepId().equals(repId)
                        && contribution.getReplacedDate() == null
                        && contribution.getActive().equals("Y");
    }
}