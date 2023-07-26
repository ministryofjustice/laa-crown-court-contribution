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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
import uk.gov.justice.laa.crime.contribution.config.CrownCourtContributionTestConfiguration;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.Assessment;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.service.CompareContributionService;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;
import uk.gov.justice.laa.crime.contribution.util.MockWebServerStubs;

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
import static uk.gov.justice.laa.crime.contribution.util.RequestBuilderUtils.buildRequestGivenContent;

@RunWith(SpringRunner.class)
@Import(CrownCourtContributionTestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = CrownCourtContributionApplication.class, webEnvironment = DEFINED_PORT)
@DirtiesContext
class CrownCourtContributionIntegrationTest {
    private static final boolean IS_VALID = true;
    private static final String ERROR_MSG = "Call to service MAAT-API failed.";
    private static final String ENDPOINT_URL = "/api/internal/v1/contribution/appeal";

    private MockMvc mvc;
    private static MockWebServer mockMaatApi;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeAll
    public void initialiseMockWebServer() throws IOException {
        mockMaatApi = new MockWebServer();
        mockMaatApi.start(9999);
        mockMaatApi.setDispatcher(MockWebServerStubs.getDispatcher());
    }

    @AfterAll
    protected void shutdownMockWebServer() throws IOException {
        mockMaatApi.shutdown();
    }

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }
    @Test
    void givenContributionsDontNeedUpdating_whenCalculateAppealContributionIsInvoked_thenOkResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(TestModelDataBuilder.getRepOrderDTO())));

        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(BigDecimal.valueOf(250))));
        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(List.of(TestModelDataBuilder.buildContribution()))));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL))
                .andExpect(status().isOk());
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
                .setBody(objectMapper.writeValueAsString(TestModelDataBuilder.getRepOrderDTO())));

        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(BigDecimal.valueOf(500))));
        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(List.of(TestModelDataBuilder.buildContribution()))));
        mockMaatApi.enqueue(new MockResponse()
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setResponseCode(OK.code())
                .setBody(objectMapper.writeValueAsString(newContribution)));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL))
                .andExpect(status().isOk());
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

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
