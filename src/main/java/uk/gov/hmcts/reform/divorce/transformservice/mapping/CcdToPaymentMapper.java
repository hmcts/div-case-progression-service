package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Component
public class CcdToPaymentMapper {

    private static final String PAYMENTS = "Payments";
    private static final String VALUE = "value";
    private static final String PAYMENT_REFERENCE = "PaymentReference";
    private static final String PAYMENT_STATUS = "PaymentStatus";

    public List<Payment> ccdToPaymentRef(Map<String, Object> caseData) {
        log.debug("Payment record from CCD getPayments {}", caseData.get("Payments"));

        Stream<Map<String, Object>> paymentMaps = Optional.ofNullable(caseData.get(PAYMENTS))
            .map(payment -> (List<Map<String, Object>>) payment)
            .orElse(Collections.EMPTY_LIST)
            .stream();

        return paymentMaps
            .map(mv -> Optional.ofNullable(mv.get(VALUE)))
            .filter(Optional::isPresent)
            .map(v -> (Map<String, Object>) v.get())
            .map(this::toPayment)
            .collect(Collectors.toList());
    }

    private Payment toPayment(Map<String, Object> mapPayment) {
        Payment payment = new Payment();
        Optional.ofNullable((mapPayment).get(PAYMENT_REFERENCE))
            .ifPresent(pr -> payment.setPaymentReference((String) pr));
        Optional.ofNullable((mapPayment).get(PAYMENT_STATUS))
            .ifPresent(ps -> payment.setPaymentStatus((String) ps));
        return payment;
    }
}
