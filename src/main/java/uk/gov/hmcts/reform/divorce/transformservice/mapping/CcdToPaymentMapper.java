package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class CcdToPaymentMapper {

    public List<Payment> ccdTpPaymentRef(Map<String, Object> caseData) {
        log.info("Payment record from CCD getPayments {}", caseData.get("Payments"));
        List<Payment> paymentList = Collections.emptyList();
        if (caseData.get("Payments") != null) {
            JsonNode payments = (JsonNode) caseData.get("Payments");
            log.info("Payment record from CCD Payments {}", payments);
            if (payments.isArray()) {
                paymentList = new ArrayList<>();
                for (JsonNode node : payments) {
                    JsonNode value = node.get("value");
                    log.info("Payment record from CCD value {}", value);
                    Payment payment = new Payment();
                    payment.setPaymentReference(value.get("PaymentReference").textValue());
                    payment.setPaymentStatus(value.get("PaymentStatus").textValue());
                    paymentList.add(payment);
                }
            }
        }
        return paymentList;
    }
}
