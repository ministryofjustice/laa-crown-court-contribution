package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.contribution.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.CorrespondenceState;
import uk.gov.justice.laa.crime.contribution.model.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.GetContributionAmountRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.UpdateContributionRequest;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AppealType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CrownCourtAppealOutcome;

import java.math.BigDecimal;
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

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("findLatestContribution", "true");
        maatCourtDataService.findContribution(TEST_REP_ID, true);
        verify(maatCourtDataClient).get(new ParameterizedTypeReference<List<Contribution>>() {
        }, "/contributions/{repId}", queryParams, 1234);
    }

    @Test
    void givenValidRepId_whenFindLatestSentContributionContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.findLatestSentContribution(TEST_REP_ID);
        verify(maatCourtDataClient).get(any(), anyString(), anyInt());
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
    void givenValidParams_whenUpdateContributionIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.updateContribution(new UpdateContributionRequest());
        verify(maatCourtDataClient).put(
                any(UpdateContributionRequest.class), eq(new ParameterizedTypeReference<Contribution>() {
                }), anyString(), anyMap()
        );
    }

    @Test
    void givenValidParams_whenGetContributionAppealAmountIsInvoked_thenResponseIsReturned() {
        GetContributionAmountRequest expected = new GetContributionAmountRequest()
                .withCaseType(CaseType.APPEAL_CC)
                .withAppealType(AppealType.ACN)
                .withOutcome(CrownCourtAppealOutcome.SUCCESSFUL)
                .withAssessmentResult(AssessmentResult.PASS);

        maatCourtDataService.getContributionAppealAmount(expected);

        verify(maatCourtDataClient).get(
                eq(new ParameterizedTypeReference<BigDecimal>() {
                }),
                anyString(),
                any(CaseType.class),
                any(AppealType.class),
                any(CrownCourtAppealOutcome.class),
                any(AssessmentResult.class)
        );
    }

    @Test
    void givenValidRepId_whenFindCorrespondenceStateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.findCorrespondenceState(TEST_REP_ID);
        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<CorrespondenceState>() {
        }), anyString(), anyInt());
    }

    @Test
    void givenValidParams_whenCreateCorrespondenceStateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.createCorrespondenceState(new CorrespondenceState());
        verify(maatCourtDataClient).post(
                any(CorrespondenceState.class), eq(new ParameterizedTypeReference<CorrespondenceState>() {
                }), anyString(), anyMap()
        );
    }

    @Test
    void givenValidParams_whenUpdateCorrespondenceStateIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.updateCorrespondenceState(new CorrespondenceState());
        verify(maatCourtDataClient).put(
                any(CorrespondenceState.class),
                eq(new ParameterizedTypeReference<CorrespondenceState>() {}),
                anyString(),
                anyMap()
        );
    }

    @Test
    void givenValidRepId_whengetRepOrderByRepIdIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.getRepOrderByRepId(TEST_REP_ID);
        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<RepOrderDTO>() {
        }), anyString(), anyInt());
    }

    @Test
    void givenAValidRepId_whenGetRepOrderCCOutcomeByRepIdIsInvoked_thenReturnOutcome() {
        maatCourtDataService.getRepOrderCCOutcomeByRepId(TEST_REP_ID);
        verify(maatCourtDataClient, atLeastOnce()).get(eq(new ParameterizedTypeReference<List<RepOrderCCOutcomeDTO>>() {
                }),
                anyString(), any());
    }

    @Test
    void givenAValidRepId_whenGetContributionsSummaryIsInvoked_thenContributionsSummariesAreReturned() {
        maatCourtDataService.getContributionsSummary(TEST_REP_ID);

        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<List<ContributionsSummaryDTO>>() {
                }),
                anyString(), anyInt());
    }

    @Test
    void givenAValidEffectiveDate_whenGetContributionCalcParametersIsInvoked_thenContributionCalcParametersAreReturned() {
        maatCourtDataService.getContributionCalcParameters(TestModelDataBuilder.TEST_DATE.toString());

        verify(maatCourtDataClient).get(eq(new ParameterizedTypeReference<ContributionCalcParametersDTO>() {
                }),
                anyString(), anyString());
    }
}
