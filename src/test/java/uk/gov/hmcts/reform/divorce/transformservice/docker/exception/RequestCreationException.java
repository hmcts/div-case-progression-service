package uk.gov.hmcts.reform.divorce.transformservice.docker.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class RequestCreationException extends RuntimeException {
    public RequestCreationException(Throwable e) {
        super(e);
    }
}
