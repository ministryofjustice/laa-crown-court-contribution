package uk.gov.justice.laa.crime.contribution.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    private static final Integer TEST_REP_ID = 1234;

    @Mock
    private MaatCourtDataApiClient maatCourtDataClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Test
    void givenValidRepId_whenFindContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.findContribution(TEST_REP_ID, true);
        verify(maatCourtDataClient).find(TEST_REP_ID, true);
    }

    @Test
    void givenValidRepIdAndNoResults_whenFindContributionIsInvoked_thenEmptyListIsReturned() {
        when(maatCourtDataClient.find(TEST_REP_ID, true)).thenReturn(null);
        List<Contribution> result = maatCourtDataService.findContribution(TEST_REP_ID, true);
        assertThat(result).isEmpty();
    }

    @Test
    void givenValidParams_whenCreateContributionIsInvoked_thenResponseIsReturned() {
        CreateContributionRequest createContributionRequest = new CreateContributionRequest();
        maatCourtDataService.createContribution(createContributionRequest);
        verify(maatCourtDataClient).create(createContributionRequest);
    }

    @Test
    void givenValidRepId_whenGetRepOrderByRepIdIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.getRepOrderByRepId(TEST_REP_ID);
        verify(maatCourtDataClient).getRepOrderByRepId(TEST_REP_ID);
    }

    @Test
    void givenValidRepId_whenGetRepOrderCCOutcomeByRepIdIsInvoked_thenReturnOutcome() {
        maatCourtDataService.getRepOrderCCOutcomeByRepId(TEST_REP_ID);
        verify(maatCourtDataClient).getRepOrderCCOutcomeByRepId(TEST_REP_ID);
    }

    @Test
    void givenValidRepIdAndNoResults_whenGetRepOrderCCOutcomeByRepIdIsInvoked_thenEmptyListIsReturned() {
        when(maatCourtDataClient.getRepOrderCCOutcomeByRepId(TEST_REP_ID)).thenReturn(null);
        List<RepOrderCCOutcomeDTO> result = maatCourtDataService.getRepOrderCCOutcomeByRepId(
                TEST_REP_ID);
        assertThat(result).isEmpty();
    }

    @Test
    void givenValidRepId_whenGetContributionsSummaryIsInvoked_thenContributionsSummariesAreReturned() {
        maatCourtDataService.getContributionsSummary(TEST_REP_ID);
        verify(maatCourtDataClient).getContributionsSummary(TEST_REP_ID);
    }

    @Test
    void givenValidRepIdAndNoResults_whenGetContributionsSummaryIsInvoked_thenEmptyListIsReturned() {
        when(maatCourtDataClient.getContributionsSummary(TEST_REP_ID)).thenReturn(null);
        List<ContributionsSummaryDTO> result = maatCourtDataService.getContributionsSummary(
                TEST_REP_ID);
        assertThat(result).isEmpty();
    }

    @Test
    void givenValidEffectiveDate_whenGetContributionCalcParametersIsInvoked_thenReturnsResult() {
        String effectiveDate = TestModelDataBuilder.TEST_DATE.toString();
        maatCourtDataService.getContributionCalcParameters(effectiveDate);
        verify(maatCourtDataClient).getContributionCalcParameters(effectiveDate);
    }
}
