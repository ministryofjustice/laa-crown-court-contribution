package uk.gov.justice.laa.crime.contribution.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestGivenContent;

import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.service.CalculateContributionService;
import uk.gov.justice.laa.crime.contribution.tracing.TraceIdHandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;

@DirtiesContext
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CalculateContributionController.class)
class CalculateContributionControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v2/contribution/calculate";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CalculateContributionService calculateContributionService;

    @MockitoBean
    private TraceIdHandler traceIdHandler;

    @Test
    void givenValidRequest_whenCalculateContributionIsInvoked_thenOkResponse() throws Exception {
        ApiCalculateContributionRequest apiCalculateContributionRequest =
                TestModelDataBuilder.buildApiCalculateContributionRequest();
        String requestData = objectMapper.writeValueAsString(apiCalculateContributionRequest);

        when(calculateContributionService.calculateContribution(any(ApiCalculateContributionRequest.class)))
                .thenReturn(new ApiCalculateContributionResponse());

        mvc.perform(buildRequestGivenContent(HttpMethod.GET, requestData, ENDPOINT_URL, false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenCalculateContributionIsInvoked_thenBadRequestResponse() throws Exception {
        ApiCalculateContributionRequest apiCalculateContributionRequest =
                TestModelDataBuilder.buildInvalidApiCalculateContributionRequest();
        String requestData = objectMapper.writeValueAsString(apiCalculateContributionRequest);

        mvc.perform(buildRequestGivenContent(HttpMethod.GET, requestData, ENDPOINT_URL, false))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenClientApiException_whenCalculateContributionIsInvoked_thenInternalServerErrorResponse() throws Exception {
        ApiCalculateContributionRequest apiCalculateContributionRequest =
                TestModelDataBuilder.buildApiCalculateContributionRequest();
        String requestData = objectMapper.writeValueAsString(apiCalculateContributionRequest);

        when(calculateContributionService.calculateContribution(any(ApiCalculateContributionRequest.class)))
                .thenThrow(WebClientRequestException.class);

        mvc.perform(buildRequestGivenContent(HttpMethod.GET, requestData, ENDPOINT_URL, false))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
