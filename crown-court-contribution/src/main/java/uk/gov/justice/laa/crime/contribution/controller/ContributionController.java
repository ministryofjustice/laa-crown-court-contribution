package uk.gov.justice.laa.crime.contribution.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.contribution.builder.ContributionDTOBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.service.ContributionRulesService;
import uk.gov.justice.laa.crime.contribution.service.MaatCalculateContributionService;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/contribution")
public class ContributionController {

    private final MaatCalculateContributionService maatCalculateContributionService;
    private final ContributionRulesService contributionRulesService;

    @PostMapping(value = "/calculate-contribution", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Calculate Contribution")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiMaatCalculateContributionResponse.class)))
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiMaatCalculateContributionResponse> calculateContribution(
            @Parameter(
                            description = "Data required to calculate contributions",
                            content =
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    ApiMaatCalculateContributionRequest.class)))
                    @Valid
                    @RequestBody
                    ApiMaatCalculateContributionRequest maatCalculateContributionRequest) {
        log.info("Contributions - {}", maatCalculateContributionRequest);
        CalculateContributionDTO calculateContributionDTO = preProcessRequest(maatCalculateContributionRequest);
        ApiMaatCalculateContributionResponse response =
                maatCalculateContributionService.calculateContribution(calculateContributionDTO);
        log.info("calculateContribution response - {}", response);
        return ResponseEntity.ok(response);
    }

    private CalculateContributionDTO preProcessRequest(
            ApiMaatCalculateContributionRequest maatCalculateContributionRequest) {
        return ContributionDTOBuilder.build(maatCalculateContributionRequest);
    }

    @GetMapping(value = "/summaries/{repId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Get Contribution Summary")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiMaatCalculateContributionResponse.class)))
    @DefaultHTTPErrorResponse
    public ResponseEntity<List<ApiContributionSummary>> getContributionSummaries(@PathVariable int repId) {
        log.info("Received request to get contribution summaries for repId {}", repId);
        return ResponseEntity.ok(maatCalculateContributionService.getContributionSummaries(repId));
    }

    @PostMapping(value = "/check-contribution-rule", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Check if contribution Rule is applicable")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @DefaultHTTPErrorResponse
    public ResponseEntity<Boolean> checkContributionRule(
            @Parameter(
                            description = "Data required to check contribution rule",
                            content =
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    ApiMaatCheckContributionRuleRequest.class)))
                    @Valid
                    @RequestBody
                    ApiMaatCheckContributionRuleRequest apiMaatCheckContributionRuleRequest) {
        log.info("Received request to check contribution rule");
        CrownCourtOutcome crownCourtOutcome =
                contributionRulesService.getActiveCCOutcome(apiMaatCheckContributionRuleRequest.getCrownCourtOutcome());
        return ResponseEntity.ok(contributionRulesService.isContributionRuleApplicable(
                apiMaatCheckContributionRuleRequest.getCaseType(),
                apiMaatCheckContributionRuleRequest.getMagCourtOutcome(),
                crownCourtOutcome));
    }
}
