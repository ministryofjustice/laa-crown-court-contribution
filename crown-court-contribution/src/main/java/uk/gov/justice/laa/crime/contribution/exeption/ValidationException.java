package uk.gov.justice.laa.crime.contribution.exeption;

public class ValidationException extends RuntimeException {
    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable rootCause) {
        super(rootCause);
    }

    public ValidationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
