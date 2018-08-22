package uk.gov.hmcts.reform.divorce.transformservice.exception;

public class DuplicateCaseException extends RuntimeException {
    public DuplicateCaseException(String message) {
        super(message);
    }
}
