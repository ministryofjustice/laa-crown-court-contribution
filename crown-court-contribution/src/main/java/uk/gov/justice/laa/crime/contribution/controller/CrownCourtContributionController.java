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
import uk.gov.justice.laa.crime.contribution.builder.ContributionDTOBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.ErrorDTO;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.service.MaatCalculateContributionService;
import uk.gov.justice.laa.crime.contribution.validation.CalculateContributionValidator;


@Slf4j
@RestController
@RequiredArgsConstructor
    @RequestMapping("api/internal/v1/contribution/")
public class CrownCourtContributionController {

    private final MaatCalculateContributionService maatCalculateContributionService;
    private final CalculateContributionValidator calculateContributionValidator;

    @PostMapping(value = "/calculate-contribution", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Calculate Contribution")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiMaatCalculateContributionResponse.class)
            )
    )
    @ApiResponse(responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    @ApiResponse(responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ResponseEntity<ApiMaatCalculateContributionResponse> calculateContribution(
            @Parameter(description = "Data required to calculate contributions",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiMaatCalculateContributionRequest.class)
                    )
            )
            @Valid @RequestBody
            ApiMaatCalculateContributionRequest maatCalculateContributionRequest,
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {

        log.info("Received request to calculate contributions for ID {}", maatCalculateContributionRequest.getApplId());
        calculateContributionValidator.validate(maatCalculateContributionRequest);
        CalculateContributionDTO calculateContributionDTO = preProcessRequest(maatCalculateContributionRequest);
        return ResponseEntity.ok(maatCalculateContributionService.calculateContribution(calculateContributionDTO, laaTransactionId));
    }

    private CalculateContributionDTO preProcessRequest(ApiMaatCalculateContributionRequest maatCalculateContributionRequest) {
        return ContributionDTOBuilder.build(maatCalculateContributionRequest);
    }


    @GetMapping(value = "/get-contribution-summaries", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Get Contribution Summary")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiMaatCalculateContributionResponse.class)
            )
    )
    @ApiResponse(responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    @ApiResponse(responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ResponseEntity<ApiMaatCalculateContributionResponse> getContributionSummaries(
            @Parameter(description = "Data required to get Contribution Summary",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiMaatCalculateContributionRequest.class)
                    )
            )
            @Valid @RequestBody
            ApiMaatCalculateContributionRequest maatCalculateContributionRequest,
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {

        log.info("Received request to get contribution summaries for ID {}", maatCalculateContributionRequest.getApplId());
        CalculateContributionDTO calculateContributionDTO = preProcessRequest(maatCalculateContributionRequest);
        return ResponseEntity.ok(maatCalculateContributionService.getContributionSummaries(calculateContributionDTO, laaTransactionId));
    }

}
