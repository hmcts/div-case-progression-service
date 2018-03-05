package uk.gov.hmcts.reform.divorce.transformservice.strategy.payments;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.PaymentCollection;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

@Component
public class NoExistingPaymentStrategy implements PaymentStrategy {

    @Override
    public List<PaymentCollection> getCurrentPaymentsList(Payment newPayment, List<PaymentCollection> existingPayments) {
        return Arrays.asList(PaymentCollection.builder().value(newPayment).build());
    }

    @Override
    public boolean accepts(Payment newPayment, List<PaymentCollection> existingPayments) {
        return Objects.isNull(existingPayments);
    }

}
