package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    @Qualifier("maatApiClient")
    private final RestAPIClient maatAPIClient;
    private final ServicesConfiguration configuration;

    private static final String RESPONSE_STRING = "Response from Court Data API: {}";

    public List<Contribution> findContribution(Integer repId, Boolean findLatestContribution) {
        log.debug("Request to find contribution for repId: {} findLatestContribution: {} ", repId, findLatestContribution);
        List<Contribution> response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getFindUrl() + "?findLatestContribution=" + findLatestContribution,
                repId
        );
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public Contribution createContribution(CreateContributionRequest createContributionRequest) {
        log.debug("Request to create contribution: {}", createContributionRequest);
        Contribution response = maatAPIClient.post(
                createContributionRequest,
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getBaseUrl(),
                Map.of()
        );
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public RepOrderDTO getRepOrderByRepId(Integer repId) {
        log.info("Request to get rep order for repId: {}", repId);
        var response = maatAPIClient.get(
                new ParameterizedTypeReference<RepOrderDTO>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getGetRepOrderUrl(),
                repId
        );
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public List<RepOrderCCOutcomeDTO> getRepOrderCCOutcomeByRepId(Integer repId) {
        log.debug("Request to get rep order CC outcome for repId: {}", repId);
        List<RepOrderCCOutcomeDTO> response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getRepOrderEndpoints().getFindOutcomeUrl(),
                repId
        );
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public List<ContributionsSummaryDTO> getContributionsSummary(Integer repId) {
        log.debug("Request to get contributions summary for repId: {}", repId);
        List<ContributionsSummaryDTO> response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getSummaryUrl(),
                repId
        );
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public ContributionCalcParametersDTO getContributionCalcParameters(String effectiveDate) {
        log.debug("Request to get contribution calc parameters for effectiveDate: {}", effectiveDate);
        ContributionCalcParametersDTO response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getContribsParametersUrl(),
                effectiveDate
        );
        log.debug(RESPONSE_STRING, response);
        return response;
    }
}
