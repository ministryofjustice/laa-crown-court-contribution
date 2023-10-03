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
import uk.gov.justice.laa.crime.contribution.dto.ErrorDTO;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.service.CalculateContributionService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v2/contribution/calculate")
public class CrownCourtCalculateContributionController {

    private final CalculateContributionService calculateContributionService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Calculate Contribution")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiCalculateContributionResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiCalculateContributionResponse> calculateContribution(
            @Parameter(description = "Data required to calculate contributions",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiCalculateContributionRequest.class)
                    )
            )
            @Valid @RequestBody
            ApiCalculateContributionRequest apiCalculateContributionRequest) {

        log.info("Received request to calculate contributions");
        return ResponseEntity.ok(calculateContributionService.calculateContribution(apiCalculateContributionRequest));
    }

}
