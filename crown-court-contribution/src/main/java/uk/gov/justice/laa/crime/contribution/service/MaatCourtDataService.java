package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.commons.common.Constants;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.model.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.contribution.model.UpdateContributionRequest;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    @Qualifier("maatApiClient")
    private final RestAPIClient maatAPIClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from Court Data API: {}";

    public ContributionDTO findContribution(Integer repId, String laaTransactionId) {
        ContributionDTO response = maatAPIClient.get(
                ContributionDTO.class,
                configuration.getMaatApi().getContributionEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public ContributionDTO createContribution(CreateContributionRequest request, String laaTransactionId) {
        ContributionDTO response = maatAPIClient.post(
                request,
                ContributionDTO.class,
                configuration.getMaatApi().getContributionEndpoints().getBaseUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public ContributionDTO updateContribution(UpdateContributionRequest request, String laaTransactionId) {
        ContributionDTO response = maatAPIClient.put(
                request,
                ContributionDTO.class,
                configuration.getMaatApi().getContributionEndpoints().getBaseUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public BigDecimal getContributionAppealAmount(GetContributionAmountRequest request, String laaTransactionId) {
        BigDecimal response = maatAPIClient.get(
                BigDecimal.class,
                configuration.getMaatApi().getContributionEndpoints().getGetAppealAmountUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
               request.getCaseType(), request.getAppealType(), request.getOutcome(), request.getAssessmentResult()
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }
}
