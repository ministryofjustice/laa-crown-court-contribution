package uk.gov.justice.laa.crime.contribution.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_IMPLEMENTED;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder.getApiCrownCourtOutcome;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestGivenContent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.contribution.CrownCourtContributionApplication;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.dto.ErrorDTO;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;

@EnableWireMock
@DirtiesContext
@AutoConfigureObservability
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = CrownCourtContributionApplication.class, webEnvironment = DEFINED_PORT)
class ContributionControllerContributionIntegrationTest {

    private MockMvc mvc;
    private static final String ENDPOINT_URL = "/api/internal/v1/contribution/calculate-contribution";
    private static final String GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL = "/api/internal/v1/contribution/summaries/" + TestModelDataBuilder.REP_ID;
    private static final String CHECK_CONTRIBUTION_RULE_ENDPOINT_URL = "/api/internal/v1/contribution/check-contribution-rule";

    @InjectWireMock
    private static WireMockServer wiremock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @BeforeEach
    void setup() throws JsonProcessingException {
        stubForOAuth();
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

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
    }


    private void setupAppealStubbing(ApiMaatCalculateContributionRequest appealContributionRequest,
                                     Contribution contribution) throws JsonProcessingException {

        var repOrderUrl = "/rep-orders/" + appealContributionRequest.getRepId();

        wiremock.stubFor(get(urlEqualTo(repOrderUrl))
                .willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getRepOrderDTO()))
                )
        );

        var findContributionUrl = "/contributions/" + appealContributionRequest.getRepId();

        wiremock.stubFor(get(urlPathMatching(findContributionUrl))
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

        var createContributionUrl = "/contributions";

        wiremock.stubFor(post(urlEqualTo(createContributionUrl))
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
        String requestData = objectMapper.writeValueAsString(new ApiMaatCalculateContributionRequest());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenMaatApiException_whenCalculateContributionIsInvoked_thenInternalServerErrorResponse() throws Exception {
        ApiMaatCalculateContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        var repOrderUrl = "/rep-orders/" + appealContributionRequest.getRepId();
        ErrorDTO errorDTO = ErrorDTO.builder()
                .code(HttpStatus.NOT_IMPLEMENTED.name())
                .build();
        wiremock.stubFor(get(urlEqualTo(repOrderUrl))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(NOT_IMPLEMENTED.code())
                                .withBody(Json.write(errorDTO))
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                )
        );

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenMaatApiException_whenGetContributionSummariesIsInvoked_thenInternalServerErrorResponse() throws Exception {
        var summariesUrl = "/contributions/" + TestModelDataBuilder.REP_ID + "/summary";
        ErrorDTO errorDTO = ErrorDTO.builder()
                .code(HttpStatus.NOT_IMPLEMENTED.name())
                .build();
        wiremock.stubFor(get(urlEqualTo(summariesUrl))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(NOT_IMPLEMENTED.code())
                                .withBody(Json.write(errorDTO))
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                )
        );
        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenApiMaatCalculateContributionRequest_whenGetContributionSummariesIsInvoked_thenOkResponse() throws Exception {
        List<ContributionsSummaryDTO> contributionsSummaryDTOList = List.of(TestModelDataBuilder.getContributionSummaryDTO());
        var summariesUrl = "/contributions/" + TestModelDataBuilder.REP_ID + "/summary";

        wiremock.stubFor(get(urlEqualTo(summariesUrl))
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