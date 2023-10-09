package uk.gov.justice.laa.crime.contribution.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.crime.contribution.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.contribution.builder.ContributionDTOBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.ApiContributionTransferRequest;
import uk.gov.justice.laa.crime.contribution.model.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.model.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.contribution.service.ContributionService;
import uk.gov.justice.laa.crime.contribution.service.MaatCalculateContributionService;
import uk.gov.justice.laa.crime.contribution.validation.CalculateContributionValidator;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/contribution")
public class ContributionController {

    private final ContributionService contributionService;
    private final CalculateContributionValidator calculateContributionValidator;
    private final MaatCalculateContributionService maatCalculateContributionService;


    @PostMapping(value = "/calculate-contribution", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Calculate Contribution")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiMaatCalculateContributionResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiMaatCalculateContributionResponse> calculateContribution(
            @Parameter(description = "Data required to calculate contributions",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiMaatCalculateContributionRequest.class)
                    )
            )
            @Valid @RequestBody
            ApiMaatCalculateContributionRequest maatCalculateContributionRequest,
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {

        log.info("Received request to calculate contributions for ID {}", maatCalculateContributionRequest.getRepId());
        calculateContributionValidator.validate(maatCalculateContributionRequest);
        CalculateContributionDTO calculateContributionDTO = preProcessRequest(maatCalculateContributionRequest);
        return ResponseEntity.ok(maatCalculateContributionService.calculateContribution(calculateContributionDTO, laaTransactionId));
    }

    private CalculateContributionDTO preProcessRequest(ApiMaatCalculateContributionRequest maatCalculateContributionRequest) {
        return ContributionDTOBuilder.build(maatCalculateContributionRequest);
    }


    @GetMapping(value = "/summaries/{repId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Get Contribution Summary")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiMaatCalculateContributionResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<List<ApiContributionSummary>> getContributionSummaries(
            @PathVariable int repId,
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {
        log.info("Received request to get contribution summaries for repId {}", repId);
        return ResponseEntity.ok(maatCalculateContributionService.getContributionSummaries(repId, laaTransactionId));
    }


    @PostMapping(value = "/request-transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Request Contributions Transfer")
    @ApiResponse(responseCode = "200")
    @DefaultHTTPErrorResponse
    public ResponseEntity<Void> requestTransfer(@Valid @RequestBody ApiContributionTransferRequest request,
                                          @RequestHeader(value = "Laa-Transaction-Id", required = false)
                                          String laaTransactionId) {
        log.info("Received request to transfer contribution for ID: {}", request.getContributionId());
        contributionService.requestTransfer(request, laaTransactionId);
        return ResponseEntity.ok().build();
    }

}
