package uk.gov.hmcts.reform.divorce.draftservice.exception;

public class DraftStoreUnavailableException extends RuntimeException {
    public DraftStoreUnavailableException(String message) {
        super(message);
    }
}
