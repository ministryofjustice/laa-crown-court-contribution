package uk.gov.justice.laa.crime.contribution.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import okhttp3.mockwebserver.*;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.contribution.CrownCourtContributionApplication;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.Assessment;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = CrownCourtContributionApplication.class, webEnvironment = DEFINED_PORT)
@DirtiesContext
public class CrownCourtContributionIntegrationTest {

    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String SCOPE_READ_WRITE = "READ_WRITE";
    private static final String CLIENT_ID = "test-client";
    private static final String CLIENT_SECRET = "secret";
    private static final String LAA_TRANSACTION_ID = "999";
    private static final String ENDPOINT_URL = "/api/internal/v1/contribution/appeal";
    private static final String AUTH_URL = "/oauth2/token";

    private MockMvc mvc;
    private MockWebServer mockMaatApi;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @BeforeAll
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @BeforeEach
    public void setupMaatApiServer() throws IOException {
        mockMaatApi = new MockWebServer();
        mockMaatApi.start(9999);

        final Dispatcher dispatcher = new QueueDispatcher() {
            @NotNull @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if (AUTH_URL.equals(request.getPath())) {
                    return getOauthResponse();
                }

                var requestLine = request.getRequestLine();
                if ("GET /favicon.ico HTTP/1.1".equals(requestLine)) {
                    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
                }

                return getResponseQueue().take();
            }
        };
        mockMaatApi.setDispatcher(dispatcher);
    }

    @AfterEach
    public void shutdownMaatApiServer() throws IOException {
        mockMaatApi.shutdown();
    }

    private MockResponse getOauthResponse() {
        Map<String, Object> token = Map.of(
                "expires_in", 3600,
                "token_type", "Bearer",
                "access_token", UUID.randomUUID()
        );
        String responseBody;
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(OK.code());
        mockResponse.setHeader("Content-Type", MediaType.APPLICATION_JSON);

        try {
            responseBody = objectMapper.writeValueAsString(token);
        } catch(JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }

        return mockResponse.setBody(responseBody);
    }

    private String obtainAccessToken() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", CLIENT_CREDENTIALS);
        params.add("scope", SCOPE_READ_WRITE);

        ResultActions result = mvc.perform(post(AUTH_URL)
                        .params(params)
                        .with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
                .andExpect(status().isOk());
        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    private MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String content, String endpointUrl,
                                                                   boolean withAuth) throws Exception {
        String endpoint = endpointUrl != null ? endpointUrl : ENDPOINT_URL;
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.request(method, endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);
        requestBuilder.header("Laa-Transaction-Id", LAA_TRANSACTION_ID);

        if (withAuth) {
            final String accessToken = obtainAccessToken();
            requestBuilder.header("Authorization", "Bearer " + accessToken);
        }

        return requestBuilder;
    }

    @Test
    void givenContributionsDontNeedUpdating_whenCalculateAppealContributionIsInvoked_thenOkResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(BigDecimal.valueOf(250))));
        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(TestModelDataBuilder.buildContribution())));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL, false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.upfrontContributions").value(250));
    }

    @Test
    void givenContributionsNeedUpdating_whenCalculateAppealContributionIsInvoked_thenOkResponse() throws Exception {
        Contribution newContribution = TestModelDataBuilder.buildContribution();
        newContribution.setUpfrontContributions(BigDecimal.valueOf(500));
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(BigDecimal.valueOf(500))));
        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(TestModelDataBuilder.buildContribution())));
        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(newContribution)));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL, false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.upfrontContributions").value(500));
    }

    @Test
    void givenInvalidRequestData_whenCalculateAppealContributionIsInvoked_thenBadRequestResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        Assessment assessment = TestModelDataBuilder.buildAssessment();
        assessment.withStatus(AssessmentStatus.IN_PROGRESS);
        appealContributionRequest.setAssessments(List.of(assessment));
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL, false))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenMaatApiException_whenCalculateAppealContributionIsInvoked_thenInternalServerErrorResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(NOT_IMPLEMENTED.code()));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL, false))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
