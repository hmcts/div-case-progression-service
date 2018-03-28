package uk.gov.hmcts.reform.divorce.transformservice.strategy.payments;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.PaymentCollection;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.util.List;
import java.util.Objects;

@Component
public class ExistingTransactionIdStrategy implements PaymentStrategy {

    @Override
    public List<PaymentCollection> getCurrentPaymentsList(Payment newPayment,
                                                          List<PaymentCollection> existingPayments) {
        existingPayments.removeIf(
            payment -> payment.getValue().getPaymentTransactionId().equals(newPayment.getPaymentTransactionId()));

        existingPayments.add(PaymentCollection.builder().value(newPayment).build());

        return existingPayments;
    }

    @Override
    public boolean accepts(Payment newPayment, List<PaymentCollection> existingPayments) {
        return Objects.nonNull(existingPayments) && existingPayments.stream()
            .anyMatch(
                payment -> payment.getValue().getPaymentTransactionId().equals(newPayment.getPaymentTransactionId()));
    }

}
