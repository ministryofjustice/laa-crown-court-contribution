package uk.gov.justice.laa.crime.contribution.exeption;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.contribution.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.dto.ErrorDTO;
import uk.gov.justice.laa.crime.exception.ValidationException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CCCExceptionHandler {

    private final TraceIdHandler traceIdHandler;
    private final ObjectMapper mapper;

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorDTO> onRuntimeException(WebClientResponseException exception) {
        String errorMessage;
        try {
            ErrorDTO errorDTO = mapper.readValue(exception.getResponseBodyAsString(), ErrorDTO.class);
            errorMessage = errorDTO.getMessage();
        } catch (IOException ex) {
            log.warn("Unable to read the ErrorDTO from WebClientResponseException", ex);
            errorMessage = exception.getMessage();
        }
        return buildErrorResponse(exception.getStatusCode(), errorMessage, traceIdHandler.getTraceId());
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ErrorDTO> onRuntimeException(WebClientRequestException exception) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), traceIdHandler.getTraceId());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(ValidationException exception) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), traceIdHandler.getTraceId());
    }

    private static ResponseEntity<ErrorDTO> buildErrorResponse(HttpStatusCode status, String message, String traceId) {
        log.error("Exception Occurred. Status - {}, Detail - {}, TraceId - {}", status, message, traceId);
        return new ResponseEntity<>(ErrorDTO.builder().traceId(traceId).code(status.toString()).message(message).build(), status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> handleRunTimeException(RuntimeException exception) {
        log.error("Service is failed due to in internal error.", exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), traceIdHandler.getTraceId());
    }

}
