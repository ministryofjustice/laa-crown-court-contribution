package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.contribution.client.HardshipApiClient;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrimeHardshipServiceTest {
    @Mock
    private HardshipApiClient hardshipAPIClient;

    @InjectMocks
    private CrimeHardshipService crimeHardshipService;

    @Test
    void givenAValidCalcHardshipForDetailRequest_whenCalculateHardshipForDetailIsInvoked_thenReturnEvidenceFeeResponse() {
        crimeHardshipService.calculateHardshipForDetail(TestModelDataBuilder.getApiCalculateHardshipByDetailRequest());
        verify(hardshipAPIClient, times(1)).calculateHardshipForDetail(any());

    }

    @Test
    void givenAValidCalcHardshipForDetailRequest_whenCalculateHardshipForDetailIsInvokedAndTheApiCallFails_thenFailureIsHandled() {

        when(hardshipAPIClient.calculateHardshipForDetail(any()))
                .thenThrow(WebClientResponseException.class);

        assertThatThrownBy(() -> crimeHardshipService.calculateHardshipForDetail(
                TestModelDataBuilder.getApiCalculateHardshipByDetailRequest())
        ).isInstanceOf(WebClientResponseException.class);
    }
}
