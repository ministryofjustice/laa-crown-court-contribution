package uk.gov.justice.laa.crime.contribution.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.contribution.dto.ErrorDTO;

@Slf4j
@RestControllerAdvice
public class CCCExceptionHandler {

    private static ResponseEntity<ErrorDTO> buildErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(ErrorDTO.builder().code(status.toString()).message(message).build(), status);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(ValidationException exception) {
        log.error("Validation exception: {}", exception);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(APIClientException.class)
    public ResponseEntity<ErrorDTO> handleApiClientException(APIClientException exception) {
        log.error("API client exception: {}", exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
