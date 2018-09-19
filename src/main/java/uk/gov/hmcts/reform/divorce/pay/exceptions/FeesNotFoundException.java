package uk.gov.hmcts.reform.divorce.pay.exceptions;

public class FeesNotFoundException extends RuntimeException {

    public FeesNotFoundException(String message) {
        super(message);
    }
}
