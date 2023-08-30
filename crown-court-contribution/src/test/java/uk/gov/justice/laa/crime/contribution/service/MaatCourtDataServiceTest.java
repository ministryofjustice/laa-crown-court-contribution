package uk.gov.justice.laa.crime.contribution.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.commons.common.Constants;
import org.springframework.core.ParameterizedTypeReference;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.contribution.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentResult;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AppealType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CrownCourtAppealOutcome;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

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
        maatCourtDataService.findContribution(TEST_REP_ID, LAA_TRANSACTION_ID, true);
        verify(maatCourtDataClient).get(any(), anyString(), anyMap(), any(), anyInt());
    }

    @Test
    void givenValidParams_whenCreateContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.createContribution(new CreateContributionRequest(), LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).post(
                any(CreateContributionRequest.class), eq(new ParameterizedTypeReference<Contribution>(){}), anyString(), anyMap()
        );
    }

    @Test
    void givenValidParams_whenUpdateContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.updateContribution(new UpdateContributionRequest(), LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).put(
                any(UpdateContributionRequest.class), eq(new ParameterizedTypeReference<Contribution>(){}), anyString(), anyMap()
        );
    }

    @Test
    void givenValidParams_whenGetContributionAppealAmountIsInvoked_thenResponseIsReturned() {
        GetContributionAmountRequest expected = new GetContributionAmountRequest()
                .withCaseType(CaseType.APPEAL_CC)
                .withAppealType(AppealType.ACN)
                .withOutcome(CrownCourtAppealOutcome.SUCCESSFUL)
                .withAssessmentResult(AssessmentResult.PASS);

        maatCourtDataService.getContributionAppealAmount(expected, LAA_TRANSACTION_ID);

        verify(maatCourtDataClient).get(
                eq(new ParameterizedTypeReference<BigDecimal>(){}),
                anyString(),
                anyMap(),
                any(CaseType.class),
                any(AppealType.class),
                any(CrownCourtAppealOutcome.class),
                any(AssessmentResult.class)
        );
    }

    @Test
    void givenValidRepId_whenFindCorrespondenceStateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.findCorrespondenceState(TEST_REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<CorrespondenceState>(){}), anyString(), anyMap(), anyInt());
    }

    @Test
    void givenValidParams_whenCreateCorrespondenceStateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.createCorrespondenceState(new CorrespondenceState(), LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).post(
                any(CorrespondenceState.class), eq(new ParameterizedTypeReference<CorrespondenceState>(){}), anyString(), anyMap()
        );
    }

    @Test
    void givenValidRepId_whenContributionCountIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.getContributionCount(TEST_REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).head(configuration.getMaatApi().getContributionEndpoints().getGetContributionCountUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, LAA_TRANSACTION_ID), TEST_REP_ID);
    }

    @Test
    void givenValidRepId_whengetRepOrderByRepIdIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.getRepOrderByRepId(TEST_REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<RepOrderDTO>(){}),anyString(), anyMap(), anyInt());
    }

    @Test
    void givenAValidRepId_whenGetRepOrderCCOutcomeByRepIdIsInvoked_thenReturnOutcome() {
        maatCourtDataService.getRepOrderCCOutcomeByRepId(TEST_REP_ID, LAA_TRANSACTION_ID);
        verify(maatCourtDataClient, atLeastOnce()).get(eq(new ParameterizedTypeReference<List<RepOrderCCOutcomeDTO>>() {}),
                anyString(), anyMap(), any());
    }

    @Test
    void givenAValidRepId_whenGetContributionsSummaryIsInvoked_thenContributionsSummariesAreReturned() {
        maatCourtDataService.getContributionsSummary(TEST_REP_ID, LAA_TRANSACTION_ID);

        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<List<ContributionsSummaryDTO>>() {}),
                anyString(), anyMap(), anyInt());
    }

    @Test
    void givenAValidEffectiveDate_whenGetContributionCalcParametersIsInvoked_thenContributionCalcParametersAreReturned() {
        maatCourtDataService.getContributionCalcParameters(TestModelDataBuilder.TEST_DATE.toString(), LAA_TRANSACTION_ID);

        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<ContributionCalcParametersDTO>() {}),
                anyString(), anyMap(), anyString());
    }
}
