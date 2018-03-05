package uk.gov.hmcts.reform.divorce.transformservice.strategy.payments;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Collectors;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.PaymentCollection;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

public class PaymentContext {

    private static final List<PaymentStrategy> paymentStrategies = asList(new ExistingTransactionIdStrategy(),
            new NoExistingTransactionIdStrategy(), new NoExistingPaymentStrategy());

    public List<PaymentCollection> getListOfPayments(DivorceSession divorceSession) {
        return paymentStrategies.stream()
                .filter(strategy -> strategy.accepts(divorceSession.getPayment(), divorceSession.getExistingPayments()))
                .flatMap(paymentStrategy -> paymentStrategy
                        .getCurrentPaymentsList(divorceSession.getPayment(), divorceSession.getExistingPayments()).stream())
                .collect(Collectors.toList());
    }
}
