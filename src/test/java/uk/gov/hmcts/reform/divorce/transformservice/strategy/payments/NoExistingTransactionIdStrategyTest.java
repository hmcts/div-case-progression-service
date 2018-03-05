package uk.gov.hmcts.reform.divorce.transformservice.strategy.payments;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.PaymentCollection;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

public class NoExistingTransactionIdStrategyTest {

    private NoExistingTransactionIdStrategy noExistingTransactionIdStrategy = new NoExistingTransactionIdStrategy();

    @Test
    public void testNoExistingTransactionIdAddsNewPayment() {
        PaymentCollection newPayment = createPayment("111222333");
        PaymentCollection existingPayment = createPayment("999888777");
        PaymentCollection anotherExistingPayment = createPayment("444555666");

        List<PaymentCollection> existingPaymentsList = new ArrayList<>();
        existingPaymentsList.add(existingPayment);
        existingPaymentsList.add(anotherExistingPayment);

        List<PaymentCollection> expectedPaymentsList = Arrays.asList(existingPayment, anotherExistingPayment, newPayment);
        List<PaymentCollection> returnedPaymentsList = noExistingTransactionIdStrategy.getCurrentPaymentsList(newPayment.getValue(), existingPaymentsList);

        assertThat(returnedPaymentsList, equalTo(expectedPaymentsList));
    }

    private PaymentCollection createPayment(String id) {
        Payment payment = new Payment();
        payment.setPaymentTransactionId(id);
        
        return PaymentCollection.builder().value(payment).build();
    }
}