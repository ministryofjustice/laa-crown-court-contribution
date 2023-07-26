package uk.gov.justice.laa.crime.contribution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.exeption.ValidationException;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.CalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.CalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.service.CalculateContributionService;
import uk.gov.justice.laa.crime.contribution.service.CompareContributionService;
import uk.gov.justice.laa.crime.contribution.validation.CalculateContributionValidator;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.contribution.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CrownCourtContributionController.class)
class CrownCourtContributionControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/contribution/appeal";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalculateContributionValidator calculateContributionValidator;

    @MockBean
    private CalculateContributionService calculateContributionService;

    @MockBean
    private CompareContributionService compareContributionService;


    @Test
    void givenValidRequest_whenCalculateAppealContributionIsInvoked_thenOkResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        when(calculateContributionValidator.validate(any(CalculateContributionRequest.class))).thenReturn(Optional.empty());

        when(calculateContributionService.calculateContribution(any(ContributionDTO.class), anyString()))
                .thenReturn(new CalculateContributionResponse());

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL, false))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenInvalidRequest_whenCalculateAppealContributionIsInvoked_thenBadRequestResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        when(calculateContributionValidator.validate(any(CalculateContributionRequest.class)))
                .thenThrow(new ValidationException("Test validation exception"));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL, false))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenClientApiException_whenCalculateAppealContributionIsInvoked_thenInternalServerErrorResponse() throws Exception {
        AppealContributionRequest appealContributionRequest = TestModelDataBuilder.buildAppealContributionRequest();
        String requestData = objectMapper.writeValueAsString(appealContributionRequest);

        when(calculateContributionValidator.validate(any(CalculateContributionRequest.class)))
                .thenReturn(Optional.empty());
        when(calculateContributionService.calculateContribution(any(ContributionDTO.class), anyString()))
                .thenThrow(new APIClientException("Test api client exception"));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestData, ENDPOINT_URL, false))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
