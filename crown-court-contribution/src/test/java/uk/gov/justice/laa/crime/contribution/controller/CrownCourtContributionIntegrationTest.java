package uk.gov.justice.laa.crime.contribution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.contribution.CrownCourtContributionApplication;
import uk.gov.justice.laa.crime.contribution.config.CrownCourtContributionTestConfiguration;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.Assessment;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AssessmentStatus;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.contribution.util.MockWebServerStubs;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_IMPLEMENTED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.contribution.util.RequestBuilderUtils.buildRequestGivenContent;

@RunWith(SpringRunner.class)
@Import(CrownCourtContributionTestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = CrownCourtContributionApplication.class, webEnvironment = DEFINED_PORT)
@DirtiesContext
class CrownCourtContributionIntegrationTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/contribution/appeal";
    private static MockWebServer mockMaatApi;
    private MockMvc mvc;
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

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL))
                .andExpect(status().isOk());
    }

    @Test
    void givenContributionsNeedUpdating_whenCalculateAppealContributionIsInvoked_thenOkResponse() throws Exception {
        Contribution newContribution = TestModelDataBuilder.buildContribution();
        newContribution.setUpfrontContributions(BigDecimal.valueOf(500));
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest().withCaseType(CaseType.APPEAL_CC);
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

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL))
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
