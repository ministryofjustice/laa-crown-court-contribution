package uk.gov.justice.laa.crime.contribution.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.Assessment;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AppealType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CrownCourtOutcome;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_IMPLEMENTED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.contribution.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(CrownCourtContributionTestConfiguration.class)
@SpringBootTest(classes = CrownCourtContributionApplication.class, webEnvironment = DEFINED_PORT)
class CrownCourtContributionIntegrationTest {

    private MockMvc mvc;
    private static final WireMockServer wiremock = new WireMockServer(9999);
    private static final String ENDPOINT_URL = "/api/internal/v1/contribution/appeal";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Value("${services.maat-api.contribution-endpoints.get-rep-order-url}")
    private String getRepOrderUrl;

    @Value("${services.maat-api.contribution-endpoints.get-appeal-amount-url}")
    private String getAppealAmountUrl;

    @Value("${services.maat-api.contribution-endpoints.find-url}")
    private String getContributionUrl;


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

    @Test
    void givenAEmptyContent_whenCalculateContributionIsInvoked_thenFailsBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCalculateContributionIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenAppealCaseWithSameContributions_whenCalculateContributionIsInvoked_thenOkResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

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

        var contributionUrl = UriComponentsBuilder.fromUriString(getContributionUrl)
                .build(appealContributionRequest.getRepId());

        wiremock.stubFor(get(urlPathEqualTo(contributionUrl.getPath()))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(
                                                List.of(
                                                        Contribution.builder()
                                                                .upfrontContributions(BigDecimal.ZERO)
                                                                .build()
                                                )
                                        )
                                )
                )
        );

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL))
                .andExpect(status().isOk());
    }

//    @Test
//    void givenContributionsNeedUpdating_whenCalculateAppealContributionIsInvoked_thenOkResponse() throws Exception {
//        Contribution newContribution = TestModelDataBuilder.buildContribution();
//        newContribution.setUpfrontContributions(BigDecimal.valueOf(500));
//        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest().withCaseType(CaseType.APPEAL_CC);
//        String requestData = objectMapper.writeValueAsString(appealContributionRequest);
//
//        mockMaatApi.enqueue(new MockResponse()
//                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
//                .setResponseCode(OK.code())
//                .setBody(objectMapper.writeValueAsString(TestModelDataBuilder.getRepOrderDTO())));
//
//        mockMaatApi.enqueue(new MockResponse()
//                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
//                .setResponseCode(OK.code())
//                .setBody(objectMapper.writeValueAsString(BigDecimal.valueOf(500))));
//        mockMaatApi.enqueue(new MockResponse()
//                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
//                .setResponseCode(OK.code())
//                .setBody(objectMapper.writeValueAsString(List.of(TestModelDataBuilder.buildContribution()))));
//        mockMaatApi.enqueue(new MockResponse()
//                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
//                .setResponseCode(OK.code())
//                .setBody(objectMapper.writeValueAsString(newContribution)));
//
//        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL))
//                .andExpect(status().isOk());
//    }

    @Test
    void givenInvalidRequestData_whenCalculateContributionIsInvoked_thenBadRequestResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        Assessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.withStatus(AssessmentStatus.IN_PROGRESS);
        appealContributionRequest.setAssessments(List.of(assessment));
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenMaatApiException_whenCalculateContributionIsInvoked_thenInternalServerErrorResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
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

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}