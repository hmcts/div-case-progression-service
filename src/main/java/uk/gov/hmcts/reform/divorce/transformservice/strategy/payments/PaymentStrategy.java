package uk.gov.hmcts.reform.divorce.transformservice.strategy.payments;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.PaymentCollection;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.util.List;

public interface PaymentStrategy {

    boolean accepts(Payment newPayment, List<PaymentCollection> existingPayments);

    List<PaymentCollection> getCurrentPaymentsList(Payment newPayment, List<PaymentCollection> existingPayments);
}
