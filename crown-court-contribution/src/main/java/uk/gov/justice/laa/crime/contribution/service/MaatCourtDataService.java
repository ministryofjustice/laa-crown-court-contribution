package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.commons.common.Constants;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.model.*;

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

    public Contribution findContribution(Integer repId, String laaTransactionId) {
        Contribution response = maatAPIClient.get(
                Contribution.class,
                configuration.getMaatApi().getContributionEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public Contribution createContribution(CreateContributionRequest request, String laaTransactionId) {
        Contribution response = maatAPIClient.post(
                request,
                Contribution.class,
                configuration.getMaatApi().getContributionEndpoints().getBaseUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public Contribution updateContribution(UpdateContributionRequest request, String laaTransactionId) {
        Contribution response = maatAPIClient.put(
                request,
                Contribution.class,
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

    public CorrespondenceState findCorrespondenceState(Integer repId, String laaTransactionId) {
        CorrespondenceState response = maatAPIClient.get(
                CorrespondenceState.class,
                configuration.getMaatApi().getCorrespondenceStateEndpoints().getFindUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public CorrespondenceState createCorrespondenceState(CorrespondenceState state, String laaTransactionId) {
        CorrespondenceState response = maatAPIClient.post(
                state,
                CorrespondenceState.class,
                configuration.getMaatApi().getCorrespondenceStateEndpoints().getBaseUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public CorrespondenceState updateCorrespondenceState(CorrespondenceState state, String laaTransactionId) {
        CorrespondenceState response = maatAPIClient.put(
                state,
                CorrespondenceState.class,
                configuration.getMaatApi().getCorrespondenceStateEndpoints().getBaseUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }
}
