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
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        List<Contribution> expected = List.of(TestModelDataBuilder.getContribution());
        when(maatCourtDataClient.find(TEST_REP_ID, true))
                .thenReturn(Mono.just(expected));
        List<Contribution> actual = maatCourtDataService.findContribution(TEST_REP_ID, true);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenNoContributionsAvailable_whenFindContributionIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataClient.find(TEST_REP_ID, true))
                .thenReturn(Mono.error(DATA_NOT_FOUND));
        var response = maatCourtDataService.findContribution(TEST_REP_ID, true);
        assertThat(response).isNull();
    }

    @Test
    void givenValidParams_whenCreateContributionIsInvoked_thenResponseIsReturned() {
        Contribution expected = TestModelDataBuilder.getContribution();
        CreateContributionRequest createContributionRequest = new CreateContributionRequest();
        when(maatCourtDataClient.create(createContributionRequest))
                .thenReturn(expected);
        Contribution actual = maatCourtDataService.createContribution(createContributionRequest);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenValidRepId_whenGetRepOrderByRepIdIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataClient.getRepOrderByRepId(TEST_REP_ID))
                .thenReturn(TestModelDataBuilder.getRepOrderDTO());
        RepOrderDTO repOrderDTO = maatCourtDataService.getRepOrderByRepId(TEST_REP_ID);
        assertThat(repOrderDTO).isEqualTo(TestModelDataBuilder.getRepOrderDTO());
    }

    @Test
    void givenAValidRepId_whenGetRepOrderCCOutcomeByRepIdIsInvoked_thenReturnOutcome() {
        List<RepOrderCCOutcomeDTO> expected = List.of(TestModelDataBuilder
                .getRepOrderCCOutcomeDTO(1, CrownCourtOutcome.ABANDONED.getCode()));
        when(maatCourtDataClient.getRepOrderCCOutcomeByRepId(TEST_REP_ID))
                .thenReturn(Mono.just(expected));
        List<RepOrderCCOutcomeDTO> actual = maatCourtDataService.getRepOrderCCOutcomeByRepId(TEST_REP_ID);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenNoCCOutcome_whenGetRepOrderCCOutcomeByRepIdIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataClient.getRepOrderCCOutcomeByRepId(TEST_REP_ID))
                .thenReturn(Mono.error(DATA_NOT_FOUND));
        var response = maatCourtDataService.getRepOrderCCOutcomeByRepId(TEST_REP_ID);
        assertThat(response).isNull();
    }

    @Test
    void givenAValidRepId_whenGetContributionsSummaryIsInvoked_thenContributionsSummariesAreReturned() {
        List<ContributionsSummaryDTO> expected = List.of(TestModelDataBuilder.getContributionSummaryDTO());
        when(maatCourtDataClient.getContributionsSummary(TEST_REP_ID))
                .thenReturn(Mono.just(expected));
        List<ContributionsSummaryDTO> actual = maatCourtDataService.getContributionsSummary(TEST_REP_ID);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenNoContributions_whenGetContributionsSummaryIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataClient.getContributionsSummary(TEST_REP_ID))
                .thenReturn(Mono.error(DATA_NOT_FOUND));
        var response = maatCourtDataService.getContributionsSummary(TEST_REP_ID);
        assertThat(response).isNull();
    }

    @Test
    void givenAValidEffectiveDate_whenGetContributionCalcParametersIsInvoked_thenContributionCalcParametersAreReturned() {
        String effectiveDate = TestModelDataBuilder.TEST_DATE.toString();
        when(maatCourtDataClient.getContributionCalcParameters(effectiveDate))
                .thenReturn(Mono.just(TestModelDataBuilder.getContributionCalcParametersDTO()));
        ContributionCalcParametersDTO actual = maatCourtDataService.getContributionCalcParameters(effectiveDate);
        assertThat(actual).isEqualTo(TestModelDataBuilder.getContributionCalcParametersDTO());
    }

    @Test
    void givenNoData_whenGetContributionCalcParametersIsInvoked_thenResponseIsReturned() {
        String effectiveDate = TestModelDataBuilder.TEST_DATE.toString();
        when(maatCourtDataClient.getContributionCalcParameters(effectiveDate))
                .thenReturn(Mono.error(DATA_NOT_FOUND));
        ContributionCalcParametersDTO response = maatCourtDataService.getContributionCalcParameters(effectiveDate);
        assertThat(response).isNull();
    }
}
