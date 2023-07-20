package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.builder.ContributionDTOBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.CalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculateContributionService {

    private final MaatCourtDataService maatCourtDataService;
    private final AppealContributionService appealContributionService;

    private final CompareContributionService compareContributionService;



    public CalculateContributionResponse calculateContribution(ContributionDTO contributionDTO, String laaTransactionId) {
        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(contributionDTO.getRepId(), laaTransactionId);
        contributionDTO.setRepOrderDTO(repOrderDTO);

        if (CaseType.APPEAL_CC.equals(contributionDTO.getCaseType())) {
            appealContributionService.calculateContribution(contributionDTO, laaTransactionId);
        }
        return new CalculateContributionResponse();
    }

    public Contribution createContribs(CreateContributionRequest request, String laaTransactionId) {
        log.info("Inactivate existing Contribution and create a new Contribution");
        return compareContributionService.compareContribution(ContributionDTOBuilder.build(request)) < 2 ?
                maatCourtDataService.createContribution(request, laaTransactionId) : null;
    }

}
