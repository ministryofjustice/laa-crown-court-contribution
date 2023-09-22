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
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.exeption.ValidationException;
import uk.gov.justice.laa.crime.contribution.model.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.service.MaatCalculateContributionService;
import uk.gov.justice.laa.crime.contribution.validation.CalculateContributionValidator;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.contribution.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CrownCourtContributionController.class)
class CrownCourtContributionControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/contribution/calculate-contribution";

    private static final String GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL = "/api/internal/v1/contribution/" + TestModelDataBuilder.REP_ID
            + "/summaries";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalculateContributionValidator calculateContributionValidator;

    @MockBean
    private MaatCalculateContributionService maatCalculateContributionService;

    @Test
    void givenValidRequest_whenCalculateAppealContributionIsInvoked_thenOkResponse() throws Exception {
        ApiMaatCalculateContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        when(calculateContributionValidator.validate(any(ApiMaatCalculateContributionRequest.class))).thenReturn(Optional.empty());

        when(maatCalculateContributionService.calculateContribution(any(CalculateContributionDTO.class), any()))
                .thenReturn(new ApiMaatCalculateContributionResponse());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL, false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenCalculateAppealContributionIsInvoked_thenBadRequestResponse() throws Exception {
        ApiMaatCalculateContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        when(calculateContributionValidator.validate(any(ApiMaatCalculateContributionRequest.class)))
                .thenThrow(new ValidationException("Test validation exception"));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL, false))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenClientApiException_whenCalculateAppealContributionIsInvoked_thenInternalServerErrorResponse() throws Exception {
        ApiMaatCalculateContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        when(calculateContributionValidator.validate(any(ApiMaatCalculateContributionRequest.class)))
                .thenReturn(Optional.empty());
        when(maatCalculateContributionService.calculateContribution(any(CalculateContributionDTO.class), any()))
                .thenThrow(new APIClientException("Test api client exception"));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestData, ENDPOINT_URL, false))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenValidRequest_whenGetContributionSummariesIsInvoked_thenOkResponse() throws Exception {
        when(maatCalculateContributionService.getContributionSummaries(anyInt(), any()))
                .thenReturn(List.of(new ApiContributionSummary()));

        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL, false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenClientApiException_whenGetContributionSummariesIsInvoked_thenInternalServerErrorResponse() throws Exception {
        when(maatCalculateContributionService.getContributionSummaries(anyInt(), any()))
                .thenThrow(new APIClientException("Test api client exception"));

        mvc.perform(buildRequestGivenContent(HttpMethod.GET, "", GET_CONTRIBUTION_SUMMARIES_ENDPOINT_URL, false))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
