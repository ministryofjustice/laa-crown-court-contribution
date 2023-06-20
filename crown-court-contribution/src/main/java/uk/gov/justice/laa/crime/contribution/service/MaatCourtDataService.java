package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.commons.common.Constants;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    private static final String RESPONSE_STRING = "Response from Court Data API: {}";
    @Qualifier("maatApiClient")
    private final RestAPIClient maatAPIClient;
    private final ServicesConfiguration configuration;

    public Contribution findContribution(Integer repId, String laaTransactionId) {
        Contribution response = maatAPIClient.get(
                new ParameterizedTypeReference<Contribution>() {},
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
                new ParameterizedTypeReference<Contribution>() {},
                configuration.getMaatApi().getContributionEndpoints().getBaseUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public Contribution updateContribution(UpdateContributionRequest request, String laaTransactionId) {
        Contribution response = maatAPIClient.put(
                request,
                new ParameterizedTypeReference<Contribution>() {},
                configuration.getMaatApi().getContributionEndpoints().getBaseUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public BigDecimal getContributionAppealAmount(GetContributionAmountRequest request, String laaTransactionId) {
        BigDecimal response = maatAPIClient.get(
                new ParameterizedTypeReference<BigDecimal>() {},
                configuration.getMaatApi().getContributionEndpoints().getGetAppealAmountUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                request.getCaseType(), request.getAppealType(), request.getOutcome(), request.getAssessmentResult()
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public CorrespondenceState findCorrespondenceState(Integer repId, String laaTransactionId) {
        CorrespondenceState response = maatAPIClient.get(
                new ParameterizedTypeReference<CorrespondenceState>() {},
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
                new ParameterizedTypeReference<CorrespondenceState>() {},
                configuration.getMaatApi().getCorrespondenceStateEndpoints().getBaseUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public CorrespondenceState updateCorrespondenceState(CorrespondenceState state, String laaTransactionId) {
        CorrespondenceState response = maatAPIClient.put(
                state,
                new ParameterizedTypeReference<CorrespondenceState>() {},
                configuration.getMaatApi().getCorrespondenceStateEndpoints().getBaseUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId)
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public long getContributionCount(Integer repId, String laaTransactionId) {
        var response = maatAPIClient.head(
                configuration.getMaatApi().getContributionEndpoints().getGetContributionCountUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(RESPONSE_STRING, response);
        if (response != null) {
            return response.getHeaders().getContentLength();
        }
        return 0L;
    }

    public RepOrderDTO getRepOrderByRepId(Integer repId, String laaTransactionId) {
        var response = maatAPIClient.get(
                new ParameterizedTypeReference<RepOrderDTO>() {},
                configuration.getMaatApi().getContributionEndpoints().getGetRepOrderUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public List<RepOrderCCOutcomeDTO> getRepOrderCCOutcomeByRepId(Integer repId, String laaTransactionId) {
        List<RepOrderCCOutcomeDTO> response = maatAPIClient.get(
                new ParameterizedTypeReference<List<RepOrderCCOutcomeDTO>>() {},
                configuration.getMaatApi().getRepOrderEndpoints().getFindOutcomeUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }


}
