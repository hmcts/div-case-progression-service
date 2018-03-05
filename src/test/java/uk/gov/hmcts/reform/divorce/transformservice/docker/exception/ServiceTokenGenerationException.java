package uk.gov.hmcts.reform.divorce.transformservice.docker.exception;

public class ServiceTokenGenerationException extends RuntimeException {
    public ServiceTokenGenerationException(Throwable cause) {
        super(cause);
    }
}
