package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.util.*;
import java.util.stream.Stream;


@Slf4j
@Component
public class CcdToPaymentMapper {

    public List<Payment> ccdToPaymentRef(Map<String, Object> caseData) {
        log.debug("Payment record from CCD getPayments {}", caseData.get("Payments"));

        if (caseData.get("Payments") != null) {
            List<Map<String, Object>> payments = (ArrayList) caseData.get("Payments");

            List<Payment> paymentList = new ArrayList<>();
            for (Map<String, Object> node : payments) {
                Payment payment = new Payment();
                if (node.get("value") != null) {
                    Map<String, Object> value = (Map<String, Object>) node.get("value");
                    if (value.get("PaymentReference") != null) {
                        payment.setPaymentReference(value.get("PaymentReference").toString());
                    }
                    if (value.get("PaymentStatus") != null) {
                        payment.setPaymentStatus(value.get("PaymentStatus").toString());
                    }
                }
                paymentList.add(payment);
            }

            return paymentList;
        }

        return Collections.emptyList();
    }
}
