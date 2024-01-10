package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiCalculateHardshipByDetailResponse;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrimeHardshipService {

    @Qualifier("hardshipApiClient")
    private final RestAPIClient hardshipApiClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from Calculate Hardship API: {}";

    public ApiCalculateHardshipByDetailResponse calculateHardshipForDetail(ApiCalculateHardshipByDetailRequest calcHardshipRequest) {
        ApiCalculateHardshipByDetailResponse response = hardshipApiClient.post(
                calcHardshipRequest,
                new ParameterizedTypeReference<>() {
                },
                configuration.getHardshipApi().getHardshipEndpoints().getCalculateHardshipForDetailUrl(),
                Map.of()
        );
        log.info(RESPONSE_STRING, response);
        return response;
    }
}
