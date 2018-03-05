package uk.gov.hmcts.reform.divorce.transformservice.strategy.payments;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.PaymentCollection;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

public class ExistingTransactionIdStrategyTest {

    private ExistingTransactionIdStrategy existingTransactionIdStrategy = new ExistingTransactionIdStrategy();

    @Test
    public void testExistingTransactionIdAndPaymentReferenceWillReplacePayment() {
        PaymentCollection newPayment = createPayment("111222333", "success");
        PaymentCollection existingPayment = createPayment("999888777", "success");
        PaymentCollection toBeReplacedPayment = createPayment("111222333", "created");

        List<PaymentCollection> existingPaymentsList = new ArrayList<>();
        existingPaymentsList.add(existingPayment);
        existingPaymentsList.add(toBeReplacedPayment);

        List<PaymentCollection> expectedPaymentsList = Arrays.asList(existingPayment, newPayment);
        List<PaymentCollection> returnedPaymentsList = existingTransactionIdStrategy.getCurrentPaymentsList(newPayment.getValue(), existingPaymentsList);

        assertThat(returnedPaymentsList, equalTo(expectedPaymentsList));
    }

    private PaymentCollection createPayment(String id, String status) {
        Payment payment = new Payment();
        payment.setPaymentTransactionId(id);
        payment.setPaymentStatus(status);
        
        return PaymentCollection.builder().value(payment).build();
    }
}