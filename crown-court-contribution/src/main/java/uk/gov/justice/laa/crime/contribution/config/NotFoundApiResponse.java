package uk.gov.justice.laa.crime.contribution.config;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import uk.gov.justice.laa.crime.dto.ErrorDTO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ApiResponses(value = {
        @ApiResponse(responseCode = "404",
                description = "Not Found.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorDTO.class)))
})
public @interface NotFoundApiResponse {
}
