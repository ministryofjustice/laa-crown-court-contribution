package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.contribution.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    private static final Integer TEST_REP_ID = 1234;

    @Mock
    private RestAPIClient maatCourtDataClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(9990);

    @Test
    void givenValidRepId_whenFindContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.findContribution(TEST_REP_ID, true);
        verify(maatCourtDataClient).get(new ParameterizedTypeReference<List<Contribution>>() {
        }, "/contributions/{repId}?findLatestContribution=true", 1234);
    }

    @Test
    void givenValidParams_whenCreateContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.createContribution(new CreateContributionRequest());
        verify(maatCourtDataClient).post(
                any(CreateContributionRequest.class), eq(new ParameterizedTypeReference<Contribution>() {
                }), anyString(), anyMap()
        );
    }

    @Test
    void givenValidRepId_whenGetRepOrderByRepIdIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.getRepOrderByRepId(TEST_REP_ID);
        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<RepOrderDTO>() {
        }), anyString(), anyInt());
    }

    @Test
    void givenAValidRepId_whenGetRepOrderCCOutcomeByRepIdIsInvoked_thenReturnOutcome() {
        maatCourtDataService.getRepOrderCCOutcomeByRepId(TEST_REP_ID);
        verify(maatCourtDataClient, atLeastOnce()).get(eq(new ParameterizedTypeReference<List<RepOrderCCOutcomeDTO>>() {
                }),
                anyString(), any()
        );
    }

    @Test
    void givenAValidRepId_whenGetContributionsSummaryIsInvoked_thenContributionsSummariesAreReturned() {
        maatCourtDataService.getContributionsSummary(TEST_REP_ID);

        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<List<ContributionsSummaryDTO>>() {
                }),
                anyString(), anyInt()
        );
    }

    @Test
    void givenAValidEffectiveDate_whenGetContributionCalcParametersIsInvoked_thenContributionCalcParametersAreReturned() {
        maatCourtDataService.getContributionCalcParameters(TestModelDataBuilder.TEST_DATE.toString());

        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<ContributionCalcParametersDTO>() {
                }),
                anyString(), anyString()
        );
    }
}
