package uk.gov.hmcts.reform.divorce.transformservice.strategy.payments;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.PaymentCollection;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

public class NoExistingPaymentStrategyTest {

    private NoExistingPaymentStrategy noExistingPaymentStrategy = new NoExistingPaymentStrategy();

    @Test
    public void testNoExistingPaymentsAddsJustNewPayment() {
        Payment newPayment = new Payment();
        newPayment.setPaymentTransactionId("111222333");

        List<PaymentCollection> existingPaymentsList = null;

        List<PaymentCollection> expectedPaymentsList = Arrays.asList(PaymentCollection.builder().value(newPayment).build());
        List<PaymentCollection> returnedPaymentsList = noExistingPaymentStrategy.getCurrentPaymentsList(newPayment, existingPaymentsList);

        assertThat(returnedPaymentsList, equalTo(expectedPaymentsList));
    }
}