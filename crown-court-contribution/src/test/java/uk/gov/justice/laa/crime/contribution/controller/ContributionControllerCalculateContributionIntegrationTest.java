package uk.gov.justice.laa.crime.contribution.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.CrownCourtContributionApplication;
import uk.gov.justice.laa.crime.contribution.config.CrownCourtContributionTestConfiguration;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(CrownCourtContributionTestConfiguration.class)
@SpringBootTest(classes = CrownCourtContributionApplication.class, webEnvironment = DEFINED_PORT)
@AutoConfigureObservability
@AutoConfigureWireMock(port = 9999)
class ContributionControllerCalculateContributionIntegrationTest {

    private MockMvc mvc;
    private static final String ENDPOINT_URL = "/api/internal/v2/contribution/calculate";

    @Autowired
    private WireMockServer wiremock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @AfterEach
    void after() {
        wiremock.resetAll();
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
    void givenAEmptyContent_whenCalculateContributionIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCalculateContributionIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenValidRequestWithDisposableIncome_whenCalculateContributionIsInvoked_thenOkResponse() throws Exception {
        ApiCalculateContributionRequest apiCalculateContributionRequest = TestModelDataBuilder.buildApiCalculateContributionRequest();
        String requestData = objectMapper.writeValueAsString(apiCalculateContributionRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.GET, requestData, ENDPOINT_URL)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void givenValidRequestWithUpliftApplied_whenCalculateContributionIsInvoked_thenOkResponse() throws Exception {
        ApiCalculateContributionRequest apiCalculateContributionRequest = TestModelDataBuilder
                .buildApiCalculateContributionRequest().withUpliftApplied(true)
                .withMinUpliftedMonthlyAmount(BigDecimal.valueOf(80))
                .withUpliftedIncomePercent(BigDecimal.TEN);
        String requestData = objectMapper.writeValueAsString(apiCalculateContributionRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.GET, requestData, ENDPOINT_URL)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

}