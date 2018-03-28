package uk.gov.hmcts.reform.divorce.transformservice.service;

public class InvalidPetitionException extends RuntimeException {

    public InvalidPetitionException(String message) {
        super(message);
    }
}
