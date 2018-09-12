package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class CcdToPaymentMapper {

    public List<Payment> ccdToPaymentsMap(String payments) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(payments);
        boolean array = jsonNode.isArray();

        JsonNode jsonNode1 = jsonNode.get(0);

        String paymentReference = jsonNode1.get("value").get("PaymentReference").textValue();

        return Collections.emptyList();
    }
}
