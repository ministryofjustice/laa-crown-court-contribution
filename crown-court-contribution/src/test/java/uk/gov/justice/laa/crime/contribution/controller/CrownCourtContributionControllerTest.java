package uk.gov.justice.laa.crime.contribution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
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
import uk.gov.justice.laa.crime.contribution.service.AppealContributionService;
import uk.gov.justice.laa.crime.contribution.validation.AppealContributionValidator;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@Import(CrownCourtContributionTestConfiguration.class)
@SpringBootTest(classes = {CrownCourtContributionApplication.class}, webEnvironment = DEFINED_PORT)
@DirtiesContext
public class CrownCourtContributionControllerTest {

    private static final String CLIENT_SECRET = "secret";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String CLIENT_ID = "test-client";
    private static final String SCOPE_READ_WRITE = "READ_WRITE";
    private static final String ENDPOINT_URL = "/api/internal/v1/contribution/appeal";
    private static final String AUTH_URL = "/oauth2/token";

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private AppealContributionValidator appealContributionValidator;

    @MockBean
    private AppealContributionService appealContributionService;

    @BeforeEach
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    private String obtainAccessToken() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", CLIENT_CREDENTIALS);
        params.add("scope", SCOPE_READ_WRITE);

        ResultActions result = mvc.perform(post("/oauth2/token")
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
        if (withAuth) {
            final String accessToken = obtainAccessToken();
            requestBuilder.header("Authorization", "Bearer " + accessToken);
        }
        return requestBuilder;
    }

    @Test
    void givenValidRequest_whenCalculateAppealContributionIsInvoked_thenOkResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        when(appealContributionValidator.validate(any(AppealContributionRequest.class)))
                .thenReturn(Optional.empty());
        when(appealContributionService.calculateContribution(any(AppealContributionRequest.class), anyString()))
                .thenReturn(TestModelDataBuilder.buildAppealContributionResponse());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL,false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void givenInvalidRequest_whenCalculateAppealContributionIsInvoked_thenBadRequestResponse() throws Exception {

    }

    @Test
    void givenClientApiException_whenCalculateAppealContributionIsInvoked_thenInternalServerErrorResponse() throws Exception {

    }
}
