package uk.gov.hmcts.reform.divorce.transformservice.docker.exception;

import java.io.IOException;

public class RoleCreationException extends RuntimeException {
    public RoleCreationException(Throwable cause) {
        super(cause);
    }
}
