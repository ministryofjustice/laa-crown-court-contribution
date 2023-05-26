package uk.gov.justice.laa.crime.contribution.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import uk.gov.justice.laa.crime.contribution.dto.ErrorDTO;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionResponse;
import uk.gov.justice.laa.crime.contribution.service.AppealContributionService;
import uk.gov.justice.laa.crime.contribution.validation.AppealContributionValidator;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/contribution/appeal")
public class CrownCourtContributionController {

    private final AppealContributionService appealContributionService;
    private final AppealContributionValidator appealContributionValidator;

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Calculate appeal contributions")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AppealContributionResponse.class)
            )
    )
    @ApiResponse(responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    @ApiResponse(responseCode = "404",
            description = "Not found",
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
    public ResponseEntity<AppealContributionResponse> calculateAppealContribution(@Parameter(description = "Data required to calculate appeal contributions",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AppealContributionRequest.class)
            )
    ) @Valid @RequestBody AppealContributionRequest appealContributionRequest) {
        log.info("Received request to calculate appeal contributions for ID {}", appealContributionRequest.getApplId());
        appealContributionValidator.validate(appealContributionRequest);
        return ResponseEntity.ok(appealContributionService.calculateContribution(appealContributionRequest));
    }
}
