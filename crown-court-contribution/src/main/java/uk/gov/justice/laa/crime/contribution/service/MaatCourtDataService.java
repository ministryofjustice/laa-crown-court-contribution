package uk.gov.justice.laa.crime.contribution.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    private final MaatCourtDataApiClient maatAPIClient;
    private static final String SERVICE_NAME = "maatCourtDataService";
    private static final String RESPONSE_STRING = "Response from Court Data API: {}";

    @Retry(name = SERVICE_NAME)
    public List<Contribution> findContribution(Integer repId, Boolean findLatestContribution) {
        log.debug("Request to find contribution for repId: {} findLatestContribution: {} ", repId, findLatestContribution);
        List<Contribution> response = maatAPIClient.find(repId, findLatestContribution)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .block();
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    @Retry(name = SERVICE_NAME)
    public Contribution createContribution(CreateContributionRequest createContributionRequest) {
        log.debug("Request to create contribution: {}", createContributionRequest);
        Contribution response = maatAPIClient.create(createContributionRequest);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    @Retry(name = SERVICE_NAME)
    public RepOrderDTO getRepOrderByRepId(Integer repId) {
        log.info("Request to get rep order for repId: {}", repId);
        var response = maatAPIClient.getRepOrderByRepId(repId);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    @Retry(name = SERVICE_NAME)
    public List<RepOrderCCOutcomeDTO> getRepOrderCCOutcomeByRepId(Integer repId) {
        log.debug("Request to get rep order CC outcome for repId: {}", repId);
        List<RepOrderCCOutcomeDTO> response = maatAPIClient.getRepOrderCCOutcomeByRepId(repId)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .block();
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    @Retry(name = SERVICE_NAME)
    public List<ContributionsSummaryDTO> getContributionsSummary(Integer repId) {
        log.debug("Request to get contributions summary for repId: {}", repId);
        List<ContributionsSummaryDTO> response = maatAPIClient.getContributionsSummary(repId)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .block();
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    @Retry(name = SERVICE_NAME)
    public ContributionCalcParametersDTO getContributionCalcParameters(String effectiveDate) {
        log.debug("Request to get contribution calc parameters for effectiveDate: {}", effectiveDate);
        ContributionCalcParametersDTO response = maatAPIClient.getContributionCalcParameters(effectiveDate)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .block();
        log.debug(RESPONSE_STRING, response);
        return response;
    }
}
