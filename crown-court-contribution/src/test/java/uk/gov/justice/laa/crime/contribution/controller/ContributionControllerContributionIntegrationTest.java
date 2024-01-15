package uk.gov.justice.laa.crime.contribution.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.justice.laa.crime.contribution.CrownCourtContributionApplication;
import uk.gov.justice.laa.crime.contribution.config.CrownCourtContributionTestConfiguration;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.model.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.common.ApiAssessment;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.enums.CurrentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_IMPLEMENTED;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder.getApiCrownCourtOutcome;
import static uk.gov.justice.laa.crime.contribution.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(CrownCourtContributionTestConfiguration.class)
@SpringBootTest(classes = CrownCourtContributionApplication.class, webEnvironment = DEFINED_PORT)
@AutoConfigureObservability
class ContributionControllerContributionIntegrationTest {

    private MockMvc mvc;
    private static final WireMockServer wiremock = new WireMockServer(9999);
    private static final String ENDPOINT_URL = "/api/internal/v1/contribution/calculate-contribution";
    private static final String GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL = "/api/internal/v1/contribution/summaries/" + TestModelDataBuilder.REP_ID;
    private static final String CHECK_CONTRIBUTION_RULE_ENDPOINT_URL = "/api/internal/v1/contribution/check-contribution-rule";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Value("${services.maat-api.contribution-endpoints.get-rep-order-url}")
    private String getRepOrderUrl;

    @Value("${services.maat-api.contribution-endpoints.summary-url}")
    private String summaryUrl;

    @Value("${services.maat-api.contribution-endpoints.get-appeal-amount-url}")
    private String getAppealAmountUrl;

    @Value("${services.maat-api.contribution-endpoints.find-url}")
    private String findContributionUrl;

    @Value("${services.maat-api.contribution-endpoints.base-url}")
    private String baseContributionUrl;

    @AfterEach
    void after() {
        wiremock.resetAll();
    }

    @AfterAll
    static void clean() {
        wiremock.shutdown();
    }

    @BeforeAll
    void stubForOAuth() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> token = Map.of(
                "expires_in", 3600,
                "token_type", "Bearer",
                "access_token", UUID.randomUUID()
        );

        wiremock.stubFor(
                post("/oauth2/token").willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(mapper.writeValueAsString(token))
                )
        );

        wiremock.start();
    }

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    private void setupAppealStubbing(ApiMaatCalculateContributionRequest appealContributionRequest,
                                     Contribution contribution) throws JsonProcessingException {

        var repOrderUrl = UriComponentsBuilder.fromUriString(getRepOrderUrl)
                .build(appealContributionRequest.getRepId());

        wiremock.stubFor(get(repOrderUrl.getPath())
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getRepOrderDTO()))
                )
        );

        var appealAmountUrl = UriComponentsBuilder.fromUriString(getAppealAmountUrl).build(
                appealContributionRequest.getCaseType(),
                appealContributionRequest.getAppealType().getCode(),
                appealContributionRequest.getLastOutcome().getOutcome().getValue(),
                appealContributionRequest.getAssessments().get(0).getResult().toString()
        );

        wiremock.stubFor(get(appealAmountUrl.getPath())
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(BigDecimal.ZERO))
                )
        );

        var findContributionUrl = UriComponentsBuilder.fromUriString(this.findContributionUrl)
                .build(appealContributionRequest.getRepId());

        wiremock.stubFor(get(urlPathEqualTo(findContributionUrl.getPath()))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(
                                                List.of(contribution)
                                        )
                                )
                )
        );
    }

    @Test
    void givenAEmptyContent_whenCalculateContributionIsInvoked_thenFailsBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCalculateContributionIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenAppealCaseWithSameContributions_whenCalculateContributionIsInvoked_thenOkResponse() throws Exception {
        ApiMaatCalculateContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        setupAppealStubbing(appealContributionRequest,
                Contribution.builder()
                        .upfrontContributions(BigDecimal.ZERO)
                        .build()
        );

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL))
                .andExpect(status().isOk());
    }

    @Test
    void givenAppealCaseWithDiffContributions_whenCalculateContributionIsInvoked_thenOkResponse() throws Exception {
        Contribution newContribution = TestModelDataBuilder.buildContribution();
        newContribution.setUpfrontContributions(BigDecimal.valueOf(500));
        ApiMaatCalculateContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        setupAppealStubbing(appealContributionRequest,
                Contribution.builder()
                        .upfrontContributions(BigDecimal.TEN)
                        .build()
        );

        var createContributionUrl = UriComponentsBuilder.fromUriString(this.baseContributionUrl).build();

        wiremock.stubFor(post(urlPathEqualTo(createContributionUrl.getPath()))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(newContribution))
                )
        );

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequestData_whenCalculateContributionIsInvoked_thenBadRequestResponse() throws Exception {
        ApiMaatCalculateContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        ApiAssessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.withStatus(CurrentStatus.IN_PROGRESS);
        appealContributionRequest.setAssessments(List.of(assessment));
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenMaatApiException_whenCalculateContributionIsInvoked_thenInternalServerErrorResponse() throws Exception {
        ApiMaatCalculateContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        var repOrderUrl = UriComponentsBuilder.fromUriString(getRepOrderUrl)
                .build(appealContributionRequest.getRepId());


        wiremock.stubFor(get(repOrderUrl.getPath())
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(NOT_IMPLEMENTED.code())
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                )
        );

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenMaatApiException_whenGetContributionSummariesIsInvoked_thenInternalServerErrorResponse() throws Exception {
        var summariesUrl = UriComponentsBuilder.fromUriString(summaryUrl)
                .build(TestModelDataBuilder.REP_ID);
        wiremock.stubFor(get(summariesUrl.getPath())
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(NOT_IMPLEMENTED.code())
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                )
        );
        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenApiMaatCalculateContributionRequest_whenGetContributionSummariesIsInvoked_thenOkResponse() throws Exception {
        List<ContributionsSummaryDTO> contributionsSummaryDTOList = List.of(TestModelDataBuilder.getContributionSummaryDTO());
        var summariesUrl = UriComponentsBuilder.fromUriString(summaryUrl)
                .build(TestModelDataBuilder.REP_ID);
        wiremock.stubFor(get(urlPathEqualTo(summariesUrl.getPath()))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(contributionsSummaryDTOList))
                )
        );
        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL))
                .andExpect(status().isOk());
    }


    @Test
    void givenApiMaatCalculateContributionRequest_whenCheckContributionRuleIsInvoked_thenOkResponse() throws Exception {
        ApiMaatCheckContributionRuleRequest apiMaatCheckContributionRuleRequest =
                TestModelDataBuilder.buildCheckContributionRuleRequest();
        String requestData = objectMapper.writeValueAsString(apiMaatCheckContributionRuleRequest);
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, CHECK_CONTRIBUTION_RULE_ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Boolean.TRUE));
    }

    @Test
    void givenApiMaatCalculateContributionRequest_whenCheckContributionRuleIsInvoked_thenFalseIsReturned() throws Exception {
        ApiMaatCheckContributionRuleRequest apiMaatCheckContributionRuleRequest =
                TestModelDataBuilder.buildCheckContributionRuleRequest();
        apiMaatCheckContributionRuleRequest.setCrownCourtOutcome(List.of(getApiCrownCourtOutcome(CrownCourtOutcome.AQUITTED)));
        String requestData = objectMapper.writeValueAsString(apiMaatCheckContributionRuleRequest);
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, CHECK_CONTRIBUTION_RULE_ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Boolean.FALSE));
    }

}