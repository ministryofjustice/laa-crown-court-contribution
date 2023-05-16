package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.contribution.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.model.*;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    private static final Integer TEST_REP_ID = 1234;
    private static final String LAA_TRANSACTION_ID = "laaTransactionId";


    @Mock
    private RestAPIClient maatCourtDataClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(9990);

    @Test
    void givenValidRepId_whenFindContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.findContribution(TEST_REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).get(eq(Contribution.class), anyString(), anyMap(), anyInt());
    }

    @Test
    void givenValidParams_whenCreateContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.createContribution(new CreateContributionRequest(), LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).post(
                any(CreateContributionRequest.class), eq(Contribution.class), anyString(), anyMap()
        );
    }

    @Test
    void givenValidParams_whenUpdateContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.updateContribution(new UpdateContributionRequest(), LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).put(
                any(UpdateContributionRequest.class), eq(Contribution.class), anyString(), anyMap()
        );
    }

    @Test
    void givenValidParams_whenGetContributionAppealAmountIsInvoked_thenResponseIsReturned() {
        GetContributionAmountRequest expected = new GetContributionAmountRequest()
                .withCaseType("APPEAL CC")
                .withAppealType("ACN")
                .withOutcome("SUCCESSFUL")
                .withAssessmentResult("PASS");
        maatCourtDataService.getContributionAppealAmount(expected, LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).get(
                eq(BigDecimal.class), anyString(), anyMap(), anyString(), anyString(), anyString(), anyString()
        );
    }

    @Test
    void givenValidRepId_whenFindCorrespondenceStateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.findCorrespondenceState(TEST_REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).get(eq(CorrespondenceState.class), anyString(), anyMap(), anyInt());
    }

    @Test
    void givenValidParams_whenCreateCorrespondenceStateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.createCorrespondenceState(new CorrespondenceState(), LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).post(
                any(CorrespondenceState.class), eq(CorrespondenceState.class), anyString(), anyMap()
        );
    }

    @Test
    void givenValidParams_whenUpdateCorrespondenceStateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.updateCorrespondenceState(new CorrespondenceState(), LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).put(
                any(CorrespondenceState.class), eq(CorrespondenceState.class), anyString(), anyMap()
        );
    }
}
