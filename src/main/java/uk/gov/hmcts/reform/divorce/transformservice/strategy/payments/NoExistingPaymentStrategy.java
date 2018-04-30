package uk.gov.hmcts.reform.divorce.transformservice.strategy.payments;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.PaymentCollection;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class NoExistingPaymentStrategy implements PaymentStrategy {

    @Override
    public List<PaymentCollection> getCurrentPaymentsList(Payment newPayment,
                                                          List<PaymentCollection> existingPayments) {
        return Collections.singletonList(PaymentCollection.builder().value(newPayment).build());
    }

    @Override
    public boolean accepts(Payment newPayment, List<PaymentCollection> existingPayments) {
        return Objects.isNull(existingPayments);
    }

}
