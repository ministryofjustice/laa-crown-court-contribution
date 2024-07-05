package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.CorrespondenceState;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.UpdateContributionRequest;

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

    public List<Contribution> findContribution(Integer repId, Boolean findLatestContribution) {
        List<Contribution> response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getFindUrl() + "?findLatestContribution=" + findLatestContribution,
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public Contribution createContribution(CreateContributionRequest createContributionRequest) {
        Contribution response = maatAPIClient.post(
                createContributionRequest,
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getBaseUrl(),
                Map.of()
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public Contribution updateContribution(UpdateContributionRequest request) {
        Contribution response = maatAPIClient.put(
                request,
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getBaseUrl(),
                Map.of()
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public BigDecimal getContributionAppealAmount(GetContributionAmountRequest request) {
        BigDecimal response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getGetAppealAmountUrl(),
                request.getCaseType(), request.getAppealType(), request.getOutcome(), request.getAssessmentResult()
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public CorrespondenceState findCorrespondenceState(Integer repId) {
        CorrespondenceState response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getCorrespondenceStateEndpoints().getFindUrl(),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public CorrespondenceState createCorrespondenceState(CorrespondenceState state) {
        CorrespondenceState response = maatAPIClient.post(
                state,
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getCorrespondenceStateEndpoints().getBaseUrl(),
                Map.of()
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public CorrespondenceState updateCorrespondenceState(CorrespondenceState state) {
        CorrespondenceState response = maatAPIClient.put(
                state,
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getCorrespondenceStateEndpoints().getBaseUrl(),
                Map.of()
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public RepOrderDTO getRepOrderByRepId(Integer repId) {
        var response = maatAPIClient.get(
                new ParameterizedTypeReference<RepOrderDTO>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getGetRepOrderUrl(),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public List<RepOrderCCOutcomeDTO> getRepOrderCCOutcomeByRepId(Integer repId) {
        List<RepOrderCCOutcomeDTO> response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getRepOrderEndpoints().getFindOutcomeUrl(),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public List<ContributionsSummaryDTO> getContributionsSummary(Integer repId) {
        List<ContributionsSummaryDTO> response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getSummaryUrl(),
                repId
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }

    public ContributionCalcParametersDTO getContributionCalcParameters(String effectiveDate) {
        ContributionCalcParametersDTO response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getContributionEndpoints().getContribsParametersUrl(),
                effectiveDate
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }
}
