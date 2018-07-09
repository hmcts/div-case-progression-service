package uk.gov.hmcts.reform.divorce.pay.exceptions;

public class PaymentFailedException extends RuntimeException{

    public PaymentFailedException(String message) {
        super(message);
    }

}
