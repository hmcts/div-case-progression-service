package uk.gov.hmcts.reform.divorce.transformservice.docker.exception;

public class RequestCreationException extends RuntimeException {
    public RequestCreationException(Throwable throwable) {
        super(throwable);
    }
}
