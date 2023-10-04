package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.contribution.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrimeHardshipServiceTest {
    @Mock
    private RestAPIClient hardshipAPIClient;

    @InjectMocks
    private CrimeHardshipService crimeHardshipService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenAValidCalcHardshipForDetailRequest_whenCalculateHardshipForDetailIsInvoked_thenReturnEvidenceFeeResponse() {
        crimeHardshipService.calculateHardshipForDetail(TestModelDataBuilder.getApiCalculateHardshipByDetailRequest());
        verify(hardshipAPIClient).post(any(), any(), any(), any());

    }

    @Test
    void givenAValidCalcHardshipForDetailRequest_whenCalculateHardshipForDetailIsInvokedAndTheApiCallFails_thenFailureIsHandled() {

        when(hardshipAPIClient.post(any(), any(), any(), any()))
                .thenThrow(new APIClientException());

        assertThatThrownBy(() -> crimeHardshipService.calculateHardshipForDetail(
                TestModelDataBuilder.getApiCalculateHardshipByDetailRequest())
        ).isInstanceOf(APIClientException.class);
    }
}
