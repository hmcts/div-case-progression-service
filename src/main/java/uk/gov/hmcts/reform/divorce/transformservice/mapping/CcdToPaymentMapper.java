package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class CcdToPaymentMapper {

    public List<Payment> ccdToPaymentRef(Map<String, Object> caseData) {
        log.info("Payment record from CCD getPayments {}", caseData.get("Payments"));
        if (caseData.get("Payments") != null) {
            log.info("Payment record from CCD getPayments class {}",
                caseData.get("Payments").getClass().getCanonicalName());
        }

        if (caseData.get("Payments") != null) {
            ArrayNode payments = (ArrayNode) caseData.get("Payments");
            List<Payment> paymentList = new ArrayList<>();
            for (JsonNode node : payments) {
                JsonNode value = node.get("value");
                Payment payment = new Payment();
                payment.setPaymentReference(value.get("PaymentReference").textValue());
                payment.setPaymentStatus(value.get("PaymentStatus").textValue());
                paymentList.add(payment);
            }
            return paymentList;
        }

        return Collections.emptyList();
    }
}
