package uk.gov.justice.laa.crime.contribution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.service.ContributionRulesService;
import uk.gov.justice.laa.crime.contribution.service.ContributionService;
import uk.gov.justice.laa.crime.contribution.service.MaatCalculateContributionService;
import uk.gov.justice.laa.crime.contribution.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ContributionController.class)
class ContributionControllerTest {

    private static final String BASE_URL = "/api/internal/v1/contribution/";
    private static final String ENDPOINT_URL = BASE_URL + "calculate-contribution";
    private static final String CHECK_CONTRIBUTION_RULE_ENDPOINT_URL = BASE_URL + "/check-contribution-rule";
    private static final String GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL =
            BASE_URL + "summaries/" + TestModelDataBuilder.REP_ID;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContributionService contributionService;

    @MockBean
    private MaatCalculateContributionService maatCalculateContributionService;

    @MockBean
    private ContributionRulesService contributionRulesService;

    @MockBean
    private TraceIdHandler traceIdHandler;

    @Test
    void givenValidRequest_whenCalculateAppealContributionIsInvoked_thenOkResponse() throws Exception {
        ApiMaatCalculateContributionRequest appealContributionRequest =
                TestModelDataBuilder.buildAppealContributionRequest();

        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        when(maatCalculateContributionService.calculateContribution(any(CalculateContributionDTO.class)))
                .thenReturn(new ApiMaatCalculateContributionResponse());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL, false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenCalculateAppealContributionIsInvoked_thenBadRequestResponse() throws Exception {
        String requestData = objectMapper.writeValueAsString(new ApiMaatCalculateContributionRequest());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL, false))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenClientApiException_whenCalculateAppealContributionIsInvoked_thenInternalServerErrorResponse() throws Exception {
        ApiMaatCalculateContributionRequest appealContributionRequest =
                TestModelDataBuilder.buildAppealContributionRequest();

        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        when(maatCalculateContributionService.calculateContribution(any(CalculateContributionDTO.class)))
                .thenThrow(WebClientRequestException.class);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL, false))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenValidRequest_whenGetContributionSummariesIsInvoked_thenOkResponse() throws Exception {
        when(maatCalculateContributionService.getContributionSummaries(anyInt()))
                .thenReturn(List.of(new ApiContributionSummary()));

        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL, false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenClientApiException_whenGetContributionSummariesIsInvoked_thenInternalServerErrorResponse() throws Exception {
        when(maatCalculateContributionService.getContributionSummaries(anyInt()))
                .thenThrow(WebClientRequestException.class);

        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL, false))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenValidRequest_whenCheckContributionRuleIsInvoked_thenOkResponse() throws Exception {
        ApiMaatCheckContributionRuleRequest apiMaatCheckContributionRuleRequest =
                TestModelDataBuilder.buildCheckContributionRuleRequest();
        String requestData = objectMapper.writeValueAsString(apiMaatCheckContributionRuleRequest);
        when(contributionRulesService.getActiveCCOutcome(any()))
                .thenReturn(CrownCourtOutcome.SUCCESSFUL);
        when(contributionRulesService.isContributionRuleApplicable(any(), any(), any()))
                .thenReturn(Boolean.TRUE);
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, CHECK_CONTRIBUTION_RULE_ENDPOINT_URL, false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Boolean.TRUE));
    }

    @Test
    void givenInvalidRequest_whenCheckContributionRuleIsInvoked_thenInternalServerErrorResponse() throws Exception {
        ApiMaatCheckContributionRuleRequest apiMaatCheckContributionRuleRequest =
                TestModelDataBuilder.buildCheckContributionRuleRequest();
        String requestData = objectMapper.writeValueAsString(apiMaatCheckContributionRuleRequest);
        when(contributionRulesService.getActiveCCOutcome(any()))
                .thenThrow(new RuntimeException("Test Exception"));
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, CHECK_CONTRIBUTION_RULE_ENDPOINT_URL, false))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInValidRequest_whenCheckContributionRuleIsInvoked_thenBadRequestResponse() throws Exception {
        ApiMaatCheckContributionRuleRequest apiMaatCheckContributionRuleRequest =
                TestModelDataBuilder.buildCheckContributionRuleRequest();
        apiMaatCheckContributionRuleRequest.setCaseType(null);
        String body = objectMapper.writeValueAsString(apiMaatCheckContributionRuleRequest);
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, body, CHECK_CONTRIBUTION_RULE_ENDPOINT_URL, false))
                .andExpect(status().isBadRequest());
    }

}
