package uk.gov.justice.laa.crime.contribution.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.contribution.client.HardshipApiClient;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ExtendWith(MockitoExtension.class)
class CrimeHardshipServiceTest {
    @Mock
    private HardshipApiClient hardshipAPIClient;

    @InjectMocks
    private CrimeHardshipService crimeHardshipService;

    @Test
    void
            givenAValidCalcHardshipForDetailRequest_whenCalculateHardshipForDetailIsInvoked_thenReturnEvidenceFeeResponse() {
        ApiCalculateHardshipByDetailRequest request = TestModelDataBuilder.getApiCalculateHardshipByDetailRequest();
        ApiCalculateHardshipByDetailResponse response =
                new ApiCalculateHardshipByDetailResponse().withHardshipSummary(BigDecimal.ONE);
        when(hardshipAPIClient.calculateHardshipForDetail(request)).thenReturn(response);
        assertThat(crimeHardshipService.calculateHardshipForDetail(request)).isEqualTo(response);
    }

    @Test
    void
            givenAValidCalcHardshipForDetailRequest_whenCalculateHardshipForDetailIsInvokedAndTheApiCallFails_thenFailureIsHandled() {

        when(hardshipAPIClient.calculateHardshipForDetail(any())).thenThrow(WebClientResponseException.class);

        assertThatThrownBy(() -> crimeHardshipService.calculateHardshipForDetail(
                        TestModelDataBuilder.getApiCalculateHardshipByDetailRequest()))
                .isInstanceOf(WebClientResponseException.class);
    }
}
