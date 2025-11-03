package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;

import java.util.List;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    private final MaatCourtDataApiClient maatAPIClient;
    private static final String RESPONSE_STRING = "Response from Court Data API: {}";

    public List<Contribution> findContribution(Integer repId, Boolean findLatestContribution) {
        log.debug(
                "Request to find contribution for repId: {} findLatestContribution: {} ",
                repId,
                findLatestContribution);
        List<Contribution> response = maatAPIClient.find(repId, findLatestContribution);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public Contribution createContribution(CreateContributionRequest createContributionRequest) {
        log.debug("Request to create contribution: {}", createContributionRequest);
        Contribution response = maatAPIClient.create(createContributionRequest);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public RepOrderDTO getRepOrderByRepId(Integer repId) {
        log.info("Request to get rep order for repId: {}", repId);
        var response = maatAPIClient.getRepOrderByRepId(repId);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public List<RepOrderCCOutcomeDTO> getRepOrderCCOutcomeByRepId(Integer repId) {
        log.debug("Request to get rep order CC outcome for repId: {}", repId);
        List<RepOrderCCOutcomeDTO> response = maatAPIClient.getRepOrderCCOutcomeByRepId(repId);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public List<ContributionsSummaryDTO> getContributionsSummary(Integer repId) {
        log.debug("Request to get contributions summary for repId: {}", repId);
        List<ContributionsSummaryDTO> response = maatAPIClient.getContributionsSummary(repId);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public ContributionCalcParametersDTO getContributionCalcParameters(String effectiveDate) {
        log.debug("Request to get contribution calc parameters for effectiveDate: {}", effectiveDate);
        ContributionCalcParametersDTO response = maatAPIClient.getContributionCalcParameters(effectiveDate);
        log.debug(RESPONSE_STRING, response);
        return response;
    }
}
