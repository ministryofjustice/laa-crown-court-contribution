package uk.gov.justice.laa.crime.contribution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.contribution.client.HardshipApiClient;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrimeHardshipService {

    private final HardshipApiClient hardshipApiClient;
    private static final String RESPONSE_STRING = "Response from Calculate Hardship API: {}";

    public ApiCalculateHardshipByDetailResponse calculateHardshipForDetail(
            ApiCalculateHardshipByDetailRequest calcHardshipRequest) {
        log.debug("Request to calculate hardship for detail: {}", calcHardshipRequest);
        ApiCalculateHardshipByDetailResponse response =
                hardshipApiClient.calculateHardshipForDetail(calcHardshipRequest);
        log.debug(RESPONSE_STRING, response);
        return response;
    }
}
