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
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.ErrorDTO;
import uk.gov.justice.laa.crime.contribution.model.CalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.CalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.service.CalculateContributionService;
import uk.gov.justice.laa.crime.contribution.validation.CalculateContributionValidator;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/contribution/appeal")
public class CrownCourtContributionController {

    private final CalculateContributionService calculateContributionService;
    private final CalculateContributionValidator calculateContributionValidator;

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Calculate Contribution")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CalculateContributionResponse.class)
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
    public ResponseEntity<CalculateContributionResponse> calculateContribution(
            @Parameter(description = "Data required to calculate contributions",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CalculateContributionRequest.class)
                    )
            )
            @Valid @RequestBody
            CalculateContributionRequest calculateContributionRequest,
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {

        log.info("Received request to calculate contributions for ID {}", calculateContributionRequest.getApplId());
        calculateContributionValidator.validate(calculateContributionRequest);
        ContributionDTO contributionDTO = preProcessRequest(calculateContributionRequest);
        return ResponseEntity.ok(calculateContributionService.calculateContribution(contributionDTO, laaTransactionId));
    }

    private ContributionDTO preProcessRequest(CalculateContributionRequest calculateContributionRequest) {
        return ContributionDTOBuilder.build(calculateContributionRequest);
    }

}
