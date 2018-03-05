package uk.gov.hmcts.reform.divorce.transformservice.docker.exception;

public class UserTokenGenerationException extends RuntimeException {
    public UserTokenGenerationException(Throwable cause) {
        super(cause);
    }
}
