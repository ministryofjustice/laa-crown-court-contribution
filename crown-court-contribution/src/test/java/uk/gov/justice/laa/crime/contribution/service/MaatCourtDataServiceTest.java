package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    private static final Integer TEST_REP_ID = 1234;
    public static final WebClientResponseException DATA_NOT_FOUND = WebClientResponseException
            .create(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null, null, null);

    @Mock
    private MaatCourtDataApiClient maatCourtDataClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Test
    void givenValidRepId_whenFindContributionIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataClient.find(TEST_REP_ID, true))
                .thenReturn(Mono.empty());
        maatCourtDataService.findContribution(TEST_REP_ID, true);
        verify(maatCourtDataClient, times(1)).find(TEST_REP_ID, true);
    }

    @Test
    void givenNoContributionsAvailable_whenFindContributionIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataClient.find(TEST_REP_ID, true))
                .thenReturn(Mono.error(DATA_NOT_FOUND));
        maatCourtDataService.findContribution(TEST_REP_ID, true);
        verify(maatCourtDataClient, times(1)).find(TEST_REP_ID, true);
    }

    @Test
    void givenValidParams_whenCreateContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.createContribution(new CreateContributionRequest());
        verify(maatCourtDataClient, times(1)).create(any(CreateContributionRequest.class));
    }

    @Test
    void givenValidRepId_whenGetRepOrderByRepIdIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.getRepOrderByRepId(TEST_REP_ID);
        verify(maatCourtDataClient, times(1)).getRepOrderByRepId(TEST_REP_ID);
    }

    @Test
    void givenAValidRepId_whenGetRepOrderCCOutcomeByRepIdIsInvoked_thenReturnOutcome() {
        when(maatCourtDataClient.getRepOrderCCOutcomeByRepId(TEST_REP_ID))
                .thenReturn(Mono.empty());
        maatCourtDataService.getRepOrderCCOutcomeByRepId(TEST_REP_ID);
        verify(maatCourtDataClient, atLeastOnce()).getRepOrderCCOutcomeByRepId(TEST_REP_ID);
    }

    @Test
    void givenNoCCOutcome_whenGetRepOrderCCOutcomeByRepIdIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataClient.getRepOrderCCOutcomeByRepId(TEST_REP_ID))
                .thenReturn(Mono.error(DATA_NOT_FOUND));
        maatCourtDataService.getRepOrderCCOutcomeByRepId(TEST_REP_ID);
        verify(maatCourtDataClient, atLeastOnce()).getRepOrderCCOutcomeByRepId(TEST_REP_ID);
    }

    @Test
    void givenAValidRepId_whenGetContributionsSummaryIsInvoked_thenContributionsSummariesAreReturned() {
        when(maatCourtDataClient.getContributionsSummary(TEST_REP_ID))
                .thenReturn(Mono.empty());
        maatCourtDataService.getContributionsSummary(TEST_REP_ID);
        verify(maatCourtDataClient, times(1)).getContributionsSummary(TEST_REP_ID);
    }

    @Test
    void givenNoContributions_whenGetContributionsSummaryIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataClient.getContributionsSummary(TEST_REP_ID))
                .thenReturn(Mono.error(DATA_NOT_FOUND));
        maatCourtDataService.getContributionsSummary(TEST_REP_ID);
        verify(maatCourtDataClient, times(1)).getContributionsSummary(TEST_REP_ID);
    }

    @Test
    void givenAValidEffectiveDate_whenGetContributionCalcParametersIsInvoked_thenContributionCalcParametersAreReturned() {
        String effectiveDate = TestModelDataBuilder.TEST_DATE.toString();
        when(maatCourtDataClient.getContributionCalcParameters(effectiveDate))
                .thenReturn(Mono.empty());
        maatCourtDataService.getContributionCalcParameters(effectiveDate);
        verify(maatCourtDataClient).getContributionCalcParameters(effectiveDate);
    }

    @Test
    void givenNoData_whenGetContributionCalcParametersIsInvoked_thenResponseIsReturned() {
        String effectiveDate = TestModelDataBuilder.TEST_DATE.toString();
        when(maatCourtDataClient.getContributionCalcParameters(effectiveDate))
                .thenReturn(Mono.error(DATA_NOT_FOUND));
        maatCourtDataService.getContributionCalcParameters(effectiveDate);
        verify(maatCourtDataClient).getContributionCalcParameters(effectiveDate);
    }
}
