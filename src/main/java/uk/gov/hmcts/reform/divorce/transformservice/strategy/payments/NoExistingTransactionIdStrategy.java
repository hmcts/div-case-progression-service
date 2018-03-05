package uk.gov.hmcts.reform.divorce.transformservice.strategy.payments;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.PaymentCollection;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

@Component
public class NoExistingTransactionIdStrategy implements PaymentStrategy {

    @Override
    public List<PaymentCollection> getCurrentPaymentsList(Payment newPayment, List<PaymentCollection> existingPayments) {
        existingPayments.add(PaymentCollection.builder().value(newPayment).build());

        return existingPayments;
    }

    @Override
    public boolean accepts(Payment newPayment, List<PaymentCollection> existingPayments) {
        return Objects.nonNull(existingPayments) && existingPayments.stream()
                .noneMatch(payment -> payment.getValue().getPaymentTransactionId().equals(newPayment.getPaymentTransactionId()));
    }

}
